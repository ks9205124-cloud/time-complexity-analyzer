package com.shaurya.spring.timecomplexityanalyzer.engine.nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class BlockNode implements rootNode{
    private List<rootNode> children;
    public BlockNode(List<rootNode> children){
        this.children = children;
    }
}
