package iddic.lang.compiler.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import java.util.List;
import iddic.lang.IddicException;
import iddic.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor<Void, PrinterState> {

    public void print(SyntaxNode node, PrintStream out) throws IddicException {
        visit(node, new PrinterState(out));
    }

    @Override
    public Void visitAccess(Access access, PrinterState state) throws IddicException {
        state.begin(access);
        state.println(access.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitAccessAssign(AccessAssign assign, PrinterState state) throws IddicException {
        state.begin(assign);
        visit(assign.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitArgumentsList(ArgumentsList list, PrinterState state) throws IddicException {
        state.begin(list);
        for (SyntaxNode argument : list.getArguments()) {
            visit(argument, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression binary, PrinterState state) throws IddicException {
        state.begin(binary);
        state.println("Operator: " + binary.getOperator());
        visit(binary.getLeft(), state);
        visit(binary.getRight(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitBlockLiteral(BlockExpression block, PrinterState state) throws IddicException {
        state.begin(block);
        for (SyntaxNode element : block.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitBooleanLiteral(BooleanLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(literal.isTrue() ? "True" : "False");
        state.end();
        return null;
    }

    @Override
    public Void visitCharacterLiteral(CharacterLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(literal.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitCompoundImportExpression(CompoundImportExpression imports, PrinterState state) throws IddicException {
        state.begin(imports);
        visit(imports.getIdentifier(), state);
        for (SubImport sub : imports.getSubImports()) {
            visit(sub, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitConditional(Conditional conditional, PrinterState state) throws IddicException {
        state.begin(conditional);
        visit(conditional.getHead(), state);
        for (SyntaxNode tail : conditional.getTail()) {
            visit(tail, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration declaration, PrinterState state) throws IddicException {
        state.begin(declaration);
        state.println(declaration.getIdentifier());
        visit(declaration.getExpression(), state);
        for (SyntaxNode meta : declaration.getMeta()) {
            visit(meta, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitMapLiteral(MapLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        for (MapElement element : literal.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitDoubleLiteral(DoubleLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(literal.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitEmbrace(Embrace embrace, PrinterState state) throws IddicException {
        state.begin(embrace);
        visit(embrace.getBegin(), state);
        state.println("Embrace:");
        for (SyntaxNode n : embrace.getEmbrace()) {
            visit(n, state);
        }
        state.println("Ensure:");
        visit(embrace.getEnsure(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitFalsyCondition(FalsyCondition condition, PrinterState state) throws IddicException {
        state.begin(condition);
        visit(condition.getCondition(), state);
        visit(condition.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitFunctionArguments(FunctionArguments list, PrinterState state) throws IddicException {
        state.begin(list);
        List<String> arguments = list.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            state.println(i + ": " + arguments.get(i));
        }
        state.end();
        return null;
    }

    @Override
    public Void visitFunctionLiteral(FunctionLiteral function, PrinterState state) throws IddicException {
        state.begin(function);
        visit(function.getArguments(), state);
        visit(function.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitGlueExpression(GlueExpression glue, PrinterState state) throws IddicException {
        state.begin(glue);
        state.println(glue.getClassName());
        state.end();
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier identifier, PrinterState state) throws IddicException {
        state.println("Identifier: " + identifier.getValue());
        return null;
    }

    @Override
    public Void visitIdentifierAlias(IdentifierAlias alias, PrinterState state) throws IddicException {
        state.begin(alias);
        state.println(alias.getAlias());
        state.end();
        return null;
    }

    @Override
    public Void visitImportExpression(ImportExpression imports, PrinterState state) throws IddicException {
        state.begin(imports);
        visit(imports.getIdentifier(), state);
        visit(imports.getAlias(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitIndexAccess(IndexAccess access, PrinterState state) throws IddicException {
        state.begin(access);
        visit(access.getArguments(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitIndexAssign(IndexAssign assign, PrinterState state) throws IddicException {
        state.begin(assign);
        visit(assign.getArguments(), state);
        visit(assign.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitIntegerLiteral(IntegerLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(literal.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitInterpolation(Interpolation interpolation, PrinterState state) throws IddicException {
        state.begin(interpolation);
        for (SyntaxNode element : interpolation.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitListLiteral(ListLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        for (SyntaxNode element : literal.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitMetaAnnotation(MetaAnnotation meta, PrinterState state) throws IddicException {
        state.begin(meta);
        state.println(meta.getIdentifier());
        visit(meta.getValue(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitModuleDeclaration(ModuleDeclaration module, PrinterState state) throws IddicException {
        state.begin(module);
        for (SyntaxNode declaration : module.getExpressions()) {
            visit(declaration, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitNothingLiteral(NothingLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println("Nothing");
        state.end();
        return null;
    }

    @Override
    public Void visitQualifiedIdentifier(QualifiedIdentifier identifier, PrinterState state) throws IddicException {
        state.begin(identifier);
        state.println(join(identifier.getSegments(), '.'));
        state.end();
        return null;
    }

    @Override
    public Void visitRootIdentifier(RootIdentifier root, PrinterState state) throws IddicException {
        state.begin(root);
        state.println(join(root.getSegments(), '.'));
        state.end();
        return null;
    }

    @Override
    public Void visitSelector(Selector selector, PrinterState state) throws IddicException {
        state.begin(selector);
        visit(selector.getExpression(), state);
        visit(selector.getSelector(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitSetLiteral(SetLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        for (SyntaxNode element : literal.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(literal.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitSymbolLiteral(SymbolLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        state.println(":" + literal.getValue());
        state.end();
        return null;
    }

    @Override
    public Void visitTruthyCondition(TruthyCondition condition, PrinterState state) throws IddicException {
        state.begin(condition);
        visit(condition.getCondition(), state);
        visit(condition.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitTupleLiteral(TupleLiteral literal, PrinterState state) throws IddicException {
        state.begin(literal);
        for (SyntaxNode element : literal.getElements()) {
            visit(element, state);
        }
        state.end();
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression unary, PrinterState state) throws IddicException {
        state.begin(unary);
        state.println("Operator: " + unary.getOperator());
        visit(unary.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitXRangeLiteral(XRangeLiteral xrange, PrinterState state) throws IddicException {
        state.begin(xrange);
        visit(xrange.getRangeBegin(), state);
        visit(xrange.getRangeEnd(), state);
        state.end();
        return null;
    }

    private void visit(SyntaxNode node, PrinterState state) throws IddicException {
        node.accept(this, state);
    }
}
