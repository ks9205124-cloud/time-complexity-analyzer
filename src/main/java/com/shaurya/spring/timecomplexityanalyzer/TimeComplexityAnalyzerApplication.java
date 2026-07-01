package com.shaurya.spring.timecomplexityanalyzer;

import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimeComplexityAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeComplexityAnalyzerApplication.class, args);

        Lexer lexer = new Lexer("for(int i=0; i<n; i++) {\n" +
                "   for(int j=0; j<n; j++) {\n" +
                "       sum = sum + 1;\n" +
                "    }\n" +
                "}");
        lexer.scan();
    }
}