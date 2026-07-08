package com.shaurya.spring.timecomplexityanalyzer.engine.nodes;

import com.shaurya.spring.timecomplexityanalyzer.engine.Token;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WhileNode implements rootNode {
    private Token var;
    private rootNode body;
    private boolean isLogN;

    public WhileNode(rootNode body, boolean isLogN, Token var) {
        this.body = body;
        this.isLogN = isLogN;
        this.var = var;
    }

    public WhileNode(rootNode body) {
        this.body = body;
        this.isLogN = false;
        this.var = null;
    }
}