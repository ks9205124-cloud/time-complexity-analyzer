package com.shaurya.spring.timecomplexityanalyzer.engine;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

public class Token {
    final String lexeme;
    final TokenType type;

    public Token(String lexeme,TokenType type){
        this.lexeme = lexeme;
        this.type = type;
    }

    public String toString(){
        return lexeme + ": " + type;
    }
}
