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

    public final List<Token> tokens = new ArrayList<>();

    public Lexer(String source) {
        this.source = stripComments(source);
    }

    // Scans source for tokens
    public List<Token> scan() {
        // Traverses until end of source
        tokens.add(new Token("{", LBRACE));
        while (current < source.length()) {
            //increment current until a meaningful token is formed
            while (current < source.length() && isTriggerChar(source.charAt(current))) {
                current++;
            }
            //Add new token to token list
            if (!source.substring(start, current).isEmpty()) {
                tokens.add(new Token(source.substring(start, current)
                        , matchToken(source.substring(start, current))));
            }
            //Add new character token to token list
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
        tokens.add(new Token("}", RBRACE));
        return tokens;
    }

    // Returns true if char is part of an identifier or keyword (letter or digit)
    private boolean isTriggerChar(char c) {
        return Character.isLetterOrDigit(c);
    }

    // Returns true if two consecutive chars form a valid two-char operator
    private boolean isTwoCharOperator(char first, char second) {
        return switch (first) {
            case '+' -> second == '+' || second == '=';
            case '-' -> second == '-' || second == '=';
            case '*', '/', '=', '!' -> second == '=';
            case '<' -> second == '<' || second == '=';
            case '>' -> second == '>' || second == '=';
            case '&' -> second == '&';
            case '|' -> second == '|';
            default -> false;
        };
    }

    // Maps a lexeme to its corresponding token type
    private TokenType matchToken(String lexeme) {
        switch (lexeme) {
            // Keywords
            case "for":
                return FOR;
            case "while":
                return WHILE;
            // Structural
            case "{":
                return LBRACE;
            case "}":
                return RBRACE;
            case "(":
                return LPAREN;
            case ")":
                return RPAREN;

            // Log n detectors
            case "++":
                return PLUS_PLUS;
            case "--":
                return MINUS_MINUS;
            case "/=":
                return DIVIDE_ASSIGN;
            case "*=":
                return MULTIPLY_ASSIGN;
            case "<<":
                return SHIFT_LEFT;
            case ">>":
                return SHIFT_RIGHT;
            case "<", "<=", "==", ">=", ">", "!=":
                return CONDITION;
            default:
                if (lexeme.chars().allMatch(Character::isDigit)) return NUMBER;
                if (lexeme.chars().allMatch(Character::isLetterOrDigit)) return IDENTIFIER;
                return OTHER;
        }
    }

    // Logs all identified tokens for debugging
    public void printTokens() {
        for (Token token : tokens) {
            logger.info(token.toString());
        }
    }

    //Removes comments from the source string
    private String stripComments(String source) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < source.length()) {
            if (i + 1 < source.length() && source.charAt(i) == '/' && source.charAt(i + 1) == '/') {
                while (i < source.length() && source.charAt(i) != '\n') {
                    i++;
                }
            } else if (i + 1 < source.length() && source.charAt(i) == '/' && source.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < source.length() && !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')) {
                    i++;
                }
                i += 2;
            } else {
                result.append(source.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
}