package com.tterrag.advent2019.days;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tterrag.advent2019.util.Day;

import lombok.EqualsAndHashCode;
import lombok.Value;

public class Day03 extends Day {
    
    @Value
    @EqualsAndHashCode(exclude = "depth")
    private static class Node {
        int x, y, depth;
        
        public int manhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }
    }
    
    private List<Node> pathA = parse(linesList().get(0));
    private List<Node> pathB = parse(linesList().get(1));
    
    private List<Node> parse(String line) {
        List<Node> ret = new ArrayList<>(100000);
        Node prev = new Node(0, 0, 0);
        ret.add(prev);
        for (String instr : line.split(",")) {
            char dir = instr.charAt(0);
            int amt = Integer.parseInt(instr.substring(1));
            int x = prev.getX(), y = prev.getY(), depth = prev.getDepth();
            for (int i = 0; i < amt; i++) {
                if (dir == 'U') {
                    y += 1;
                } else if (dir == 'D') {
                    y += -1;
                } else if (dir == 'R') {
                    x += 1;
                } else if (dir == 'L') {
                    x += -1;
                }
                Node n = new Node(x, y, ++depth);
                ret.add(n);
            }
            prev = ret.get(ret.size() - 1);
        }
        return ret;
    }

    @Override
    protected Result doParts() {
        Set<Node> intersections = new HashSet<>(pathA);
        intersections.retainAll(pathB);
        intersections.remove(new Node(0, 0, 0));

        return new Result(intersections.stream().mapToInt(Node::manhattanDistance).min().orElseThrow(IllegalStateException::new),
                          intersections.stream().mapToInt(n -> n.getDepth() + pathB.get(pathB.indexOf(n)).getDepth()).min().orElseThrow(IllegalStateException::new));
    }
}
