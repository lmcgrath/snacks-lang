package snacks.lang.ast;

public interface LocatorVisitor {

    void visitClosureLocator(ClosureLocator locator);

    void visitDeclarationLocator(DeclarationLocator locator);

    void visitVariableLocator(VariableLocator locator);
}
