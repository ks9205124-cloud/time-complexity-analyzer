package com.shaurya.spring.timecomplexityanalyzer.engine.nodes;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WhileNode implements rootNode {
    private rootNode body;
    private boolean isLogN;

    public WhileNode(rootNode body, boolean isLogN) {
        this.body = body;
        this.isLogN = isLogN;
    }

    public WhileNode(rootNode body) {
        this.body = body;
        this.isLogN = false;
    }
}