package com.tterrag.advent2019.taketwo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

import lombok.EqualsAndHashCode;
import lombok.Getter;

public class Day06 extends Day {
    
    public static void main(String[] args) {
        new Day06().run();
    }
    
    @EqualsAndHashCode(of = "name")
    @Getter
    private class Node {
        private final String name;
        private Node parent;
        @Getter
        private final List<Node> children = new ArrayList<>();
        private int depth;
        
        Node(String name, Node parent) {
            this.name = name;
            setParent(parent);
        }
        
        final void setParent(Node parent) {
            this.parent = parent;
            this.depth = parent == null ? 0 : parent.getDepth() + 1;
            children.forEach(n -> n.setParent(this));
        }
        
        Set<Node> parents() {
            Set<Node> ret = new HashSet<>();
            Node p = getParent();
            while (p != null) {
                ret.add(p);
                p = p.getParent();
            }
            return ret;
        }
        
        public String toString() {
            return (parent == null ? "" : parent.getName() + " -> ") + name + " -> [" + children.stream().map(Node::getName).collect(Collectors.joining(", ")) + "]";
        }
    }

    private final Map<String, Node> nodes = new HashMap<>(); {
        linesList().forEach(s -> {
            String[] bodies = s.split("\\)");
            Node parent = nodes.computeIfAbsent(bodies[0], name -> new Node(name, null));
            Node child = nodes.computeIfAbsent(bodies[1], name -> new Node(name, parent));
            parent.getChildren().add(child);
            if (child.getParent() == null) {
                child.setParent(parent);
            }
        });
    }

    @Override
    protected Object part1() {
        return nodes.values().stream().mapToInt(Node::getDepth).sum();
    }
    
    @Override
    protected Object part2() {   
        Node orbiting = nodes.get("YOU").getParent();
        Node target = nodes.get("SAN").getParent();
        Set<Node> orbitingToRoot = orbiting.parents();
        Set<Node> targetToRoot = target.parents();
        Set<Node> intersect = new HashSet<>(orbitingToRoot);
        intersect.retainAll(targetToRoot);
        orbitingToRoot.removeAll(intersect);
        targetToRoot.removeAll(intersect);
        return orbitingToRoot.size() + targetToRoot.size() + 2;
    }
}
