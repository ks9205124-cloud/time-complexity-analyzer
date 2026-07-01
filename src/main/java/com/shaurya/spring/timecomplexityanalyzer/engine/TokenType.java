package com.shaurya.spring.timecomplexityanalyzer.engine;

public enum TokenType {
    // Keywords
    FOR, WHILE, IF, INT, RETURN,

    // Structural — most important
    LBRACE, RBRACE, LPAREN, RPAREN,

    // Separators
            SEMICOLON,

    // Operators — log n detectors
    DIVIDE_ASSIGN,    // /=
    MULTIPLY_ASSIGN,  // *=
    SHIFT_LEFT,       //
    SHIFT_RIGHT,      // >>

    // General operators — low importance
    ASSIGN,           // =
    LESS,             //
    GREATER,          // >

    // Literals
    IDENTIFIER,       // variable names: i, n, arr
    NUMBER,           // 0, 1, 2

    // Catch-all
    EOF,              // end of input
    OTHER             // anything we don't care about
}
