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

        Lexer lexer = new Lexer("for(++){" +
                "for(/=){" +
                "}" +
                "for(++){" +
                "}" +
                "}");
        lexer.scan();
        Parser parser = new Parser(lexer);
        var t = parser.parse();
        parser.walk(t);

        //System.out.println(parser.getDelthList());
        System.out.println(parser.getLogList());
    }
}