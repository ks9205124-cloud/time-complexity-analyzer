package com.shaurya.spring.timecomplexityanalyzer.service;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;
import com.shaurya.spring.timecomplexityanalyzer.engine.ComplexityAnalyzer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Parser;
import com.shaurya.spring.timecomplexityanalyzer.model.Submission;
import com.shaurya.spring.timecomplexityanalyzer.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public ComplexityResult analyze(String source){

        Submission sb = new Submission();
        sb.setUserString(source);

        Lexer lexer = new Lexer(source);
        lexer.scan();

        Parser parser = new Parser(lexer);
        parser.parse();

        ComplexityAnalyzer complexityAnalyzer = new ComplexityAnalyzer(parser);

        sb.setComplexity(complexityAnalyzer.result().complexity());
        sb.setDepth(complexityAnalyzer.result().depth());

        submissionRepository.save(sb);

        return complexityAnalyzer.result();
    }
}
