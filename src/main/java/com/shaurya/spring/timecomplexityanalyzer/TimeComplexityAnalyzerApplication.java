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

        Lexer lexer = new Lexer("// Outer loop (Rows)\n" +
                "        for (int i = 1; i <= 3; j/=) {" +
                "i/=" +
                "\n" +
                "            \n" +
                "            // Inner loop (Columns)\n" +
                "            for (int j = 1; j <= 3; j++) {\n" +
                "                System.out.print(\"(\" + i + \", \" + j + \") \");\n" +
                "            }\n" +
                "            \n" +
                "            // Moves to the next line after the inner loop finishes a row\n" +
                "            System.out.println(); \n" +
                "        }");
        lexer.scan();
        lexer.printTokens();
        Parser parser = new Parser(lexer);
        var t = parser.parse();
        parser.walk(t);

    }
}