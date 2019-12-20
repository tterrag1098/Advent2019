package com.tterrag.advent2019.days;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tterrag.advent2019.days.Day15.Point;
import com.tterrag.advent2019.days.Day17.Direction;
import com.tterrag.advent2019.util.Day;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class Day20 extends Day {
    
    @RequiredArgsConstructor
    enum Tile {
        EMPTY('.'),
        WALL('#'),
        UNKNOWN(' '),
        ;
        
        static Map<Character, Tile> byKey = Arrays.stream(values()).collect(Collectors.toMap(t -> t.key, Function.identity()));
        
        private final char key;
    }
    
    Tile[][] map;
    Map<Point, Point> portals = new HashMap<>();
    Map<Point, Point> portalsReverse = new HashMap<>();
    Point start, end;
    
    @Override
    protected Result doParts() {
        char[][] input = parse(s -> s.replaceAll("\r?\n", "").toCharArray()).toArray(char[][]::new);
        map = new Tile[input.length - 4][input[0].length - 4];
        Map<String, Point> unassignedPortals = new HashMap<>();
        for (int y = 0; y < input.length; y++) {
            for (int x = 0; x < input[y].length; x++) {
                Tile tile = Tile.byKey.get(input[y][x]);
                if (x > 1 && x < input[y].length - 2 && y > 1 && y < input.length - 2) {
                    map[y - 2][x - 2] = tile == null ? Tile.UNKNOWN : tile;
                }
                if (tile == null && x < input[y].length - 1 && y < input.length - 1) {
                    boolean axisX = Character.isAlphabetic(input[y][x + 1]);
                    boolean axisY = Character.isAlphabetic(input[y + 1][x]);
                    if (axisX || axisY) {
                        char secondChar = axisX ? input[y][x + 1] : input[y + 1][x];
                        String name = String.valueOf(input[y][x]) + secondChar;
                        Point p = new Point(axisX ? x != 0 && input[y][x - 1] != ' ' ? x - 3 : x : x - 2, axisY ? y != 0 && input[y - 1][x] != ' ' ? y - 3 : y : y - 2);
                        if (name.equals("AA")) {
                            start = p;
                        } else if (name.equals("ZZ")) {
                            end = p;
                        } else {
                            Point unassigned = unassignedPortals.get(name);
                            if (unassigned == null) {
                                unassignedPortals.put(name, p);
                            } else {
                                unassignedPortals.remove(name);
                                portals.put(p, unassigned);
                                portalsReverse.put(unassigned, p);
                            }
                        }
                    }
                }
            }
        }
        
        return super.doParts();
    }
    
    @Override
    protected Object part1() {
        
        @Value
        class Node {
            Point point;
            int depth;
        }
        
        Queue<Node> search = new ArrayDeque<>();
        Set<Point> seen = new HashSet<>();
        search.add(new Node(start, 0));
        while (!search.isEmpty()) {
            Node n = search.remove();
            Point p = n.getPoint();
            seen.add(p);
            if (p.equals(end)) {
                return n.getDepth();
            }
            for (Direction d : Direction.values()) {
                Point p2 = d.apply(p);
                if (seen.contains(p2)) {
                    continue;
                }
                if (p2.getX() < 0 || p2.getX() >= map[0].length || p2.getY() < 0 || p2.getY() >= map.length || map[p2.getY()][p2.getX()] == Tile.UNKNOWN) {
                    Point portal = portals.get(p);
                    if (portal == null) {
                        portal = portalsReverse.get(p);
                    }
                    if (portal != null) {
                        search.add(new Node(portal, n.getDepth() + 1));
                    }
                } else if (map[p2.getY()][p2.getX()] == Tile.EMPTY) {
                    search.add(new Node(p2, n.getDepth() + 1));
                }
            }
        }
        throw new IllegalStateException();
    }
    
    @Override
    protected Object part2() {
        
        @Value
        @EqualsAndHashCode(onlyExplicitlyIncluded = true)
        class Node {
            @EqualsAndHashCode.Include
            Point point;
            int depth;
            @EqualsAndHashCode.Include
            int level;
            Point cameFrom;
        }
        
        Queue<Node> search = new ArrayDeque<>();
        Set<Node> seen = new HashSet<>();
        search.add(new Node(start, 0, 0, null));
        while (!search.isEmpty()) {
            Node n = search.remove();
            Point p = n.getPoint();
            seen.add(n);
            if (n.getLevel() == 0 && p.equals(end)) {
                return n.getDepth();
            }
            for (Direction d : Direction.values()) {
                Point p2 = d.apply(p);
                Point next = null;
                Point cameFrom = n.getCameFrom();
                boolean portaled = false;
                boolean isOuterEdge = p2.getX() < 0 || p2.getX() >= map[0].length || p2.getY() < 0 || p2.getY() >= map.length;
                if ((isOuterEdge && n.getLevel() > 0) || (!isOuterEdge && map[p2.getY()][p2.getX()] == Tile.UNKNOWN)) {
                    if (p.equals(cameFrom)) {
                        continue;
                    }
                    Point portal = portals.get(p);
                    if (portal == null) {
                        portal = portalsReverse.get(p);
                    }
                    if (portal != null) {
                        next = portal;
                        cameFrom = portal;
                        portaled = true;
                    }
                } else if (!isOuterEdge && map[p2.getY()][p2.getX()] == Tile.EMPTY) {
                    next = p2;
                }
                if (next != null) {
                    Node n2 = new Node(next, n.getDepth() + 1, portaled ? isOuterEdge ? n.getLevel() - 1 : n.getLevel() + 1 : n.getLevel(), cameFrom);
                    if (seen.add(n2)) {
                        search.add(n2);
                    }
                }
            }
        }
        throw new IllegalStateException();
    }
}
