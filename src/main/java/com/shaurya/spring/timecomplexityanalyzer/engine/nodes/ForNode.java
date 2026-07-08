package com.shaurya.spring.timecomplexityanalyzer.engine.nodes;

import com.shaurya.spring.timecomplexityanalyzer.engine.Token;
import com.shaurya.spring.timecomplexityanalyzer.engine.TokenType;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ForNode implements rootNode {
    private Token var;
    private rootNode body;
    private boolean isLogN;

    public ForNode(rootNode body,boolean isLogN,Token var) {
        this.body = body;
        this.isLogN = isLogN;
        this.var = var;
    }
    public ForNode(rootNode body) {
        this.body = body;
        this.isLogN = false;
        this.var = null;
    }
}
