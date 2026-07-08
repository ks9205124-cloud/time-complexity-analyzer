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
        //  if instance is of FOR
        if (node instanceof ForNode) {
            //  increment current depth
            currDepth++;
            //  check for sibling loops and calc the weight of each sibling (n>log(n))
            if (currDepth > maxDepth) {
                logList.add(((ForNode) node).isLogN() ? 1 : 0);
            } else {
                if (!((ForNode) node).isLogN())
                    logList.set(currDepth - 1, 0);
            }
            //  handles sibling loops
            maxDepth = Math.max(maxDepth, currDepth);
            System.out.println(((ForNode) node).isLogN());
            walk(((ForNode) node).getBody());
            //  decrement current depth
            currDepth--;
        }
        //  if instance is of WHILE
        if (node instanceof WhileNode) {
            //  increment current depth
            currDepth++;
            //  check for sibling loops and calc the weight of each sibling (n>log(n))
            if (currDepth > maxDepth) {
                logList.add(((WhileNode) node).isLogN() ? 1 : 0);
            } else {
                if (!((WhileNode) node).isLogN())
                    logList.set(currDepth - 1, 0);
            }
            //  handles sibling loops
            maxDepth = Math.max(maxDepth, currDepth);
            System.out.println(((WhileNode) node).isLogN());
            walk(((WhileNode) node).getBody());
            //  decrement current depth
            currDepth--;
        }
        //  if instance if of LBRACE
        if (node instanceof BlockNode) {
            for (rootNode children : ((BlockNode) node).getChildren()) {
                walk(children);
            }
        }
    }

    private rootNode parseFor() {
        Token type = null;
        boolean flag = false; // flag for log(n) determination
        advance(); // consume 'for'

        // consume condition tokens up to the closing RPAREN
        while (!checkTokenType(LBRACE)) {
            if (isLogarithm() && type.lexeme.equals(getLexer().tokens.get(getPrev()).lexeme)) flag = true;
            if (isIncrement()) type = getLexer().tokens.get(getPrev());
            advance();
        }

        ForNode forNode = new ForNode(null, flag, type);
        rootNode body = parseBlock(forNode);
        forNode.setBody(body);
        return forNode;
    }

    private rootNode parseBlock(rootNode parent) {
        List<rootNode> children = new ArrayList<>();
        advance(); // consume LBRACE

        // recursively parse internal components until hitting matching RBRACE
        while (!checkTokenType(RBRACE)) {
            if (isLogarithm() && parent instanceof WhileNode && ((WhileNode) parent).getVar().lexeme.equals(getLexer().tokens.get(getPrev()).lexeme)) {
                ((WhileNode) parent).setLogN(true);
            }
            if (isLogarithm() && parent instanceof ForNode && ((ForNode) parent).getVar().lexeme.equals(getLexer().tokens.get(getPrev()).lexeme)) {
                ((ForNode) parent).setLogN(true);
            }
            children.add(parse());
        }
        advance(); // consume RBRACE
        return new BlockNode(children);
    }

    private rootNode parseWhile() {
        Token type = null;
        advance(); // consume 'while'

        // consume condition tokens up to the closing LBrace
        while (!checkTokenType(LBRACE)) {
            if (isIncrement()) type = getLexer().tokens.get(getPrev());
            advance();
        }

        WhileNode whileNode = new WhileNode(null, false, type);
        rootNode body = parseBlock(whileNode);
        whileNode.setBody(body);
        return whileNode;
    }

    private void advance() {
        current = (current < lexer.tokens.size() - 1) ? current + 1 : current;
    }

    private int getPrev() {
        return (current == 0) ? 0 : current - 1;
    }

    private boolean checkTokenType(TokenType type) {
        return lexer.tokens.get(current).type.equals(type);
    }

    private boolean isLogarithm() {
        return checkTokenType(DIVIDE_ASSIGN) || checkTokenType(MULTIPLY_ASSIGN) ||
                checkTokenType(SHIFT_LEFT) || checkTokenType(SHIFT_RIGHT);
    }

    private boolean isIncrement() {
        return checkTokenType(CONDITION);
    }
}