package iddic.lang.compiler;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static iddic.lang.compiler.IddicParser.*;
import static iddic.lang.compiler.SyntaxFactory.*;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import iddic.lang.compiler.IddicParser.*;
import iddic.lang.compiler.syntax.*;

public class Translator extends IddicParserBaseVisitor<SyntaxNode> {

    private final ArrayDeque<List<SyntaxNode>> collections;

    public Translator() {
        collections = new ArrayDeque<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends SyntaxNode> T translate(ParserRuleContext ctx) {
        return (T) ctx.accept(this);
    }

    @Override
    public SyntaxNode visitAccessor(@NotNull AccessorContext ctx) {
        return access(translate(ctx.Id()));
    }

    @Override
    public SyntaxNode visitAlternativeConditional(@NotNull AlternativeConditionalContext ctx) {
        SyntaxNode condition = translate(ctx.expression());
        if (ctx.ElseIf() != null) {
            return truthy(condition, translate(ctx.sequenceExpressions()));
        } else if (ctx.ElseUnless() != null) {
            return falsy(condition, translate(ctx.sequenceExpressions()));
        } else {
            throw new IllegalArgumentException("Did not receive IF or UNLESS");
        }
    }

    @Override
    public SyntaxNode visitBlockExpression(@NotNull BlockExpressionContext ctx) {
        return translate(ctx.sequenceExpressions());
    }

    @Override
    public SyntaxNode visitBooleanLiteral(@NotNull BooleanLiteralContext ctx) {
        return literal(ctx.value.getType() == True);
    }

    @Override
    public SyntaxNode visitCanonicalFunction(@NotNull CanonicalFunctionContext ctx) {
        return func(arguments(ctx.argument()), translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitCharacterLiteral(@NotNull CharacterLiteralContext ctx) {
        return literal(translate(ctx.Character()).charAt(2));
    }

    @Override
    public SyntaxNode visitConditionalExpression(@NotNull ConditionalExpressionContext ctx) {
        SyntaxNode condition = translate(ctx.expression());
        if (ctx.If() != null) {
            condition = truthy(condition, translate(ctx.sequenceExpressions()));
        } else if (ctx.Unless() != null) {
            condition = falsy(condition, translate(ctx.sequenceExpressions()));
        } else {
            throw new IllegalArgumentException("Did not receive IF or UNLESS");
        }
        beginCollection();
        for (AlternativeConditionalContext alternative : ctx.alternativeConditional()) {
            collect(translate(alternative));
        }
        ElseConditionalContext elseCtx = ctx.elseConditional();
        if (elseCtx != null) {
            collect(translate(elseCtx));
        }
        return conditional(condition, acceptCollection());
    }

    @Override
    public SyntaxNode visitDeclaration(@NotNull DeclarationContext ctx) {
        MetaAnnotationsContext metaCtx = ctx.metaAnnotations();
        if (metaCtx == null) {
            return declaration(translate(ctx.Id()), translate(ctx.expression()));
        } else {
            beginCollection();
            translate(metaCtx);
            MetaAnnotation[] meta = new MetaAnnotation[metaCtx.metaAnnotation().size()];
            return declaration(translate(ctx.Id()), translate(ctx.expression()), acceptCollection().toArray(meta));
        }
    }

    @Override
    public SyntaxNode visitDoubleLiteral(@NotNull DoubleLiteralContext ctx) {
        return literal(parseDouble(translate(ctx.Double())));
    }

    @Override
    public SyntaxNode visitEmbraceClause(@NotNull EmbraceClauseContext ctx) {
        return func(args(translate(ctx.Id())), translate(ctx.sequenceExpressions()));
    }

    @Override
    public SyntaxNode visitEmbraceExpression(@NotNull EmbraceExpressionContext ctx) {
        SyntaxNode begin = translate(ctx.sequenceExpressions());
        beginCollection();
        for (EmbraceClauseContext clause : ctx.embraceClause()) {
            collect(translate(clause));
        }
        List<SyntaxNode> embrace = acceptCollection();
        if (embrace.size() == 0) {
            embrace.add(VoidExpression.INSTANCE);
        }
        EnsureClauseContext ensureCtx = ctx.ensureClause();
        if (ensureCtx == null) {
            return embrace(begin, embrace, VoidExpression.INSTANCE);
        } else {
            return embrace(begin, embrace, translate(ensureCtx));
        }
    }

    @Override
    public SyntaxNode visitEnsureClause(@NotNull EnsureClauseContext ctx) {
        return translate(ctx.sequenceExpressions());
    }

    @Override
    public SyntaxNode visitEntryList(@NotNull EntryListContext ctx) {
        for (EntryContext entry : ctx.entry()) {
            collect(entry(translate(entry.expression(0)), translate(entry.expression(1))));
        }
        return null;
    }

    @Override
    public SyntaxNode visitExpression(@NotNull ExpressionContext ctx) {
        List<ExpressionContext> expressions = ctx.expression();
        switch (expressions.size()) {
            case 0: return selectors(ctx.selector());
            case 1: return unary(translate(ctx.op), translate(expressions.get(0)));
            case 2: return binary(translate(ctx.op), translate(expressions.get(0)), translate(expressions.get(1)));
            default: throw new IllegalArgumentException("Got " + expressions.size() + " expressions");
        }
    }

    @Override
    public SyntaxNode visitExpressionInterpolation(@NotNull ExpressionInterpolationContext ctx) {
        return translate(ctx.expression());
    }

    @Override
    public SyntaxNode visitExpressionList(@NotNull ExpressionListContext ctx) {
        for (ExpressionContext expression : ctx.expression()) {
            collect(translate(expression));
        }
        return null;
    }

    @Override
    public SyntaxNode visitExpressionMapLiteral(@NotNull ExpressionMapLiteralContext ctx) {
        beginCollection();
        EntryListContext listCtx = ctx.entryList();
        if (listCtx != null) {
            translate(listCtx);
        }
        List<SyntaxNode> list = acceptCollection();
        MapElement[] elements = new MapElement[list.size()];
        return map(list.toArray(elements));
    }

    @Override
    public SyntaxNode visitFromImport(@NotNull FromImportContext ctx) {
        List<SubImportContext> contexts = ctx.importList().subImport();
        QualifiedIdentifier id = translate(ctx.qualifiedId());
        SubImport[] subImports = new SubImport[contexts.size()];
        for (int i = 0; i < subImports.length; i++) {
            subImports[i] = translate(contexts.get(i));
        }
        return from(id, subImports);
    }

    @Override
    public SyntaxNode visitFunctionArgument(@NotNull FunctionArgumentContext ctx) {
        TypeSpecContext typeSpec = ctx.typeSpec();
        if (typeSpec == null) {
            return new FunctionArgument(translate(ctx.Id()), null);
        } else {
            return new FunctionArgument(translate(ctx.Id()), (QualifiedIdentifier) translate(typeSpec));
        }
    }

    @Override
    public SyntaxNode visitHereDoc(@NotNull HereDocContext ctx) {
        return interpolate(ctx.interpolation());
    }

    @Override
    public SyntaxNode visitHurlStatement(@NotNull HurlStatementContext ctx) {
        return hurl(translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitIdSymbolEntry(@NotNull IdSymbolEntryContext ctx) {
        return entry(symbol(translate(ctx.Id())), translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitIdentifier(@NotNull IdentifierContext ctx) {
        return id(translate(ctx.Id()));
    }

    @Override
    public SyntaxNode visitImport(@NotNull ImportContext ctx) {
        QualifiedIdentifier id = translate(ctx.qualifiedId());
        if (ctx.importAlias() == null) {
            return imports(id, alias(id.getLastSegment()));
        } else {
            IdentifierAlias alias = translate(ctx.importAlias());
            return imports(id, alias);
        }
    }

    @Override
    public SyntaxNode visitImportAlias(@NotNull ImportAliasContext ctx) {
        return alias(translate(ctx.Id()));
    }

    @Override
    public SyntaxNode visitIndexer(@NotNull IndexerContext ctx) {
        beginCollection();
        translate(ctx.expressionList());
        return index(acceptCollection());
    }

    @Override
    public SyntaxNode visitIntegerLiteral(@NotNull IntegerLiteralContext ctx) {
        return literal(parseInt(translate(ctx.Integer())));
    }

    @Override
    public SyntaxNode visitInterpolatedString(@NotNull InterpolatedStringContext ctx) {
        return interpolate(ctx.interpolation());
    }

    @Override
    public SyntaxNode visitKeySymbolEntry(@NotNull KeySymbolEntryContext ctx) {
        String symbol = translate(ctx.KeySymbol());
        symbol = symbol.substring(0, symbol.length() - 1);
        return entry(symbol(symbol), translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitListLiteral(@NotNull ListLiteralContext ctx) {
        beginCollection();
        ExpressionListContext listCtx = ctx.expressionList();
        if (listCtx != null) {
            translate(ctx.expressionList());
        }
        return list(acceptCollection());
    }

    @Override
    public SyntaxNode visitMetaAnnotation(@NotNull MetaAnnotationContext ctx) {
        QualifiedIdentifier id = translate(ctx.qualifiedId());
        SelectorContext selector = ctx.selector();
        return meta(id.getLastSegment(), selector == null ? nothing() : translate(ctx.selector()));
    }

    @Override
    public SyntaxNode visitMetaAnnotations(@NotNull MetaAnnotationsContext ctx) {
        for (MetaAnnotationContext meta : ctx.metaAnnotation()) {
            collect(translate(meta));
        }
        return null;
    }

    @Override
    public SyntaxNode visitModuleDeclaration(@NotNull ModuleDeclarationContext ctx) {
        beginCollection();
        for (ParserRuleContext child : ctx.moduleExpression()) {
            collect(translate(child));
        }
        return module(acceptCollection());
    }

    @Override
    public SyntaxNode visitNothingLiteral(@NotNull NothingLiteralContext ctx) {
        return nothing();
    }

    @Override
    public SyntaxNode visitParentheticalExpression(@NotNull ParentheticalExpressionContext ctx) {
        return translate(ctx.expression());
    }

    @Override
    public SyntaxNode visitParentheticalFromImport(@NotNull ParentheticalFromImportContext ctx) {
        List<SubImportContext> contexts = ctx.importList().subImport();
        QualifiedIdentifier id = translate(ctx.qualifiedId());
        SubImport[] subImports = new SubImport[contexts.size()];
        for (int i = 0; i < subImports.length; i++) {
            subImports[i] = translate(contexts.get(i));
        }
        return from(id, subImports);
    }

    @Override
    public SyntaxNode visitQualifiedId(@NotNull QualifiedIdContext ctx) {
        List<String> segments = new ArrayList<>();
        for (TerminalNode id : ctx.Id()) {
            segments.add(id.getText());
        }
        return qualifiedId(segments);
    }

    @Override
    public SyntaxNode visitRegex(@NotNull RegexContext ctx) {
        Set<String> options = new HashSet<>();
        for (char option : translate(ctx.RegexOptions()).toCharArray()) {
            options.add("" + option);
        }
        return regex(interpolate(ctx.interpolation()), options);
    }

    @Override
    public SyntaxNode visitReturnStatement(@NotNull ReturnStatementContext ctx) {
        return result(translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitSelector(@NotNull SelectorContext ctx) {
        SyntaxNode head = translate(ctx.primaryExpression());
        for (SuffixContext suffix : ctx.suffix()) {
            head = select(head, translate(suffix));
        }
        return head;
    }

    @Override
    public SyntaxNode visitSequenceExpressions(@NotNull SequenceExpressionsContext ctx) {
        beginCollection();
        for (StatementContext statement : ctx.statement()) {
            collect(translate(statement));
        }
        return block(acceptCollection());
    }

    @Override
    public SyntaxNode visitSetLiteral(@NotNull SetLiteralContext ctx) {
        beginCollection();
        ExpressionListContext listCtx = ctx.expressionList();
        if (listCtx != null) {
            translate(ctx.expressionList());
        }
        return set(acceptCollection());
    }

    @Override
    public SyntaxNode visitString(@NotNull StringContext ctx) {
        TerminalNode string = ctx.String();
        return (string == null) ? literal("") : literal(unescapeJava(string.getText()));
    }

    @Override
    public SyntaxNode visitStringInterpolation(@NotNull StringInterpolationContext ctx) {
        return literal(unescapeJava(translate(ctx.String())));
    }

    @Override
    public SyntaxNode visitSubImport(@NotNull SubImportContext ctx) {
        ImportAliasContext aliasCtx = ctx.importAlias();
        String id = translate(ctx.Id());
        IdentifierAlias alias = (aliasCtx == null) ? alias(id) : (IdentifierAlias) translate(aliasCtx);
        return sub(id, alias);
    }

    @Override
    public SyntaxNode visitSymbolList(@NotNull SymbolListContext ctx) {
        for (SymbolEntryContext entry : ctx.symbolEntry()) {
            collect(translate(entry));
        }
        return null;
    }

    @Override
    public SyntaxNode visitSymbolLiteral(@NotNull SymbolLiteralContext ctx) {
        return symbol(translate(ctx.Symbol()).substring(1));
    }

    @Override
    public SyntaxNode visitSymbolMapLiteral(@NotNull SymbolMapLiteralContext ctx) {
        beginCollection();
        SymbolListContext listCtx = ctx.symbolList();
        if (listCtx != null) {
            translate(listCtx);
        }
        List<SyntaxNode> list = acceptCollection();
        MapElement[] elements = new MapElement[list.size()];
        return map(list.toArray(elements));
    }

    @Override
    public SyntaxNode visitTerminalFunction(@NotNull TerminalFunctionContext ctx) {
        return func(arguments(ctx.argument()), translate(ctx.expression()));
    }

    @Override
    public SyntaxNode visitTupleLiteral(@NotNull TupleLiteralContext ctx) {
        beginCollection();
        ExpressionListContext listCtx = ctx.expressionList();
        if (listCtx != null) {
            translate(ctx.expressionList());
        }
        return tuple(acceptCollection());
    }

    @Override
    public SyntaxNode visitVarDeclarationStatement(@NotNull VarDeclarationStatementContext ctx) {
        return var(translate(ctx.Id()), translate(ctx.expression()));
    }

    private List<SyntaxNode> acceptCollection() {
        return collections.pop();
    }

    private FunctionArguments arguments(List<ArgumentContext> argCtx) {
        String[] args = new String[argCtx.size()];
        for (int i = 0; i < args.length; i++) {
            FunctionArgument arg = translate(argCtx.get(i));
            args[i] = arg.getId();
        }
        return args(args);
    }

    private void beginCollection() {
        collections.push(new ArrayList<SyntaxNode>());
    }

    private void collect(SyntaxNode node) {
        collections.peek().add(node);
    }

    private SyntaxNode interpolate(List<InterpolationContext> interpolation) {
        List<SyntaxNode> elements = new ArrayList<>();
        for (InterpolationContext interpCtx : interpolation) {
            elements.add(translate(interpCtx));
        }
        return elements.size() == 1 ? elements.get(0) : interpolation(elements);
    }

    private SyntaxNode selectors(List<SelectorContext> ctxList) {
        Iterator<SelectorContext> selectors = ctxList.iterator();
        SyntaxNode head = translate(selectors.next());
        if (selectors.hasNext()) {
            List<SyntaxNode> tail = new ArrayList<>();
            while (selectors.hasNext()) {
                tail.add(translate(selectors.next()));
            }
            return select(head, args(tail));
        } else {
            return head;
        }
    }

    private String translate(TerminalNode node) {
        return node.getText();
    }

    private String translate(Token token) {
        return token.getText();
    }
}
