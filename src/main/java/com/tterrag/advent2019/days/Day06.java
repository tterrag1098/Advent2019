package com.tterrag.advent2019.days;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

public class Day06 extends Day {
    
    @Value
    @EqualsAndHashCode(of = "name")
    private class Node {
        String name;
        @NonFinal
        @Setter
        Node parent;
        List<Node> children = new ArrayList<>();
        
        public String toString() {
            return (parent == null ? "" : parent.getName() + " -> ") + name + " -> [" + children.stream().map(Node::getName).collect(Collectors.joining(", ")) + "]";
        }
    }
    
    @Value
    private class SearchNode {
        Node node;
        int depth;
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
        int orbits = 0;
        for (Node root : nodes.values().stream().filter(n -> n.getParent() == null).collect(Collectors.toList())) {
            Deque<SearchNode> search = new ArrayDeque<>(root.getChildren().stream().map(s -> new SearchNode(s, 1)).collect(Collectors.toList()));
            while (!search.isEmpty()) {
                SearchNode n = search.pop();
                orbits++;
                orbits += n.getDepth() - 1;
                Node child = n.getNode();
                child.getChildren().forEach(s -> search.add(new SearchNode(s, n.getDepth() + 1)));
            }
        }
        return orbits;
    }
    
    @Override
    protected Object part2() {   
        Node orbiting = nodes.get("YOU").getParent();
        Node target = nodes.get("SAN").getParent();
        Deque<SearchNode> search = new ArrayDeque<>(orbiting.getChildren().stream().map(s -> new SearchNode(s, 1)).collect(Collectors.toList()));
        search.add(new SearchNode(orbiting.getParent(), 1));
        Set<Node> seen = new HashSet<>();
        while (!search.isEmpty()) {
            SearchNode n = search.pop();
            if (n.getNode() == target) {
                return n.getDepth();
            }
            seen.add(n.getNode());
            n.getNode().getChildren().stream()
                .filter(s -> !seen.contains(s))
                .forEach(s -> search.add(new SearchNode(s, n.getDepth() + 1)));
            if (n.getNode().getParent() != null && !seen.contains(n.getNode().getParent())) {
                search.add(new SearchNode(n.getNode().getParent(), n.getDepth() + 1));
            }
        }
        throw new IllegalStateException("Could not find santa :(");
    }
}
