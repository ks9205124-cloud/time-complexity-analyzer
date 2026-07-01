package com.shaurya.spring.timecomplexityanalyzer.engine;

/**
 * Represents the type of a token produced by the Lexer.
 * Used by the ComplexityAnalyzer to detect loop structures and Big O patterns.
 */
public enum TokenType {

    // --- Keywords ---
    FOR, WHILE, DO, IF, ELSE,
    INT, LONG, DOUBLE, FLOAT, BOOLEAN, CHAR, VOID,
    RETURN, BREAK, CONTINUE,
    NEW, THIS, STATIC, FINAL, CLASS, PUBLIC, PRIVATE, PROTECTED,

    // --- Structural (critical for nesting depth) ---
    LBRACE,     // {
    RBRACE,     // }
    LPAREN,     // (
    RPAREN,     // )
    SEMICOLON,  // ;

    // --- Log n detectors ---
    DIVIDE_ASSIGN,    // /=
    MULTIPLY_ASSIGN,  // *=
    SHIFT_LEFT,       //
    SHIFT_RIGHT,      // >>

    // --- General operators ---
    ASSIGN,    // =
    LESS,      //
    GREATER,   // >

    // --- Literals ---
    IDENTIFIER,  // variable names: i, n, arr
    NUMBER,      // 0, 1, 2

    // --- Catch-all ---
    EOF,    // end of input
    OTHER   // anything else
}