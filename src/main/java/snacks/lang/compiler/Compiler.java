package snacks.lang.compiler;

import me.qmx.jitescript.AnnotationArrayValue;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.VisibleAnnotation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.Errorize;
import snacks.lang.Infix;
import snacks.lang.Invokable;
import snacks.lang.JavaUtils;
import snacks.lang.MatchException;
import snacks.lang.Operator;
import snacks.lang.Prefix;
import snacks.lang.Snack;
import snacks.lang.SnackDefinition;
import snacks.lang.SnackType;
import snacks.lang.SnacksList;
import snacks.lang.SnacksRegistry;
import snacks.lang.Symbol;
import snacks.lang.Type;
import snacks.lang.Type.AlgebraicType;
import snacks.lang.Type.FunctionType;
import snacks.lang.Type.RecordType;
import snacks.lang.Type.RecordType.Property;
import snacks.lang.Type.RecursiveType;
import snacks.lang.TypeGenerator;
import snacks.lang.Unit;
import snacks.lang.ast.Access;
import snacks.lang.ast.Apply;
import snacks.lang.ast.Assign;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Begin;
import snacks.lang.ast.BooleanConstant;
import snacks.lang.ast.Break;
import snacks.lang.ast.CharacterConstant;
import snacks.lang.ast.Closure;
import snacks.lang.ast.ClosureLocator;
import snacks.lang.ast.Continue;
import snacks.lang.ast.DeclarationLocator;
import snacks.lang.ast.DeclaredConstant;
import snacks.lang.ast.DeclaredConstructor;
import snacks.lang.ast.DeclaredExpression;
import snacks.lang.ast.DeclaredRecord;
import snacks.lang.ast.DeclaredType;
import snacks.lang.ast.DoubleConstant;
import snacks.lang.ast.Embrace;
import snacks.lang.ast.Exceptional;
import snacks.lang.ast.ExpressionConstant;
import snacks.lang.ast.Function;
import snacks.lang.ast.FunctionClosure;
import snacks.lang.ast.Generator;
import snacks.lang.ast.GuardCase;
import snacks.lang.ast.GuardCases;
import snacks.lang.ast.Hurl;
import snacks.lang.ast.Initializer;
import snacks.lang.ast.IntegerConstant;
import snacks.lang.ast.Locator;
import snacks.lang.ast.LogicalAnd;
import snacks.lang.ast.LogicalOr;
import snacks.lang.ast.Loop;
import snacks.lang.ast.MatchConstant;
import snacks.lang.ast.MatchConstructor;
import snacks.lang.ast.NamedNode;
import snacks.lang.ast.Nop;
import snacks.lang.ast.PatternCase;
import snacks.lang.ast.PatternCases;
import snacks.lang.ast.Reducer;
import snacks.lang.ast.Reference;
import snacks.lang.ast.ReferencesEqual;
import snacks.lang.ast.Result;
import snacks.lang.ast.Sequence;
import snacks.lang.ast.StringConstant;
import snacks.lang.ast.SymbolConstant;
import snacks.lang.ast.TupleInitializer;
import snacks.lang.ast.UndefinedSymbolException;
import snacks.lang.ast.UnitConstant;
import snacks.lang.ast.VariableDeclaration;
import snacks.lang.ast.VariableLocator;
import snacks.lang.ast.VoidFunction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.ci;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.params;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.apache.commons.lang.StringUtils.join;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static snacks.lang.JavaUtils.javaGetter;
import static snacks.lang.JavaUtils.javaName;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_APPLY;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_GET;
import static snacks.lang.Type.SimpleType;
import static snacks.lang.Type.UnionType;
import static snacks.lang.Type.VariableType;
import static snacks.lang.Types.isInvokable;

public class Compiler implements Generator, TypeGenerator, Reducer {

    private static final Pattern tuplePattern = Pattern.compile("^_\\d+$");

    private final SnacksRegistry registry;
    private final List<JiteClass> acceptedClasses;
    private final Deque<ClassBuilder> builders;
    private final Map<Locator, NamedNode> declarations;
    private final List<JiteClass> childClasses;
    private final ArrayDeque<LabelNode> patternScopes;
    private String parentClass;

    public Compiler(SnacksRegistry registry) {
        this.registry = registry;
        this.acceptedClasses = new ArrayList<>();
        this.builders = new ArrayDeque<>();
        this.declarations = new HashMap<>();
        this.childClasses = new ArrayList<>();
        this.patternScopes = new ArrayDeque<>();
    }

    public List<SnackDefinition> compile(Collection<NamedNode> declarations) {
        this.declarations.clear();
        for (NamedNode declaration : declarations) {
            this.declarations.put(declaration.locator(), declaration);
        }
        for (NamedNode declaration : declarations) {
            generate(declaration);
        }
        List<SnackDefinition> definitions = new ArrayList<>();
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
        block.newobj(p(AlgebraicType.class));
        block.dup();
        block.ldc(type.getName().getValue());
        generateTypes(type.getArguments());
        generateTypes(type.getOptions());
        block.invokespecial(p(AlgebraicType.class), "<init>", sig(void.class, String.class, Iterable.class, Iterable.class));
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
    public void generateBegin(Begin node) {
        CodeBlock block = block();
        EmbraceScope embrace = currentEmbrace();
        block.label(embrace.getStart());
        generate(node.getBody());
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
    public void generateCharacterConstant(CharacterConstant node) {
        CodeBlock block = block();
        block.ldc(node.getValue());
        block.invokestatic(p(Character.class), "valueOf", sig(Character.class, char.class));
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
        String className = javaClass(locator.getName());
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
        Class<?> clazz = registry.classOf(locator.getName(), locator.getKind());
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
        block().invokestatic(className, "instance", sig(Object.class));
    }

    @Override
    public void generateDeclaredConstant(final DeclaredConstant node) {
        JiteClass jiteClass = new JiteClass(node.getJavaClass(), getSuperClass(), array(interfacesFor(node.getType())));
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        snack.value("name", node.getSimpleName());
        snack.enumValue("kind", TYPE);
        jiteClass.addAnnotation(snack);
        jiteClass.setAccess(ACC_PUBLIC | ACC_STATIC | ACC_FINAL);
        jiteClass.defineDefaultConstructor();
        if (hasParent()) {
            childClasses.add(jiteClass);
        }
        builders.push(new ClassBuilder(jiteClass));
        defineType(node.getType());
        jiteClass.defineMethod("toString", ACC_PUBLIC, sig(String.class), new CodeBlock() {{
            ldc(node.getSimpleName());
            areturn();
        }});
        acceptClass();
    }

    @Override
    public void generateDeclaredConstructor(DeclaredConstructor node) {
        beginClass(node, node.getSimpleName() + "Constructor", interfacesFor(node.getType()));
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
        final JiteClass jiteClass = beginSubType(node, node.getType());
        List<String> types = new ArrayList<>();
        for (Property property : properties) {
            types.add(propertyClass(property));
        }
        jiteClass.defineMethod("<init>", ACC_PUBLIC, "(L" + join(types, ";L") + ";)V", new CodeBlock() {{
            aload(0);
            invokespecial(getSuperClass(), "<init>", sig(void.class));
            int arg = 1;
            for (Property property : properties) {
                String type = propertyClass(property);
                aload(0);
                aload(arg++);
                putfield(jiteClass.getClassName(), javaName(property.getName()), "L" + type + ";");
            }
            voidreturn();
        }});
        generateProperties(node);
        generateEquals(node);
        generateHashCode(node);
        generateToString(node);
        acceptClass();
    }

    private void generateHashCode(DeclaredRecord node) {
        final List<Property> properties = node.getProperties();
        final JiteClass jiteClass = jiteClass();
        jiteClass.defineMethod("hashCode", ACC_PUBLIC, sig(int.class), new CodeBlock() {{
            ldc(properties.size());
            anewarray(p(Object.class));
            int i = 0;
            for (Property property : properties) {
                dup();
                ldc(i++);
                aload(0);
                getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + propertyClass(property) + ";");
                aastore();
            }
            invokestatic(p(Objects.class), "hash", sig(int.class, Object[].class));
            ireturn();
        }});
    }

    private void generateEquals(DeclaredRecord node) {
        final List<Property> properties = node.getProperties();
        final JiteClass jiteClass = jiteClass();
        jiteClass.defineMethod("equals", ACC_PUBLIC, sig(boolean.class, Object.class), new CodeBlock() {{
            LabelNode instanceOf = new LabelNode();
            LabelNode end = new LabelNode();
            // references equal
            aload(0);
            aload(1);
            if_acmpne(instanceOf);
            ldc(true);
            ireturn();

            // references same type
            label(instanceOf);
            aload(1);
            instance_of(jiteClass.getClassName());
            ifne(end);
            aload(1);
            checkcast(jiteClass.getClassName());
            astore(2);
            newobj(p(EqualsBuilder.class));
            dup();
            invokespecial(p(EqualsBuilder.class), "<init>", sig(void.class));
            for (Property property : properties) {
                aload(0);
                getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + propertyClass(property) + ";");
                aload(2);
                getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + propertyClass(property) + ";");
                invokevirtual(p(EqualsBuilder.class), "append", sig(EqualsBuilder.class, Object.class, Object.class));
            }
            invokevirtual(p(EqualsBuilder.class), "isEquals", sig(boolean.class));
            ireturn();

            // no equality
            label(end);
            ldc(false);
            ireturn();
        }});
    }

    @Override
    public void generateDeclaredType(DeclaredType node) {
        List<NamedNode> variants = node.getVariants();
        if (variants.size() == 1 && Objects.equals(variants.get(0).getQualifiedName(), node.getQualifiedName())) {
            generate(variants.get(0));
        } else {
            beginType(node);
            parentClass = jiteClass().getClassName();
            JiteClass jiteClass = acceptClass();
            for (NamedNode variant : node.getVariants()) {
                generate(variant);
            }
            for (JiteClass childClass : childClasses) {
                jiteClass.addChildClass(childClass);
            }
            parentClass = null;
            childClasses.clear();
        }
    }

    @Override
    public void generateDoubleConstant(DoubleConstant node) {
        CodeBlock block = block();
        block.ldc(node.getValue());
        block.invokestatic(p(Double.class), "valueOf", sig(Double.class, double.class));
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
        generateApply(node.getVariable(), node.getBody());
    }

    @Override
    public void generateFunctionClosure(FunctionClosure node) {
        defineClosureFields(node.getEnvironment());
        defineClosureConstructor(node.getEnvironment());
        generateApply(node.getVariable(), node.getBody());
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
        block.invokestatic(p(Errorize.class), "instance", sig(Object.class));
        generate(node.getBody());
        block.invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP_APPLY);
        block.checkcast(p(Throwable.class));
        block.athrow();
    }

    @Override
    public void generateInitializer(Initializer node) {
        CodeBlock block = block();
        String className = node.getType().getJavaName().replace('.', '/');
        block.newobj(className);
        block.dup();
        if (node.getType() instanceof RecordType) {
            Iterator<Property> properties = ((RecordType) node.getType()).getProperties().iterator();
            List<AstNode> arguments = node.getArguments();
            String[] types = new String[arguments.size()];
            for (int i = 0; properties.hasNext(); i++) {
                types[i] = propertyClass(properties.next());
                generate(arguments.get(i));
                block.checkcast(types[i]);
            }
            block.invokespecial(className, "<init>", "(L" + join(types, ";L") + ";)V");
        } else {
            block.invokespecial(className, "<init>", sig(void.class));
        }
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
    public void generateMatchConstant(MatchConstant node) {
        CodeBlock block = block();
        generate(node.getReference());
        generate(node.getConstant());
        block.if_acmpne(nextPattern());
    }

    @Override
    public void generateMatchConstructor(MatchConstructor node) {
        CodeBlock block = block();
        generate(node.getReference());
        block.instance_of(classOf(node.getReference().getType()));
        block.iffalse(nextPattern());
        for (AstNode parameter : node.getParameters()) {
            generate(parameter);
        }
    }

    @Override
    public void generateNop(Nop node) {
        block().aconst_null();
    }

    @Override
    public void generatePatternCase(PatternCase node) {
        enterPattern();
        for (AstNode matcher : node.getMatchers()) {
            generate(matcher);
        }
        generate(node.getBody());
        leavePattern();
    }

    @Override
    public void generatePatternCases(PatternCases node) {
        CodeBlock block = block();
        for (PatternCase pattern : node.getPatterns()) {
            generate(pattern);
        }
        block.newobj(p(MatchException.class));
        block.dup();
        block.ldc("Failed to match pattern");
        block.invokespecial(p(MatchException.class), "<init>", sig(void.class, String.class));
        block.athrow();
    }

    @Override
    public void generateRecordType(RecordType type) {
        CodeBlock block = block();
        block.newobj(p(RecordType.class));
        block.dup();
        block.ldc(type.getName().getValue());
        generateTypes(type.getArguments());
        SnacksList<Property> properties = type.getProperties();
        Iterator<Property> propertyIterator = properties.iterator();
        block.ldc(properties.size());
        block.anewarray(p(Property.class));
        for (int i = 0; i < properties.size(); i++) {
            Property property = propertyIterator.next();
            block.dup();
            block.ldc(i);
            block.newobj(p(Property.class));
            block.dup();
            block.ldc(property.getName().getValue());
            generate(property.getType());
            block.invokespecial(p(Property.class), "<init>", sig(void.class, String.class, Type.class));
            block.aastore();
        }
        block.invokestatic(p(Arrays.class), "asList", sig(List.class, Object[].class));
        block.invokespecial(p(RecordType.class), "<init>", sig(void.class, String.class, Iterable.class, Iterable.class));
    }

    @Override
    public void generateRecursiveType(RecursiveType type) {
        CodeBlock block = block();
        block.newobj(p(RecursiveType.class));
        block.dup();
        block.ldc(type.getName().getValue());
        generateTypes(type.getArguments());
        block.invokespecial(p(RecursiveType.class), "<init>", sig(void.class, String.class, Iterable.class));
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
        block.ldc(type.getName().getValue());
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
        CodeBlock block = block();
        block.newobj(p(UnionType.class));
        block.dup();
        generateTypes(type.getTypes());
        block.invokespecial(p(UnionType.class), "<init>", sig(void.class, Iterable.class));
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
        block.ldc(type.getName().getValue());
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

    private CodeBlock acceptBlock() {
        return state().acceptBlock();
    }

    private JiteClass acceptClass() {
        JiteClass jiteClass = builders.pop().getJiteClass();
        acceptedClasses.add(jiteClass);
        return jiteClass;
    }

    private String[] array(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    private CodeBlock beginBlock() {
        return state().beginBlock();
    }

    private JiteClass beginClass(NamedNode node, List<String> interfaces) {
        return beginClass(node, node.getSimpleName(), interfaces);
    }

    private JiteClass beginClass(NamedNode node, String internalName, List<String> interfaces) {
        JiteClass jiteClass = new JiteClass(
            javaClass(node.getModule(), internalName),
            p(Object.class),
            array(interfaces)
        );
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        snack.value("name", node.getSimpleName());
        snack.enumValue("kind", node.getKind());
        jiteClass.addAnnotation(snack);
        builders.push(new ClassBuilder(jiteClass));
        defineType(node.getType());
        return jiteClass;
    }

    private JiteClass beginSubType(NamedNode node, Type type) {
        JiteClass jiteClass = new JiteClass(node.getJavaClass(), getSuperClass(), array(interfacesFor(type)));
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        snack.enumValue("kind", node.getKind());
        snack.value("name", node.getSimpleName());
        jiteClass.addAnnotation(snack);
        if (hasParent()) {
            childClasses.add(jiteClass);
        }
        builders.push(new ClassBuilder(jiteClass));
        defineType(type);
        jiteClass.setAccess(ACC_PUBLIC | ACC_STATIC | ACC_FINAL);
        return jiteClass;
    }

    private JiteClass beginType(NamedNode node) {
        JiteClass jiteClass = new JiteClass(node.getJavaClass(), p(Object.class), new String[0]);
        jiteClass.setAccess(ACC_PUBLIC | ACC_ABSTRACT);
        jiteClass.defineDefaultConstructor(ACC_PROTECTED);
        VisibleAnnotation snack = new VisibleAnnotation(Snack.class);
        AnnotationArrayValue parameters = snack.arrayValue("arguments");
        snack.value("name", node.getSimpleName());
        snack.enumValue("kind", TYPE);
        jiteClass.addAnnotation(snack);
        for (Type parameter : node.getType().getArguments()) {
            parameters.add(node.getQualifiedName() + '#' + parameter.getName());
        }
        builders.push(new ClassBuilder(jiteClass));
        return jiteClass;
    }

    private CodeBlock block() {
        return state().block();
    }

    private String classOf(Type type) {
        Class<?> clazz = registry.classOf(type.getName().getValue(), TYPE);
        if (clazz == null) {
            return javaName(type.getName()).replace('.', '/');
        } else {
            return p(clazz);
        }
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
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
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
        state().enterEmbrace(getVariable("#snacks#~exception"), node);
    }

    private void enterGuard() {
        state().enterGuard();
    }

    private LoopScope enterLoop() {
        return state().enterLoop();
    }

    private void enterPattern() {
        patternScopes.push(new LabelNode());
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

    private void generateApply(String variable, AstNode body) {
        CodeBlock block = beginBlock();
        getVariable(variable);
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

    private void generateProperties(final DeclaredRecord node) {
        final JiteClass jiteClass = jiteClass();
        for (final Property property : node.getProperties()) {
            final String type = propertyClass(property);
            jiteClass.defineField(javaName(property.getName()), ACC_PRIVATE | ACC_FINAL, "L" + type + ";", null);
            jiteClass.defineMethod(javaGetter(property.getName()), ACC_PUBLIC, "()L" + type + ";", new CodeBlock() {{
                aload(0);
                getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + type + ";");
                areturn();
            }});
        }
    }

    private void generateToString(final DeclaredRecord node) {
        final List<Property> properties = node.getProperties();
        final JiteClass jiteClass = jiteClass();
        jiteClass.defineMethod("toString", ACC_PUBLIC, sig(String.class), new CodeBlock() {{
            newobj(p(StringBuilder.class));
            dup();
            invokespecial(p(StringBuilder.class), "<init>", sig(void.class));
            if (isNamedTuple(node)) {
                ldc(node.getSimpleName() + "(");
                invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
                for (int i = 0; i < properties.size(); i++) {
                    if (i > 0) {
                        ldc(", ");
                        invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
                    }
                    Property property = properties.get(i);
                    aload(0);
                    getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + propertyClass(property) + ";");
                    invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, Object.class));
                }
                ldc(")");
            } else {
                ldc(node.getSimpleName() + "{");
                invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
                for (int i = 0; i < properties.size(); i++) {
                    Property property = properties.get(i);
                    if (i > 0) {
                        ldc(", " + property.getName().getValue() + "=");
                    } else {
                        ldc(property.getName().getValue() + "=");
                    }
                    invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
                    aload(0);
                    getfield(jiteClass.getClassName(), javaName(property.getName()), "L" + propertyClass(property) + ";");
                    invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, Object.class));
                }
                ldc("}");
            }
            invokevirtual(p(StringBuilder.class), "append", sig(StringBuilder.class, String.class));
            invokevirtual(p(StringBuilder.class), "toString", sig(String.class));
            areturn();
        }});
    }

    private void generateTypes(SnacksList<Type> types) {
        CodeBlock block = block();
        block.ldc(types.size());
        block.anewarray(p(Type.class));
        int i = 0;
        for (Type type : types) {
            block.dup();
            block.ldc(i++);
            generate(type);
            block.aastore();
        }
        block.invokestatic(p(Arrays.class), "asList", sig(List.class, Object[].class));
    }

    private String getSuperClass() {
        return hasParent() ? parentClass() : p(Object.class);
    }

    private int getVariable(String name) {
        return state().getVariable(name);
    }

    private boolean hasParent() {
        return parentClass != null;
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

    private boolean isNamedTuple(DeclaredRecord record) {
        boolean namedTuple = true;
        for (Property property : record.getProperties()) {
            if (!tuplePattern.matcher(property.getName().getValue()).find()) {
                namedTuple = false;
                break;
            }
        }
        return namedTuple;
    }

    private String javaClass(String qualifiedName) {
        String module = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        return javaClass(module, name);
    }

    private String javaClass(String module, String name) {
        return JavaUtils.javaClass(module, name).replace('.', '/');
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

    private void leavePattern() {
        CodeBlock block = block();
        block.areturn();
        block.label(patternScopes.pop());
    }

    private void loadVariable(String name) {
        CodeBlock block = block();
        if (isField(name)) {
            block.aload(0);
            block.getfield(jiteClass().getClassName(), name, ci(Object.class));
        } else {
            int variable = getVariable(name);
            block.aload(variable);
        }
    }

    private LabelNode nextPattern() {
        return patternScopes.peek();
    }

    private String parentClass() {
        return parentClass;
    }

    private String propertyClass(Property property) {
        if (property.getType() instanceof VariableType) {
            return p(Object.class);
        } else {
            return classOf(property.getType());
        }
    }

    private ClassBuilder state() {
        return builders.peek();
    }
}
