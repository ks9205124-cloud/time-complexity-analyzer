package com.shaurya.spring.timecomplexityanalyzer;

import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimeComplexityAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeComplexityAnalyzerApplication.class, args);

        Lexer lexer = new Lexer("int n = 3;\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            for (int j = 0; j < n; j++) {\n" +
                "                System.out.print(i + \",\" + j + \" \");\n" +
                "            }\n" +
                "            System.out.println();\n" +
                "// this is a test comment" +
                "        }");
        lexer.scan();
        lexer.printTokens();
    }
}