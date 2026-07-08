package com.shaurya.spring.timecomplexityanalyzer.engine;

import com.shaurya.spring.timecomplexityanalyzer.engine.nodes.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.shaurya.spring.timecomplexityanalyzer.engine.TokenType.*;

@Getter
@Setter
public class Parser {

    private final Lexer lexer;
    private int current;

    public int currDepth = 0;
    public int maxDepth = 0;
    public List<Integer> logList = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    // The core entry point for your recursive descent parser
    public rootNode parse() {
        if (checkTokenType(FOR)) {
            return parseFor();
        }
        if (checkTokenType(LBRACE)) {
            return parseBlock(null);
        }
        if (checkTokenType(WHILE)) {
            return parseWhile();
        }
        advance();
        return null;
    }

    // Tree walker to calculate the maximum nested loop depth
    public void walk(rootNode node) {
        if (node instanceof ForNode) {
            currDepth++;
            if (currDepth > maxDepth) {
                logList.add(((ForNode) node).isLogN() ? 1 : 0);
            } else {
                if (!((ForNode) node).isLogN())
                    logList.set(currDepth - 1, 0);
            }
            maxDepth = Math.max(maxDepth, currDepth);
            walk(((ForNode) node).getBody());
            currDepth--;
        }
        if (node instanceof WhileNode) {
            currDepth++;
            if (currDepth > maxDepth) {
                //delthList.add(1);
                logList.add(((WhileNode) node).isLogN() ? 1 : 0);
            } else {
                if (!((WhileNode) node).isLogN())
                    logList.set(currDepth - 1, 0);
            }
            maxDepth = Math.max(maxDepth, currDepth);
            walk(((WhileNode) node).getBody());
            currDepth--;
        }
        if (node instanceof BlockNode) {
            for (rootNode children : ((BlockNode) node).getChildren()) {
                walk(children);
            }
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

        ForNode forNode = new ForNode(null, flag);
        rootNode body = parseBlock(forNode);
        forNode.setBody(body);
        return forNode;
    }

    private rootNode parseBlock(rootNode parent) {
        List<rootNode> children = new ArrayList<>();
        advance(); // consume LBRACE

        // recursively parse internal components until hitting matching RBRACE
        while (!checkTokenType(RBRACE)) {
            if (isLogarithm() && parent instanceof WhileNode) {
                ((WhileNode) parent).setLogN(true);
            }
            if (isLogarithm() && parent instanceof ForNode) {
                ((ForNode) parent).setLogN(true);
            }
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

        WhileNode whileNode = new WhileNode(null, false);
        rootNode body = parseBlock(whileNode);
        whileNode.setBody(body);
        return whileNode;
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