package com.shaurya.spring.timecomplexityanalyzer.engine;

/**
 * Represents the type of a token produced by the Lexer.
 * Used by the ComplexityAnalyzer to detect loop structures and Big O patterns.
 */
public enum TokenType {

    // --- Keywords ---
    FOR,
    WHILE,

    // --- Structural (critical for nesting depth tracking) ---
    LBRACE,     // {
    RBRACE,     // }
    LPAREN,     // (
    RPAREN,     // )

    // --- Comparison operators ---
    LESS,           //
    LESS_EQUAL,     // <=
    GREATER,        // >
    GREATER_EQUAL,  // >=
    EQUAL,          // ==
    NOT_EQUAL,      // !=

    // --- Log n detectors ---
    PLUS_PLUS,
    MINUS_MINUS,
    DIVIDE_ASSIGN,    // /=
    MULTIPLY_ASSIGN,  // *=
    SHIFT_LEFT,       //
    SHIFT_RIGHT,      // >>

    // --- Literals ---
    IDENTIFIER,  // variable names: i, n, arr
    NUMBER,      // 0, 1, 2

    // --- Catch-all ---
    OTHER,   // anything else
    EOF
}