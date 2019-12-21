package com.tterrag.advent2019.taketwo;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

public class Day15 extends Day {
    
    @RequiredArgsConstructor
    enum Direction {
        NORTH(1, 0, -1),
        EAST(4, 1, 0),
        SOUTH(2, 0, 1),
        WEST(3, -1, 0),
        ;
        
        private final int id;
        private final int dx, dy;

        public Point apply(Point droidPos) {
            return new Point(droidPos.getX() + dx, droidPos.getY() + dy);
        }
        
        public Direction rotate() {
            Direction[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }
    
    @Value
    @Wither
    static class Point {
        int x, y;
    }
    
    final long[] input = csvLongArray();
    
    Point droidPos = new Point(0, 0);
    Direction direction = Direction.NORTH;
    Direction trueDirection = direction;
    Set<Point> walls = new HashSet<>();
    Map<Point, Long> seen = new HashMap<>();
    long step = 0;
    Point oxygenPos;
    
    class InputHandler implements LongSupplier {
        
        @Override
        public long getAsLong() {
            return step > 3_000 ? -1 : direction.id;
        }
    }
    
    class OutputHandler implements LongConsumer {
        
        @Override
        public void accept(long value) {
            step++;
            seen.put(droidPos, step);
            if (value == 0) {
                seen.put(direction.apply(droidPos), step);
                walls.add(direction.apply(droidPos));
                direction = trueDirection;
            } else {
                trueDirection = direction;
                droidPos = direction.apply(droidPos);
                if (value == 2) {
                    oxygenPos = droidPos;
                }
                System.out.println("Step: " + step);
                visualize();
            }
            Direction cw = direction.rotate();
            long[] neighbors = new long[Direction.values().length];
            for (int i = 0; i < neighbors.length; i++) {
                Direction dir = Direction.values()[i];
                Point p = dir.apply(droidPos);
                if (walls.contains(p)) {
                    neighbors[i] = 0;
                } else if (!seen.containsKey(p)) {
                    neighbors[i] = Long.MAX_VALUE;
                } else {
                    neighbors[i] = step - seen.get(p);
                }
            }
            int bestChoice = -1;
            long max = -1;
            Direction d = cw;
            do {
                int ord = d.ordinal();
                if (neighbors[ord] > max) {
                    bestChoice = ord;
                    max = neighbors[ord];
                }
                d = d.rotate();
            } while (d != cw);
            direction = Direction.values()[bestChoice];
        }
    }
    
    @Override
    protected Result doParts() {
        return super.doParts();
    }
    
    @Value
    class Node {
        Point point;
        int depth;
    }
    
    @Override
    protected Object part1() {
        new IntcodeInterpreter(new InputHandler(), new OutputHandler()).execute(input);
        Queue<Node> search = new ArrayDeque<>();
        Set<Point> seen = new HashSet<>();
        search.add(new Node(new Point(0, 0), 0));
        while (!search.isEmpty()) {
            Node n = search.remove();
            if (n.getPoint().equals(oxygenPos)) {
                return n.getDepth();
            }
            for (Direction d : Direction.values()) {
                Point p = d.apply(n.getPoint());
                if (!walls.contains(p) && seen.add(p)) {
                    search.add(new Node(p, n.getDepth() + 1));
                }
            }
        }
        throw new IllegalStateException("No path found");
    }
    
    @Override
    protected Object part2() {
        Queue<Node> search = new ArrayDeque<>();
        Set<Point> seen = new HashSet<>();
        search.add(new Node(oxygenPos, 0));
        seen.add(search.element().getPoint());
        int maxDepth = 0;
        while (!search.isEmpty()) {
            Node n = search.remove();
            visualize(seen);
            seen.add(n.getPoint());
            maxDepth = Math.max(maxDepth, n.getDepth());
            for (Direction d : Direction.values()) {
                Point p = d.apply(n.getPoint());
                if (!walls.contains(p) && !seen.contains(p)) {
                    search.add(new Node(p, n.getDepth() + 1));
                }
            }
        }
        return maxDepth;
    }
    
    private void visualize() {
        visualize(oxygenPos == null ? Collections.emptySet() : Collections.singleton(oxygenPos));
    }
    
    private void visualize(Set<Point> oxygenated) {
        int minX = Collections.min(this.seen.keySet(), Comparator.comparing(Point::getX)).getX();
        int maxX = Collections.max(this.seen.keySet(), Comparator.comparing(Point::getX)).getX();
        int minY = Collections.min(this.seen.keySet(), Comparator.comparing(Point::getY)).getY();
        int maxY = Collections.max(this.seen.keySet(), Comparator.comparing(Point::getY)).getY();
        StringBuilder sb = new StringBuilder("\n");
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Point p = new Point(x, y);
                sb.append(walls.contains(p) ? '#' : (oxygenated.contains(p)) ? 'O' : x == droidPos.getX() && y == droidPos.getY() ? 'D' : this.seen.containsKey(p) ? '.' : ' ');
            }
            sb.append("\n");
        }
        System.out.println(sb);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
    }
}
