package com.shaurya.spring.timecomplexityanalyzer.engine;

public class ComplexityAnalyzer {
    private final Parser parser;
    public ComplexityAnalyzer(Parser parser) {
        this.parser = parser;
    }
    public String result(){
        return switch (parser.depth) {
            case 0 -> "O(1)";
            default -> "O(n^" + parser.getDepth() + ")";
        };
    }
}
