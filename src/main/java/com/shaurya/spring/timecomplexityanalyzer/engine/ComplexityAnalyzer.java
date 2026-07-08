package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;

public class ComplexityAnalyzer {
    private final Parser parser;
    public ComplexityAnalyzer(Parser parser) {
        this.parser = parser;
    }
    public ComplexityResult result() {
        StringBuilder sb = new StringBuilder();
        for (int i : parser.getLogList()){
            if(i == 0){
                sb.append("*n");
            } else if (i == 1) {
                sb.append("*log(n)");
            }
        }
        //boolean hasLogN = !parser.logN.isEmpty();
        int depth = parser.getMaxDepth();

        //if (hasLogN && depth <= 1) return new ComplexityResult("O(log n)",String.valueOf(parser.getDepth()));
        //if (hasLogN && depth == 2) return new ComplexityResult("O(n log n)",String.valueOf(parser.getDepth()));
        return switch (depth) {
            case 0 -> new ComplexityResult("O(1)",String.valueOf(depth));
            default -> new ComplexityResult("O(1"+sb.toString()+")",String.valueOf(depth));
        };

    }
}
