package com.shaurya.spring.timecomplexityanalyzer;

import com.shaurya.spring.timecomplexityanalyzer.engine.ComplexityAnalyzer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import com.shaurya.spring.timecomplexityanalyzer.engine.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class TimeComplexityAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeComplexityAnalyzerApplication.class, args);

        Lexer lexer = new Lexer("for(int i = 0; i < n; i++) { }\n" +
                "for(int i = 0; i < n; i++) {\n" +
                "    for(int j = 0; j < n; j++) {\n" +
                "    }\n" +
                "}");
        lexer.scan();
        lexer.printTokens();

        Parser parser = new Parser(lexer);
        parser.parse();
        parser.print();

        ComplexityAnalyzer complexityAnalyzer = new ComplexityAnalyzer(parser);
        System.out.println(complexityAnalyzer.result());
    }
}