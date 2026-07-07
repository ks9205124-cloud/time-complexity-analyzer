package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

public class Parser {

    private final Lexer lexer;
    private int current;

    @Getter
    public int depth = 0;

    public ArrayList<Integer> logN = new ArrayList<>();
    public ArrayList<Integer> depthList = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    // The core entry point for your recursive descent parser
    public rootNode parse() {
        if (checkTokenType(FOR)) {
            return parseFor();
        }
        if (checkTokenType(LBRACE)) {
            return parseBlock();
        }
        if (checkTokenType(WHILE)) {
            return parseWhile();
        } else {
            advance();
        }
        return null;
    }

    // Tree walker to calculate the maximum nested loop depth
    public void walk(rootNode node) {
        if (node instanceof ForNode) {
            int currentDepth = depth + 1;
            depth = Math.max(depth, currentDepth);
            System.out.println(depth);
            walk(((ForNode) node).getBody());
        }
        if (node instanceof BlockNode) {
            System.out.println("I AM INSIDE BLOCK");
            for (rootNode children : ((BlockNode) node).getChildren()) {
                walk(children);
            }
        }
        if (node instanceof WhileNode) {
            int currentDepth = depth + 1;
            depth = Math.max(depth, currentDepth);
            System.out.println(depth);
            walk(((WhileNode) node).getBody());
        } else {
            System.out.println("EXITING");
        }
    }

    private rootNode parseFor() {
        boolean flag = false; // flag for log(n) determination
        advance(); // consume 'for'

        // consume condition tokens up to the closing RPAREN
        while (!checkTokenType(RPAREN)) {
            if (isLogarithm()) {
                flag = true;
            }
            advance();
        }
        advance(); // consume RPAREN

        rootNode body = parseBlock();
        return new ForNode(body, flag);
    }

    private rootNode parseBlock() {
        List<rootNode> children = new ArrayList<>();
        advance(); // consume LBRACE

        // recursively parse internal components until hitting matching RBRACE
        while (!checkTokenType(RBRACE)) {
            children.add(parse());
        }
        advance(); // consume RBRACE
        return new BlockNode(children);
    }

    private rootNode parseWhile() {
        advance(); // consume 'while'

        // consume condition tokens up to the closing RPAREN
        while (!checkTokenType(RPAREN)) {
            advance();
        }
        advance(); // consume RPAREN

        rootNode body = parseBlock();
        return new WhileNode(body);
    }

    private void advance() {
        current = (current < lexer.tokens.size() - 1) ? current + 1 : current;
    }

    private boolean checkTokenType(TokenType type) {
        return lexer.tokens.get(current).type.equals(type);
    }

    private boolean isLogarithm() {
        return checkTokenType(DIVIDE_ASSIGN) || checkTokenType(MULTIPLY_ASSIGN) ||
                checkTokenType(SHIFT_LEFT) || checkTokenType(SHIFT_RIGHT);
    }
}