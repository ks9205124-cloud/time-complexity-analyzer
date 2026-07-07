package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.*;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

public class Parser {

    private final Lexer lexer;
    private int current;
    @Getter
    public int depth = 0;

    public ArrayList<Integer> logN = new ArrayList<>();
    public ArrayList<Integer> depthList = new ArrayList<>();

    Deque<rootNode> stack = new ArrayDeque<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

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
        //boolean for log(n) determination
        boolean flag = false;
        //consume for
        advance();
        //TODO:init for
        //consume till RParen
        while (!checkTokenType(RPAREN)) {
            if (isLogarithm()) {
                flag = true;
            }
            advance();
        }
        advance();
        //start parseBlock
        rootNode body = parseBlock();
        return new ForNode(body, flag);
    }

    private rootNode parseBlock() {
        List<rootNode> children = new ArrayList<>();
        //consume LBrace
        advance();
        //recurse parse until RBrace
        while (!checkTokenType(RBRACE)) {
            children.add(parse());
        }
        //consume RBrace
        advance();
        return new BlockNode(children);
    }

    private rootNode parseWhile() {
        //consume while
        advance();
        //consume till RParen
        while (!checkTokenType(RPAREN)) {
            advance();
        }
        advance();
        //start parseBlock
        rootNode body = parseBlock();
        return new WhileNode(body);
    }

    //increment current
    private void advance() {
        current = (current < lexer.tokens.size() - 1) ? current + 1 : current;
    }

    //return true if current token type matches given token
    private boolean checkTokenType(TokenType type) {
        return lexer.tokens.get(current).type.equals(type);
    }

    private boolean isLogarithm() {
        return checkTokenType(DIVIDE_ASSIGN) || checkTokenType(MULTIPLY_ASSIGN) ||
                checkTokenType(SHIFT_LEFT) || checkTokenType(SHIFT_RIGHT);
    }
}
