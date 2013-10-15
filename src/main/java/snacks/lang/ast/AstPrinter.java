package snacks.lang.ast;

import static java.lang.String.valueOf;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import snacks.lang.type.*;
import snacks.lang.type.RecordType.Property;
import snacks.lang.util.PrinterState;

public class AstPrinter implements TypePrinter {

    private final PrinterState state;

    public AstPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Set<AstNode> nodes) {
        for (AstNode node : nodes) {
            print(node);
        }
    }

    public void print(AstNode node) {
        state.begin(node);
        node.print(this);
        print("type:");
        state.begin();
        print(node.getType());
        state.end();
        state.end();
    }

    public void print(String object) {
        state.println(object);
    }

    @Override
    public void print(Type type) {
        state.begin(type);
        type.print(this);
        state.end();
    }

    public void printAccess(Access node) {
        print("expression", node.getExpression());
        print("property: " + node.getProperty());
    }

    @Override
    public void printAlgebraicType(AlgebraicType type) {
        print(type.getName());
        for (Type argument : type.getArguments()) {
            print(argument);
        }
        for (Type member : type.getOptions()) {
            print(member);
        }
    }

    public void printApply(Apply node) {
        print(node.getFunction());
        print(node.getArgument());
    }

    public void printAssign(Assign node) {
        print(node.getLeft());
        print(node.getRight());
    }

    public void printBegin(Begin node) {
        print("body", node.getBody());
    }

    public void printBooleanConstant(BooleanConstant node) {
        print("value: " + node.getValue());
    }

    public void printBreak(@SuppressWarnings("unused") Break node) {
        // intentionally empty
    }

    public void printCharacterConstant(CharacterConstant node) {
        print("value: '" + escapeJava(valueOf(node.getValue())));
    }

    public void printClosure(Closure node) {
        print(node.getBody());
    }

    public void printClosureLocator(ClosureLocator locator) {
        print("name: " + locator.getName());
        print("environment: " + locator.getEnvironment());
    }

    public void printContinue(@SuppressWarnings("unused") Continue node) {
        // intentionally empty
    }

    public void printDeclarationLocator(DeclarationLocator locator) {
        print("name: " + locator.getName());
    }

    public void printDeclaredArgument(DeclaredArgument node) {
        print("name: " + node.getName());
    }

    public void printDeclaredConstant(DeclaredConstant node) {
        print("name: " + node.getQualifiedName());
    }

    public void printDeclaredConstructor(DeclaredConstructor node) {
        print("name: " + node.getQualifiedName());
        print("body", node.getBody());
    }

    public void printDeclaredExpression(DeclaredExpression node) {
        print("name: '" + node.getQualifiedName() + "'");
        print(node.getBody());
    }

    public void printDeclaredProperty(DeclaredProperty node) {
        print("name: " + node.getName());
        print("type", node.getType());
    }

    public void printDeclaredRecord(DeclaredRecord node) {
        print("name: " + node.getQualifiedName());
        List<Property> properties = node.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            print("property " + i);
            state.begin();
            print("name: " + properties.get(i).getName());
            print("type", properties.get(i).getType());
            state.end();
        }
    }

    public void printDeclaredType(DeclaredType node) {
        print("name: " + node.getQualifiedName());
        List<NamedNode> variants = node.getVariants();
        for (int i = 0; i < variants.size(); i++) {
            print("variant " + i, variants.get(i));
        }
    }

    public void printDoubleConstant(DoubleConstant node) {
        print("value: " + node.getValue());
    }

    public void printEmbrace(Embrace embrace) {
        print("variable: " + embrace.getVariable());
        print("type: " + embrace.getException());
        print(embrace.getBody());
    }

    public void printExceptional(Exceptional node) {
        print(node.getBegin());
        for (AstNode embrace : node.getEmbraces()) {
            print(embrace);
        }
        print(node.getEnsure());
    }

    public void printExpressionConstant(ExpressionConstant node) {
        print(node.getValue());
    }

    public void printFunction(Function node) {
        print("variable: " + node.getVariable());
        print(node.getBody());
    }

    public void printFunctionClosure(FunctionClosure node) {
        print("variable: " + node.getVariable());
        print(node.getBody());
    }

    @Override
    public void printFunctionType(FunctionType type) {
        print(type.getArgument());
        print(type.getResult());
    }

    public void printGuardCase(GuardCase node) {
        print(node.getCondition());
        print(node.getExpression());
    }

    public void printGuardCases(GuardCases node) {
        for (AstNode n : node.getCases()) {
            print(n);
        }
    }

    public void printHurl(Hurl node) {
        print("body", node.getBody());
    }

    public void printInitializer(Initializer node) {
        print("constructor", node.getConstructor());
        List<AstNode> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            print("argument " + i, arguments.get(i));
        }
    }

    public void printIntegerConstant(IntegerConstant node) {
        print("value: " + node.getValue());
    }

    public void printLogicalAnd(LogicalAnd node) {
        print("left", node.getLeft());
        print("right", node.getRight());
    }

    public void printLogicalOr(LogicalOr node) {
        print("left", node.getLeft());
        print("right", node.getRight());
    }

    public void printLoop(Loop node) {
        print("condition", node.getCondition());
        print("body", node.getBody());
    }

    public void printMatchConstant(MatchConstant node) {
        print("value", node.getConstant());
    }

    public void printMatchConstructor(MatchConstructor node) {
        print("reference", node.getReference());
        List<AstNode> parameters = node.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            print("parameter " + i, parameters.get(i));
        }
    }

    public void printNop(@SuppressWarnings("unused") Nop node) {
        // intentionally empty
    }

    public void printPatternCase(PatternCase node) {
        List<AstNode> matchers = node.getMatchers();
        for (int i = 0; i < matchers.size(); i++) {
            print("matcher " + i, matchers.get(i));
        }
        print("body", node.getBody());
    }

    public void printPatternCases(PatternCases node) {
        for (int i = 0; i < node.getPatterns().size(); i++) {
            print("pattern " + i, node.getPatterns().get(i));
        }
    }

    public void printPropertyInitializer(PropertyInitializer node) {
        print("name: " + node.getName());
        print("value", node.getValue());
    }

    @Override
    public void printRecordType(RecordType type) {
        print("name: " + type.getName());
        for (Property property : type.getProperties()) {
            state.begin(property);
            print("name: " + property.getName());
            print(property.getType());
            state.end();
        }
    }

    @Override
    public void printRecursiveType(RecursiveType type) {
        print("reference: " + type.getName());
        for (Type argument : type.getArguments()) {
            print(argument);
        }
    }

    public void printReference(Reference node) {
        print("locator: '" + node.getLocator() + "'");
    }

    public void printReferencesEqual(ReferencesEqual node) {
        print(node.getLeft());
        print(node.getRight());
    }

    public void printResult(Result node) {
        print(node.getValue());
    }

    public void printSequence(Sequence sequence) {
        for (AstNode element : sequence.getElements()) {
            print(element);
        }
    }

    @Override
    public void printSimpleType(SimpleType type) {
        print("name: " + type.getName());
    }

    public void printStringConstant(StringConstant node) {
        print("value: \"" + escapeJava(node.getValue()) + "\"");
    }

    public void printSymbolConstant(SymbolConstant node) {
        print("name: \"" + escapeJava(node.getName()) + "\"");
    }

    public void printTupleInitializer(TupleInitializer node) {
        print("elements: " + node.getElements().size());
        for (AstNode element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void printUnionType(UnionType type) {
        for (Type t : type.getTypes()) {
            print(t);
        }
    }

    public void printUnitConstant(@SuppressWarnings("unused") UnitConstant node) {
        // intentionally empty
    }

    public void printVariableDeclaration(VariableDeclaration node) {
        print("name: " + node.getName());
    }

    public void printVariableLocator(VariableLocator locator) {
        print("name: " + locator.getName());
    }

    @Override
    public void printVariableType(VariableType type) {
        if (type.expose() == type) {
            print("name: " + type.getName());
        } else {
            print(type.expose());
        }
    }

    public void printVoidFunction(VoidFunction node) {
        print(node.getBody());
    }

    private void print(String label, Type type) {
        print(label + ":");
        state.begin();
        print(type);
        state.end();
    }

    private void print(String label, AstNode node) {
        print(label + ":");
        state.begin();
        print(node);
        state.end();
    }
}
