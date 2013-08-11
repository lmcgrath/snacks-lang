package iddic.lang.compiler;

public interface TextStream extends SourceStream {

    void consumeLine();

    Segment getSegment(Position start);
}
