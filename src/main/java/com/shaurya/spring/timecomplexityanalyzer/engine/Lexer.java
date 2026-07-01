package com.shaurya.spring.timecomplexityanalyzer.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

public class Lexer {

    private static final Logger logger = LoggerFactory.getLogger(Lexer.class);

    // Tracks current position in source during scanning (fast pointer)
    private int current = 0;

    // Marks the beginning of the current token being scanned (slow pointer)
    private int start = 0;

    // Source code string being lexed
    private final String source;

    private final List<Token> tokens = new ArrayList<Token>();

    public Lexer(String source) {
        this.source = source;
    }

    // Scans source for tokens
    public void scan() {
        // Traverses until end of source
        while (current < source.length()) {
            while (current < source.length() && isTriggerChar(source.charAt(current))) {
                current++;
            }
            if (!source.substring(start, current).isEmpty()) {
                tokens.add(new Token(source.substring(start, current)
                        , matchToken(source.substring(start, current))));
            }

            if (current < source.length() && !Character.isWhitespace(source.charAt(current))) {
                // Handles two-char operators like ++, --, *=, /=, <<, >>
                if (current + 1 < source.length() && isTwoCharOperator(source.charAt(current), source.charAt(current + 1))) {
                    tokens.add(new Token(source.substring(current, current + 2),
                            matchToken(source.substring(current, current + 2))));
                    current = current + 2;
                    start = current;
                    continue;
                }
                tokens.add(new Token(String.valueOf(source.charAt(current)),
                        matchToken(String.valueOf(source.charAt(current)))));
            }
            current++;
            start = current;
        }
    }

    // Returns true if char is part of an identifier or keyword (letter or digit)
    private boolean isTriggerChar(char c) {
        return Character.isLetterOrDigit(c);
    }

    // Returns true if two consecutive chars form a valid two-char operator
    private boolean isTwoCharOperator(char first, char second) {
        switch (first) {
            case '+':
                return second == '+' || second == '=';
            case '-':
                return second == '-' || second == '=';
            case '*':
                return second == '=';
            case '/':
                return second == '=';
            case '<':
                return second == '<' || second == '=';
            case '>':
                return second == '>' || second == '=';
            case '=':
                return second == '=';
            case '!':
                return second == '=';
            case '&':
                return second == '&';
            case '|':
                return second == '|';
            default:
                return false;
        }
    }

    private TokenType matchToken(String lexeme) {
        switch (lexeme) {
            // Keywords
            case "for":
                return FOR;
            case "while":
                return WHILE;
            case "do":
                return DO;
            case "if":
                return IF;
            case "else":
                return ELSE;
            case "int":
                return INT;
            case "long":
                return LONG;
            case "double":
                return DOUBLE;
            case "float":
                return FLOAT;
            case "boolean":
                return BOOLEAN;
            case "char":
                return CHAR;
            case "void":
                return VOID;
            case "return":
                return RETURN;
            case "break":
                return BREAK;
            case "continue":
                return CONTINUE;
            case "new":
                return NEW;
            case "this":
                return THIS;
            case "static":
                return STATIC;
            case "final":
                return FINAL;
            case "class":
                return CLASS;
            case "public":
                return PUBLIC;
            case "private":
                return PRIVATE;
            case "protected":
                return PROTECTED;

            // Structural
            case "{":
                return LBRACE;
            case "}":
                return RBRACE;
            case "(":
                return LPAREN;
            case ")":
                return RPAREN;
            case ";":
                return SEMICOLON;

            // Log n detectors
            case "/=":
                return DIVIDE_ASSIGN;
            case "*=":
                return MULTIPLY_ASSIGN;
            case "<<":
                return SHIFT_LEFT;
            case ">>":
                return SHIFT_RIGHT;

            // General operators
            case "=":
                return ASSIGN;
            case "<":
                return LESS;
            case ">":
                return GREATER;

            // TODO: implement IDENTIFIER and NUMBER classification
            default:
                return OTHER;
        }
    }

    public void printTokens() {
        for (Token token : tokens) {
            logger.info(token.toString());
        }
    }
}