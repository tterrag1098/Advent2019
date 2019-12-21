package com.tterrag.advent2019.days;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.tterrag.advent2019.days.Day15.Point;
import com.tterrag.advent2019.days.Day17.Direction;
import com.tterrag.advent2019.util.Day;

import lombok.Value;

public class Day18 extends Day {
    
    boolean[][] walls;
    Point[] keys = new Point[26];
    Point[] doors = new Point[26];
    Map<Point, Character> keyMap = new HashMap<>();
    Map<Point, Character> doorMap = new HashMap<>();
    Point startPos;
    
    private int keyIdx(char c) {
        return c - 'a';
    }
    
    private Point getKey(char c) {
        return keys[keyIdx(c)];
    }
    
    private int doorIdx(char c) {
        return c - 'A';
    }
    
    private Point getDoor(char c) {
        return doors[doorIdx(c)];
    }
    
    @Override
    protected Result doParts() {
        List<String> lines = linesList();
        walls = new boolean[lines.size()][];
        for (int y = 0; y < walls.length; y++) {
            String line = lines.get(y).trim();
            walls[y] = new boolean[line.length()];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                walls[y][x] = c == '#';
                if (!walls[y][x] && c != '.') {
                    Point p = new Point(x, y);
                    if (c == '@') {
                        startPos = p;
                    } else if (Character.isUpperCase(c)) {
                        doors[doorIdx(c)] = p;
                    } else {
                        keys[keyIdx(c)] = p;
                    }
                }
            }
        }
        for (int c = 0; c < 26; c++) {
            keyMap.put(keys[c], (char) (c + 'a'));
            doorMap.put(doors[c], (char) (c + 'A'));
        }
        return super.doParts();
    }

    @Value
    class Node {
        Point point;
        int depth;
    }
    
    private BitSet getReachableKeys(BitSet openedDoors) {
        Queue<Node> search = new ArrayDeque<>();
        search.add(new Node(startPos, 0));
        Set<Point> seen = new HashSet<>();
        BitSet reachableKeys = new BitSet(26);
        while (!search.isEmpty()) {
            Node n = search.remove();
            seen.add(n.getPoint());
            Point p = n.getPoint();
            for (Direction d : Direction.values()) {
                Point p2 = d.apply(p);
                if (seen.contains(p2)) continue;
                if (walls[p2.getY()][p2.getX()] || (doorMap.containsKey(p2) && !openedDoors.get(doorIdx(doorMap.get(p2))))) {
                    if (keyMap.containsKey(p2)) {
                        reachableKeys.set(keyIdx(keyMap.get(p2)));
                        search.add(new Node(p2, n.getDepth() + 1));
                    }
                } else {
                    search.add(new Node(p2, n.getDepth() + 1));
                }
            }
        }
        return reachableKeys;
    }
    
    @Override
    protected Object part1() {
        Set<Character> ownedKeys = new HashSet<>();
        BitSet openedDoors = new BitSet(26);
        while (ownedKeys.size() < 26) {
            BitSet reachableKeys = getReachableKeys(openedDoors);
            int minPath = Integer.MAX_VALUE;
            int key = 0;
            while ((key = reachableKeys.nextSetBit(key)) >= 0) {
                
                key++;
            }
        }
        return keys;
    }
    
    @Override
    protected Object part2() {
        return null;
    }
}
