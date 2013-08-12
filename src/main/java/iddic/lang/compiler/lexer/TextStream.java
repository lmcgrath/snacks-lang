package iddic.lang.compiler.lexer;

public interface TextStream extends SourceStream {

    void consumeLine();

    Segment getSegment(Position start);
}
