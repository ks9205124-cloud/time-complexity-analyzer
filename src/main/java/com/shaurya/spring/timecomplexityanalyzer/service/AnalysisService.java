package com.shaurya.spring.timecomplexityanalyzer.service;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;
import com.shaurya.spring.timecomplexityanalyzer.engine.ComplexityAnalyzer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Parser;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    public ComplexityResult Analyze(String source){

        Lexer lexer = new Lexer(source);
        lexer.scan();

        Parser parser = new Parser(lexer);
        parser.parse();

        ComplexityAnalyzer complexityAnalyzer = new ComplexityAnalyzer(parser);

        return complexityAnalyzer.result();
    }
}
