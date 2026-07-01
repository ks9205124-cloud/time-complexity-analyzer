package com.shaurya.spring.timecomplexityanalyzer.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lexer {

    private static final Logger logger = LoggerFactory.getLogger(Lexer.class);

    // Tracks current position in source during scanning (fast pointer)
    private int current = 0;

    // Marks the beginning of the current token being scanned (slow pointer)
    private int start = 0;

    // Source code string being lexed
    private final String source;

    public Lexer(String source){
        this.source = source;
    }

    // Scans source for tokens
    // TODO: token classification logic not yet implemented, currently logging raw values
    public void scan(){
        // Traverses until end of source
        while(current < source.length()){
            while(current < source.length() && isTriggerChar(source.charAt(current))){
                current++;
            }
            if(!source.substring(start, current).isEmpty()){
                logger.info(source.substring(start, current));
            }

            if(current < source.length() && !Character.isWhitespace(source.charAt(current))){
                // Handles two-char operators like ++, --, *=, /=, <<, >>
                if (current+1 < source.length() && isTwoCharOperator(source.charAt(current), source.charAt(current+1))) {
                    logger.info(source.substring(current, current+2));
                    current = current+2;
                    start = current;
                    continue;
                }
                logger.info(String.valueOf(source.charAt(current)));
            }
            current++;
            start = current;
        }
    }

    // Returns true if char is part of an identifier or keyword (letter or digit)
    // TODO: token classification logic not yet implemented
    private boolean isTriggerChar(char c){
        return Character.isLetterOrDigit(c);
    }
    
    // Returns true if two consecutive chars form a valid two-char operator
    private boolean isTwoCharOperator(char first, char second){
        switch (first){
            case '+': return second == '+' || second == '=';
            case '-': return second == '-' || second == '=';
            case '*': return second == '=';
            case '/': return second == '=';
            case '<': return second == '<' || second == '=';
            case '>': return second == '>' || second == '=';
            case '=': return second == '=';
            case '!': return second == '=';
            case '&': return second == '&';
            case '|': return second == '|';
            default:  return false;
        }
    }
}