package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.ForNode;
import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.LBraceNode;
import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.rootNode;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

public class Parser {

    private final Lexer lexer;
    private int current;
    @Getter
    public int depth = 0;


    Deque<rootNode> stack = new ArrayDeque<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void parse() {
        stack.push(new LBraceNode());
        while (current < lexer.tokens.size()) {
            if (checkTokenType(FOR)) {
                stack.push(new ForNode());
            }
            if (checkTokenType(LBRACE)) {
                stack.push(new LBraceNode());
            }
            if (checkTokenType(RBRACE)) {
                helper();
            }
            advance();
        }
        System.out.println("DEPTH: " + depth);
    }

    private void helper() {
        while (!(stack.peek() instanceof LBraceNode) && !stack.isEmpty()) {
            stack.pop();
        }
        if (!stack.isEmpty()) stack.pop();//consume LBRACE

        if ((stack.peek() instanceof ForNode) && !stack.isEmpty()) {
            int depth = getCurrentDepth();
            this.depth = Math.max(this.depth, depth);
            stack.pop();
        }
    }

    private int getCurrentDepth() {
        int depth = 0;
        for(rootNode node : stack) {
            if(node instanceof ForNode) {
                depth++;
            }
        }
        return depth;
    }

    //helper fxn
    public void print() {
        System.out.println("------------------");
        for (rootNode node : stack) {
            System.out.println(node.toString());
        }
        System.out.println("------------------");
    }

    //increment current
    private void advance() {
        current++;
    }

    //return true if current token type matches given token
    private boolean checkTokenType(TokenType type) {
        return lexer.tokens.get(current).type.equals(type);
    }
}
