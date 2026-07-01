package com.shaurya.spring.timecomplexityanalyzer;

import com.shaurya.spring.timecomplexityanalyzer.engine.Lexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimeComplexityAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeComplexityAnalyzerApplication.class, args);

        Lexer lexer = new Lexer("i++ i-- i*=2 i/=2 i<<1 i>>1 i==j i!=j i<=j i>=j i+=1 i-=1 a&&b a||b");
        lexer.scan();
    }
}