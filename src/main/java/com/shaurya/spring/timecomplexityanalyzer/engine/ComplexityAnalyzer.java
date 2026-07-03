package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;

public class ComplexityAnalyzer {
    private final Parser parser;
    public ComplexityAnalyzer(Parser parser) {
        this.parser = parser;
    }
    public ComplexityResult result(){
        return switch (parser.depth) {
            case 0 -> new ComplexityResult("O(1)");
            default -> new ComplexityResult("O(n^" + parser.getDepth() + ")");
        };
    }
}
