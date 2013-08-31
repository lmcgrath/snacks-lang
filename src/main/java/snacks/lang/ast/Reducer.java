package snacks.lang.ast;

public interface Reducer {

    void reduceReference(Reference node);

    void reduceVariableDeclaration(VariableDeclaration node);

    void reduceVariableLocator(VariableLocator node);
}
