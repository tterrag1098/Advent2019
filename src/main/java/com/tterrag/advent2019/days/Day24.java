package com.tterrag.advent2019.days;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.tterrag.advent2019.days.Day15.Point;
import com.tterrag.advent2019.days.Day17.Direction;
import com.tterrag.advent2019.util.Day;

public class Day24 extends Day {
    
    private static final int SIZE = 5;
    
    private boolean[][] getInitialMap() {
        boolean[][] bugs = new boolean[SIZE][SIZE];
        List<String> lines = linesList();
        for (int i = 0; i < bugs.length; i++) {
            char[] chars = lines.get(i).trim().toCharArray();
            for (int j = 0; j < chars.length; j++) {
                bugs[i][j] = chars[j] == '#';
            }
        }
        return bugs;
    }
    
    private int getBiodiversity(boolean[][] bugs) {
        int ret = 0;
        for (int y = 0; y < bugs.length; y++) {
            for (int x = 0; x < bugs[y].length; x++) {
                if (bugs[y][x]) {
                    int bit = ((x % bugs[y].length) + (y * bugs[y].length));
                    if (bit > 31) {
                        throw new IllegalStateException();
                    }
                    ret |= 1 << bit;
//                    System.out.println(Long.toBinaryString(ret));
                }
            }
        }
        return ret;
    }
    
    private boolean isBug(boolean[][] bugs, Point p) {
        return isBug(bugs, p.getX(), p.getY());
    }
    
    private boolean isBug(boolean[][] bugs, int x, int y) {
        return y >= 0 && y < bugs.length && x >= 0 && x < bugs[y].length ? bugs[y][x] : false;
    }
    
    private boolean isBug(Map<Integer, boolean[][]> levels, int level, Point p) {
        return isBug(levels, level, p.getX(), p.getY());
    }

    private boolean isBug(Map<Integer, boolean[][]> levels, int level, int x, int y) {
        boolean[][] bugs = levels.get(level);
        if (x == 2 && y == 2) {
            throw new IllegalStateException();
        }
        if (y >= 0 && y < bugs.length && x >= 0 && x < bugs[y].length) {
            return bugs[y][x];
        } else {
            bugs = levels.get(level - 1);
            if (bugs == null) {
                return false;
            } else if (y < 0) {
                return bugs[1][2];
            } else if (x < 0) {
                return bugs[2][1];
            } else if (y >= SIZE) {
                return bugs[3][2];
            } else if (x >= SIZE) {
                return bugs[2][3];
            } else {
                throw new IllegalStateException();
            }
        }
    }
    
    private void visualize(boolean[][] bugs) {
        for (int y = 0; y < bugs.length; y++) {
            for (int x = 0; x < bugs[y].length; x++) {
                if (x == 2 && y == 2 && !bugs[y][x]) {
                    System.out.print('?');
                } else {
                    System.out.print(bugs[y][x] ? '#' : '.');
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    @Override
    protected Object part1() {
        BitSet seen = new BitSet();
        boolean[][] bugs = getInitialMap();
        while (true) {
//            visualize(bugs);
            int biodiversity = getBiodiversity(bugs);
            if (seen.get(biodiversity)) return biodiversity;
            seen.set(biodiversity);
            boolean[][] newstate = new boolean[bugs.length][bugs[0].length];
            for (int y = 0; y < bugs.length; y++) {
                for (int x = 0; x < bugs[y].length; x++) {
                    int neighbors = 0;
                    for (Direction d : Direction.values()) {
                        Point p = d.apply(new Point(x, y));
                        if (isBug(bugs, p)) {
                            neighbors++;
                        }
                    }
                    boolean isBug = isBug(bugs, x, y);
                    if (isBug && neighbors != 1) {
                        newstate[y][x] = false;
                    } else if (!isBug && (neighbors == 1 || neighbors == 2)) {
                        newstate[y][x] = true;
                    } else {
                        newstate[y][x] = isBug;
                    }
                }
            }
            bugs = newstate;
        }
    }
    
    @Override
    protected Object part2() {
        TreeMap<Integer, boolean[][]> levels = new TreeMap<>();
        levels.put(0, getInitialMap());
        for (int i = 0; i < 200; i++) {
            Map.Entry<Integer, boolean[][]> firstEntry = levels.firstEntry();
            boolean[][] first = firstEntry.getValue();
            if (first[1][2] || first[2][1] || first[3][2] || first[2][3]) {
                levels.put(firstEntry.getKey() - 1, new boolean[SIZE][SIZE]);
            }
            Map.Entry<Integer, boolean[][]> lastEntry = levels.lastEntry();
            boolean[][] last = lastEntry.getValue();
            outer:
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x = y == 0 || y == SIZE - 1 ? x + 1 : x + (SIZE - 1)) {
                    if (last[y][x]) {
                        levels.put(lastEntry.getKey() + 1, new boolean[SIZE][SIZE]);
                        break outer;
                    }
                }
            }
            TreeMap<Integer, boolean[][]> newmap = new TreeMap<>();
            for (Map.Entry<Integer, boolean[][]> e : levels.entrySet()) {
//                System.out.println("Depth: " + e.getKey());
//                visualize(e.getValue());
                boolean[][] bugs = e.getValue();
                boolean[][] newstate = new boolean[SIZE][SIZE];
                for (int y = 0; y < bugs.length; y++) {
                    for (int x = 0; x < bugs[y].length; x++) {
                        if (x == 2 && y == 2) continue;
                        int neighbors = 0;
                        for (Direction d : Direction.values()) {
                            Point p = d.apply(new Point(x, y));
                            if (p.getX() == 2 && p.getY() == 2) {
                                boolean[][] inner = levels.get(e.getKey() + 1);
                                for (int k = 0; inner != null && k < SIZE; k++) {
                                    boolean isBug;
                                    switch(d) {
                                    case NORTH:
                                        isBug = inner[SIZE - 1][k];
                                        break;
                                    case EAST:
                                        isBug = inner[k][0];
                                        break;
                                    case SOUTH:
                                        isBug = inner[0][k];
                                        break;
                                    case WEST:
                                        isBug = inner[k][SIZE - 1];
                                        break;
                                    default:
                                        isBug = false;
                                    }
                                    if (isBug) {
                                        neighbors++;
                                    }
                                }
                            } else if (isBug(levels, e.getKey(), p)) {
                                neighbors++;
                            }
                        }
                        boolean isBug = isBug(bugs, x, y);
                        if (isBug && neighbors != 1) {
                            newstate[y][x] = false;
                        } else if (!isBug && (neighbors == 1 || neighbors == 2)) {
                            newstate[y][x] = true;
                        } else {
                            newstate[y][x] = isBug;
                        }
                    }
                }
                newmap.put(e.getKey(), newstate);
            }
            levels = newmap;
        }

        int count = 0;
        for (boolean[][] bugs : levels.values()) {
            for (int y = 0; y < bugs.length; y++) {
                for (int x = 0; x < bugs[y].length; x++) {
                    if (bugs[y][x]) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
}
