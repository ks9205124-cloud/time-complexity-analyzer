package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;

public class ComplexityAnalyzer {
    private final Parser parser;
    public ComplexityAnalyzer(Parser parser) {
        this.parser = parser;
    }
    public ComplexityResult result() {
        boolean hasLogN = !parser.logN.isEmpty();
        int depth = parser.getDepth();

        if (hasLogN && depth <= 1) return new ComplexityResult("O(log n)",String.valueOf(parser.getDepth()));
        if (hasLogN && depth == 2) return new ComplexityResult("O(n log n)",String.valueOf(parser.getDepth()));
        return switch (depth) {
            case 0 -> new ComplexityResult("O(1)",String.valueOf(parser.getDepth()));
            case 1 -> new ComplexityResult("O(n)",String.valueOf(parser.getDepth()));
            default -> new ComplexityResult("O(n^" + depth + ")",String.valueOf(parser.getDepth()));
        };
    }
}
