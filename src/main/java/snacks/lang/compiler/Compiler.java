package snacks.lang.compiler;

import static me.qmx.jitescript.util.CodegenUtils.*;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.objectweb.asm.Opcodes.*;
import static snacks.lang.JavaUtils.javaName;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_APPLY;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_GET;
import static snacks.lang.type.Types.isFunction;
import static snacks.lang.type.Types.isInvokable;
import static snacks.lang.type.Types.isType;

import java.util.*;
import me.qmx.jitescript.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.*;
import snacks.lang.ast.*;
import snacks.lang.type.*;
import snacks.lang.type.RecordType.Property;

public class Compiler implements Generator, TypeGenerator, Reducer {

    private final SnacksRegistry registry;
    private final List<JiteClass> acceptedClasses;
    private final Deque<ClassBuilder> builders;
    private final Map<Locator, NamedNode> declarations;

    public Compiler(SnacksRegistry registry) {
        this.registry = registry;
        this.acceptedClasses = new ArrayList<>();
        this.builders = new ArrayDeque<>();
        this.declarations = new HashMap<>();
    }

    public Set<SnackDefinition> compile(Collection<NamedNode> declarations) {
        this.declarations.clear();
        for (NamedNode declaration : declarations) {
            this.declarations.put(declaration.locator(), declaration);
        }
        for (NamedNode declaration : declarations) {
            generate(declaration);
        }
        Set<SnackDefinition> definitions = new HashSet<>();
        for (JiteClass jiteClass : acceptedClasses) {
            byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
            definitions.add(new SnackDefinition(c(jiteClass.getClassName()), bytes));
        }
        return definitions;
    }

    @Override
    public void generate(AstNode node) {
        node.generate(this);
    }

    @Override
    public void generateAccess(Access node) {
        CodeBlock block = block();
        generate(node.getExpression());
        block.ldc(node.getProperty());
        block.invokedynamic("get", sig(Object.class, Object.class, String.class), BOOTSTRAP_GET);
    }

    @Override
    public void generateAlgebraicType(AlgebraicType type) {
        CodeBlock block = block();
        Set<Type> types = type.getTypes();
        block.newobj(p(AlgebraicType.class));
        block.dup();
        block.ldc(type.getName());
        block.ldc(types.size());
        block.anewarray(p(Type.class));
        int i = 0;
        for (Type t : types) {
            block.dup();
            generate(t);
            block.ldc(i++);
            block.aastore();
        }
        block.invokestatic(p(Arrays.class), "asList", sig(List.class, Object[].class));
        block.invokespecial(p(AlgebraicType.class), "<init>", sig(void.class, String.class, Collection.class));
    }

    @Override
    public void generateApply(Apply node) {
        generate(node.getFunction());
        generate(node.getArgument());
        block().invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP_APPLY);
    }

    @Override
    public void generateAssign(Assign node) {
        generate(node.getRight());
        block().dup();
        node.getLeft().reduce(this);
    }

    @Override
    public void generateBegin(Begin begin) {
        CodeBlock block = block();
        EmbraceScope embrace = currentEmbrace();
        block.label(embrace.getStart());
        generate(begin.getBody());
        block.label(embrace.getEnd());
        embrace.generateEnsure(this);
        block.go_to(embrace.getExit());
    }

    @Override
    public void generateBooleanConstant(BooleanConstant node) {
        CodeBlock block = block();
        block.ldc(node.getValue());
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    }

    @Override
    public void generateBreak(Break node) {
        currentLoop().exit();
    }

    @Override
    public void generateClosure(Closure node) {
        defineClosureFields(node.getEnvironment());
        defineClosureConstructor(node.getEnvironment());
        generateInvoke(node.getBody());
    }

    @Override
    public void generateClosureLocator(ClosureLocator locator) {
        CodeBlock block = block();
        String className = javaClass(locator.getModule(), locator.getName());
        block.newobj(className);
        block.dup();
        for (String variable : locator.getEnvironment()) {
            loadVariable(variable);
        }
        block.invokespecial(className, "<init>", sig(params(void.class, Object.class, locator.getEnvironment().size())));
    }

    @Override
    public void generateContinue(Continue node) {
        currentLoop().next();
    }

    @Override
    public void generateDeclarationLocator(DeclarationLocator locator) {
        Class<?> clazz = registry.classOf(locator.getQualifiedName(), locator.getKind());
        String className;
        if (clazz == null) {
            if (declarations.containsKey(locator)) {
                if (declarations.get(locator) instanceof DeclaredConstructor) {
                    className = locator.getJavaName() + "Constructor";
                } else {
                    className = locator.getJavaName();
                }
            } else {
                throw new UndefinedSymbolException("Cannot find " + locator);
            }
        } else {
            className = p(clazz);
        }
        if (isFunction(typeOf(locator))) {
            block().invokestatic(className, "instance", "()L" + className + ";");
        } else {
            block().invokestatic(className, "instance", sig(Object.class));
        }
    }

    @Override
    public void generateDeclaredConstructor(DeclaredConstructor node) {
        beginClass(node, node.getName(), interfacesFor(node.getType()));
        generate(node.getBody());
        acceptClass();
    }

    @Override
    public void generateDeclaredExpression(DeclaredExpression node) {
        JiteClass jiteClass = beginClass(node, interfacesFor(node.getType()));
        if (node.isOperator()) {
            Operator operator = node.getOperator();
            if (operator.isPrefix()) {
                jiteClass.addAnnotation(new VisibleAnnotation(Prefix.class)
                    .value("precedence", operator.getPrecedence())
                );
            } else {
                jiteClass.addAnnotation(new VisibleAnnotation(Infix.class)
                    .enumValue("fixity", operator.getFixity())
                    .value("precedence", operator.getPrecedence())
                );
            }
        }
        generate(node.getBody());
        acceptClass();
    }

    @Override
    public void generateDeclaredRecord(final DeclaredRecord node) {
        final List<Property> properties = node.getProperties();
        final JiteClass jiteClass = beginSubType(node.getModule(), node.getName(), node.getType());
        for (final Property property : properties) {
            final Class<?> type = propertyType(property);
            jiteClass.defineField(javaName(property.getName()), ACC_PRIVATE | ACC_FINAL, ci(type), null);
            jiteClass.defineMethod(javaGetter(property.getName()), ACC_PUBLIC, sig(type), new CodeBlock() {{
                aload(0);
                getfield(jiteClass.getClassName(), javaName(property.getName()), ci(type));
                areturn();
            }});
        }
        Class<?>[] types = new Class<?>[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            types[i] = propertyType(properties.get(i));
        }
        jiteClass.defineMethod("<init>", ACC_PUBLIC, sig(void.class, params(types)), new CodeBlock() {{
            aload(0);
            invokespecial(p(Object.class), "<init>", sig(void.class));
            int arg = 1;
            for (Property property : properties) {
                Class<?> type = propertyType(property);
                aload(0);
                aload(arg++);
                putfield(jiteClass.getClassName(), javaName(property.getName()), ci(type));
            }
            voidreturn();
        }});
        jiteClass.defineMethod("toString", ACC_PUBLIC, sig(String.class), new CodeBlock() {{
            newobj(p(StringBuilder.class));
            dup();
            invokespecial(p(StringBuilder.class), "<init>", sig(void.class));
            ldc(node.getName().substring(node.getName().lastIndexOf('.') + 1) + "{");
            invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
            for (int i = 0; i < properties.size(); i++) {
                Property property = properties.get(i);
                if (i > 0) {
                    ldc(", " + property.getName() + "=");
                } else {
                    ldc(property.getName() + "=");
                }
                invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
                aload(0);
                getfield(jiteClass.getClassName(), javaName(property.getName()), ci(propertyType(property)));
                invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, Object.class));
            }
            ldc("}");
            invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
            invokevirtual(p(StringBuilder.class), "toString", sig(String.class));
            areturn();
        }});
    }

    @Override
    public void generateDeclaredType(DeclaredType node) {
        beginType(node.getModule(), node.getName(), node.getType());
        for (AstNode variant : node.getVariants()) {
            generate(variant);
        }
        acceptClass();
    }

    @Override
    public void generateDoubleConstant(DoubleConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void generateEmbrace(Embrace node) {
        EmbraceScope scope = currentEmbrace();
        CodeBlock block = block();
        LabelNode beginCatch = new LabelNode();
        LabelNode endCatch = new LabelNode();
        block.trycatch(scope.getStart(), scope.getEnd(), beginCatch, node.getException().replace('.', '/'));
        block.trycatch(beginCatch, endCatch, scope.getError(), null);
        block.label(beginCatch);
        block.astore(getVariable(node.getVariable()));
        generate(node.getBody());
        block.label(endCatch);
        scope.generateEnsure(this);
        block.go_to(scope.getExit());
    }

    @Override
    public void generateExceptional(Exceptional node) {
        enterEmbrace(node);
        generate(node.getBegin());
        for (AstNode embrace : node.getEmbraces()) {
            generate(embrace);
        }
        leaveEmbrace();
    }

    @Override
    public void generateExpressionConstant(ExpressionConstant node) {
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        JiteClass jiteClass = jiteClass();
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, ci(Object.class), null);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.ifnonnull(returnValue);
        generate(node.getValue());
        block.putstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
    }

    @Override
    public void generateFunction(Function node) {
        defineFunctionInitializer();
        generateApply(node.getBody());
    }

    @Override
    public void generateFunctionClosure(FunctionClosure node) {
        defineClosureFields(node.getEnvironment());
        defineClosureConstructor(node.getEnvironment());
        generateApply(node.getBody());
    }

    @Override
    public void generateFunctionType(FunctionType type) {
        CodeBlock block = block();
        block.newobj(p(FunctionType.class));
        block.dup();
        generate(type.getArgument());
        generate(type.getResult());
        block.invokespecial(p(FunctionType.class), "<init>", sig(void.class, Type.class, Type.class));
    }

    @Override
    public void generateGuardCase(GuardCase node) {
        CodeBlock block = block();
        LabelNode skipLabel = new LabelNode();
        generate(node.getCondition());
        block.ldc(true);
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
        block.if_acmpne(skipLabel);
        generate(node.getExpression());
        exitGuard();
        block.label(skipLabel);
    }

    @Override
    public void generateGuardCases(GuardCases node) {
        enterGuard();
        for (AstNode guard : node.getCases()) {
            generate(guard);
        }
        leaveGuard();
    }

    @Override
    public void generateHurl(Hurl node) {
        CodeBlock block = block();
        block.invokestatic(p(Errorize.class), "instance", sig(Errorize.class));
        generate(node.getBody());
        block.invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP_APPLY);
        block.checkcast(p(Throwable.class));
        block.athrow();
    }

    @Override
    public void generateInitializer(Initializer node) {
        CodeBlock block = block();
        String className = javaName(node.getType().getName()).replace('.', '/');
        block.newobj(className);
        block.dup();
        List<Property> properties = ((RecordType) node.getType()).getProperties();
        List<AstNode> arguments = node.getArguments();
        Class<?>[] types = new Class<?>[arguments.size()];
        for (int i = 0; i < properties.size(); i++) {
            types[i] = propertyType(properties.get(i));
            generate(arguments.get(i));
            block.checkcast(p(types[i]));
        }
        block.invokespecial(className, "<init>", sig(void.class, types));
    }

    @Override
    public void generateIntegerConstant(IntegerConstant node) {
        CodeBlock block = block();
        block().ldc(node.getValue());
        block.invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
    }

    @Override
    public void generateLogicalAnd(LogicalAnd node) {
        CodeBlock block = block();
        LabelNode skipLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        generate(node.getLeft());
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.if_acmpne(skipLabel);
        generate(node.getRight());
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.if_acmpne(skipLabel);
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.go_to(endLabel);
        block.label(skipLabel);
        block.getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
        block.label(endLabel);
    }

    @Override
    public void generateLogicalOr(LogicalOr node) {
        CodeBlock block = block();
        LabelNode skipLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        generate(node.getLeft());
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.if_acmpeq(skipLabel);
        generate(node.getRight());
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.if_acmpeq(skipLabel);
        block.getstatic(p(Boolean.class), "FALSE", ci(Boolean.class));
        block.go_to(endLabel);
        block.label(skipLabel);
        block.getstatic(p(Boolean.class), "TRUE", ci(Boolean.class));
        block.label(endLabel);
    }

    @Override
    public void generateLoop(Loop node) {
        LoopScope loop = enterLoop();
        generate(node.getCondition());
        loop.testCondition();
        generate(node.getBody());
        leaveLoop();
    }

    @Override
    public void generateNop(Nop nop) {
        block().aconst_null();
    }

    @Override
    public void generateParameterizedType(ParameterizedType type) {
        List<Type> parameters = type.getParameters();
        CodeBlock block = block();
        block.newobj(p(ParameterizedType.class));
        block.dup();
        block.ldc(type.getName());
        block.ldc(parameters.size());
        block.anewarray(p(Type.class));
        for (int i = 0; i < parameters.size(); i++) {
            block.dup();
            generate(parameters.get(i));
            block.ldc(i);
            block.aastore();
        }
        block.invokespecial(p(ParameterizedType.class), "<init>", sig(void.class, String.class, Type[].class));
    }

    @Override
    public void generateRecordType(RecordType type) {
        CodeBlock block = block();
        block.newobj(p(RecordType.class));
        block.dup();
        block.ldc(type.getName());
        List<Property> parameters = type.getProperties();
        block.ldc(parameters.size());
        block.anewarray(p(Property.class));
        for (int i = 0; i < parameters.size(); i++) {
            block.dup();
            block.ldc(i);
            block.newobj(p(Property.class));
            block.dup();
            block.ldc(parameters.get(i).getName());
            generate(parameters.get(i).getType());
            block.invokespecial(p(Property.class), "<init>", sig(void.class, String.class, Type.class));
            block.aastore();
        }
        block.invokestatic(p(Arrays.class), "asList", sig(List.class, Object[].class));
        block.invokespecial(p(RecordType.class), "<init>", sig(void.class, String.class, Collection.class));
    }

    @Override
    public void generateReference(Reference node) {
        generate(node.getLocator());
    }

    @Override
    public void generateReferencesEqual(ReferencesEqual node) {
        generate(node.getLeft());
        generate(node.getRight());
        LabelNode skipLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        CodeBlock block = block();
        block.if_acmpne(skipLabel);
        block.ldc(true);
        block.go_to(endLabel);
        block.label(skipLabel);
        block.ldc(false);
        block.label(endLabel);
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    }

    @Override
    public void generateResult(Result node) {
        generate(node.getValue());
        block().areturn();
    }

    @Override
    public void generateSequence(Sequence node) {
        Iterator<AstNode> elements = node.getElements().iterator();
        CodeBlock block = block();
        generate(elements.next());
        while (elements.hasNext()) {
            block.pop();
            generate(elements.next());
        }
    }

    @Override
    public void generateSimpleType(SimpleType type) {
        CodeBlock block = block();
        block.newobj(p(SimpleType.class));
        block.dup();
        block.ldc(type.getName());
        block.invokespecial(p(SimpleType.class), "<init>", sig(void.class, String.class));
    }

    @Override
    public void generateStringConstant(StringConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void generateSymbol(SymbolConstant node) {
        CodeBlock block = block();
        block.ldc(node.getName());
        block.invokestatic(p(Symbol.class), "valueOf", sig(Symbol.class, String.class));
    }

    @Override
    public void generateTupleInitializer(TupleInitializer node) {
        try {
            CodeBlock block = block();
            List<AstNode> elements = node.getElements();
            String tupleClass = p(Class.forName("snacks.lang.Tuple" + elements.size()));
            block.newobj(tupleClass);
            block.dup();
            for (AstNode element : elements) {
                generate(element);
            }
            block.invokespecial(tupleClass, "<init>", sig(void.class, params(Object.class, elements.size())));
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void generateUnionType(UnionType type) {
        List<Type> members = new ArrayList<>(type.getTypes());
        CodeBlock block = block();
        block.newobj(p(UnionType.class));
        block.dup();
        block.ldc(members.size());
        block.anewarray(p(Type.class));
        for (int i = 0; i < members.size(); i++) {
            block.dup();
            block.ldc(i);
            generate(members.get(i));
            block.aastore();
        }
        block.invokestatic(p(Arrays.class), "asList", sig(List.class, Object[].class));
        block.invokespecial(p(UnionType.class), "<init>", sig(void.class, Collection.class));
    }

    @Override
    public void generateUnitConstant(UnitConstant node) {
        block().getstatic(p(Unit.class), "UNIT", ci(Unit.class));
    }

    @Override
    public void generateVariableDeclaration(VariableDeclaration node) {
        getVariable(node.getName());
        block().aconst_null();
    }

    @Override
    public void generateVariableLocator(VariableLocator locator) {
        loadVariable(locator.getName());
    }

    @Override
    public void generateVariableType(VariableType type) {
        CodeBlock block = block();
        block.newobj(p(VariableType.class));
        block.dup();
        block.ldc(type.getName());
        block.invokespecial(p(VariableType.class), "<init>", sig(void.class, String.class));
    }

    @Override
    public void generateVoidFunction(VoidFunction node) {
        defineFunctionInitializer();
        generateInvoke(node.getBody());
    }

    @Override
    public void reduceReference(Reference node) {
        node.getLocator().reduce(this);
    }

    @Override
    public void reduceVariableDeclaration(VariableDeclaration node) {
        block().astore(getVariable(node.getName()));
    }

    @Override
    public void reduceVariableLocator(VariableLocator node) {
        block().astore(getVariable(node.getName()));
    }

    @Override
    public void visitCharacterConstant(CharacterConstant node) {
        CodeBlock block = block();
        block.ldc(node.getValue());
        block.invokestatic(p(Character.class), "valueOf", sig(Character.class, char.class));
    }

    private CodeBlock acceptBlock() {
        return state().acceptBlock();
    }

    private void acceptClass() {
        acceptedClasses.add(builders.pop().getJiteClass());
    }

    private CodeBlock beginBlock() {
        return state().beginBlock();
    }

    private JiteClass beginClass(NamedNode node, List<String> interfaces) {
        return beginClass(node, node.getName(), interfaces);
    }

    private JiteClass beginClass(NamedNode node, String internalName, List<String> interfaces) {
        JiteClass jiteClass = new JiteClass(
            javaClass(node.getModule(), internalName),
            p(Object.class),
            interfaces.toArray(new String[interfaces.size()])
        );
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        AnnotationArrayValue kind = snack.arrayValue("kind");
        snack.value("name", node.getName());
        if (isType(node.getType())) {
            kind.addEnum(TYPE);
        } else {
            kind.addEnum(EXPRESSION);
        }
        jiteClass.addAnnotation(snack);
        builders.push(new ClassBuilder(jiteClass));
        defineType(node.getType());
        return jiteClass;
    }

    private JiteClass beginSubType(String module, String name, Type type) {
        JiteClass parentClass = jiteClass();
        JiteClass jiteClass = new JiteClass(javaClass(module, name), p(Object.class), new String[0]);
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        AnnotationArrayValue kind = snack.arrayValue("kind");
        snack.value("name", name);
        if (isType(type)) {
            kind.addEnum(TYPE);
        } else {
            kind.addEnum(EXPRESSION);
        }
        jiteClass.addAnnotation(snack);
        builders.push(new ClassBuilder(jiteClass));
        defineType(type);
        jiteClass.setAccess(ACC_PUBLIC | ACC_STATIC | ACC_FINAL);
        parentClass.addChildClass(jiteClass);
        return jiteClass;
    }

    private JiteClass beginType(String module, String name, Type type) {
        JiteClass jiteClass = new JiteClass(javaClass(module, name), p(Object.class), new String[0]);
        jiteClass.setAccess(ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT);
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        AnnotationArrayValue parameters = snack.arrayValue("parameters");
        snack.value("name", name);
        snack.arrayValue("kind").addEnum(TYPE);
        jiteClass.addAnnotation(snack);
        if (type instanceof ParameterizedType) {
            for (Type parameter : ((ParameterizedType) type).getParameters()) {
                parameters.add(module + '.' + name + '#' + parameter.getName());
            }
        }
        builders.push(new ClassBuilder(jiteClass));
        return jiteClass;
    }

    private CodeBlock block() {
        return state().block();
    }

    private EmbraceScope currentEmbrace() {
        return state().currentEmbrace();
    }

    private LoopScope currentLoop() {
        return state().currentLoop();
    }

    private void defineClosureConstructor(List<String> environment) {
        String signature = sig(params(void.class, Object.class, environment.size()));
        CodeBlock block = beginBlock();
        block.aload(0);
        block.invokespecial(p(Object.class), "<init>", sig(void.class));
        int i = 1;
        for (String field : environment) {
            block.aload(0);
            block.aload(i++);
            block.putfield(jiteClass().getClassName(), field, ci(Object.class));
        }
        block.voidreturn();
        jiteClass().defineMethod("<init>", ACC_PUBLIC, signature, acceptBlock());
    }

    private void defineClosureFields(List<String> environment) {
        state().setFields(environment);
        JiteClass jiteClass = jiteClass();
        for (String field : environment) {
            jiteClass.defineField(field, ACC_PRIVATE | ACC_FINAL, ci(Object.class), null);
        }
    }

    private void defineFunctionInitializer() {
        JiteClass jiteClass = jiteClass();
        String className = "L" + jiteClass().getClassName() + ";";
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, className, null);
        block.getstatic(jiteClass.getClassName(), "instance", className);
        block.ifnonnull(returnValue);
        block.newobj(jiteClass.getClassName());
        block.dup();
        block.invokespecial(jiteClass.getClassName(), "<init>", sig(void.class));
        block.putstatic(jiteClass.getClassName(), "instance", className);
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", className);
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, "()" + className, acceptBlock());
        jiteClass.defineDefaultConstructor();
    }

    private void defineType(Type type) {
        JiteClass jiteClass = jiteClass();
        CodeBlock block = beginBlock();
        block.addAnnotation(new VisibleAnnotation(SnackType.class));
        generate(type);
        block.areturn();
        jiteClass.defineMethod("type", ACC_PUBLIC | ACC_STATIC, sig(Type.class), acceptBlock());
    }

    private void enterEmbrace(Exceptional node) {
        state().enterEmbrace(getVariable("$snacks$~exception"), node);
    }

    private void enterGuard() {
        state().enterGuard();
    }

    private LoopScope enterLoop() {
        return state().enterLoop();
    }

    private void exitGuard() {
        state().exitGuard();
    }

    private void generate(Type type) {
        type.generate(this);
    }

    private void generate(Locator locator) {
        locator.generate(this);
    }

    private void generateApply(AstNode body) {
        CodeBlock block = beginBlock();
        generate(body);
        if (!block.returns()) {
            block.areturn();
        }
        jiteClass().defineMethod("apply", ACC_PUBLIC, sig(Object.class, Object.class), acceptBlock());
    }

    private void generateInvoke(AstNode body) {
        final JiteClass jiteClass = jiteClass();
        CodeBlock block = beginBlock();
        generate(body);
        if (!block.returns()) {
            block.areturn();
        }
        jiteClass.defineMethod("apply", ACC_PUBLIC, sig(Object.class, Unit.class), acceptBlock());
        jiteClass.defineMethod("invoke", ACC_PUBLIC, sig(Object.class), new CodeBlock() {{
            aload(0);
            getstatic(p(Unit.class), "UNIT", ci(Unit.class));
            invokevirtual(jiteClass.getClassName(), "apply", sig(Object.class, Unit.class));
            areturn();
        }});
    }

    private int getVariable(String name) {
        return state().getVariable(name);
    }

    private List<String> interfacesFor(Type type) {
        List<String> interfaces = new ArrayList<>();
        if (isInvokable(type)) {
            interfaces.add(p(Invokable.class));
        }
        return interfaces;
    }

    private boolean isField(String name) {
        return state().isField(name);
    }

    private boolean isVariable(String name) {
        return state().isVariable(name);
    }

    private String javaClass(String module, String name) {
        return JavaUtils.javaClass(module, name).replace('.', '/');
    }

    private String javaGetter(String name) {
        return "get" + capitalize(javaName(name));
    }

    private JiteClass jiteClass() {
        return state().getJiteClass();
    }

    private void leaveEmbrace() {
        state().leaveEmbrace(this);
    }

    private void leaveGuard() {
        state().leaveGuard();
    }

    private void leaveLoop() {
        state().leaveLoop();
    }

    private void loadVariable(String name) {
        CodeBlock block = block();
        if (isField(name)) {
            block.aload(0);
            block.getfield(jiteClass().getClassName(), name, ci(Object.class));
        } else if (isVariable(name)) {
            block.aload(getVariable(name));
        } else {
            block.aload(1);
        }
    }

    private Class<?> propertyType(Property property) {
        return registry.classOf(property.getType().getName(), TYPE);
    }

    private ClassBuilder state() {
        return builders.peek();
    }

    private Type typeOf(DeclarationLocator locator) {
        Type type = registry.typeOf(locator.getQualifiedName(), locator.getKind());
        if (type == null) {
            if (!declarations.containsKey(locator)) {
                throw new CompileException("Unable to determine type of " + locator);
            }
            return declarations.get(locator).getType();
        }
        return type;
    }
}
