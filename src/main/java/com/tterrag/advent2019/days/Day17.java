package com.tterrag.advent2019.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

import com.tterrag.advent2019.days.Day15.Point;
import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

public class Day17 extends Day {
    
    long[] input = csvLongArray();
    char[][] map = new char[0][];
    Point robotPos;
    
    @Override
    protected Result doParts() {
        
        return super.doParts();
    }
    
    @Override
    protected Object part1() {
        new IntcodeInterpreter(Collections.emptyList(), new LongConsumer() {
            int lineIdx = 0;
            int idx = 0;
            @Override
            public void accept(long value) {
                if (lineIdx >= map.length) {
                    map = Arrays.copyOf(map, lineIdx + 1);
                }
                char[] line = map[lineIdx];
                if (line == null) {
                    line = map[lineIdx] = lineIdx == 0 ? new char[1] : new char[map[lineIdx - 1].length];
                }
                if (value == 10) {
                    lineIdx++;
                    idx = 0;
                } else {
                    if (idx >= line.length) {
                        line = map[lineIdx] = Arrays.copyOf(line, idx + 1);
                    }
                    line[idx++] = (char) value;
                }
            }
        }).execute(input);
        
        visualize();
        
        int sum = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (isScaffolding(map[y][x])) {
                    if (isScaffolding(x + 1, y) && isScaffolding(x - 1, y) && isScaffolding(x, y + 1) && isScaffolding(x, y - 1)) {
                        sum += x * y;
                    }
                    if (map[y][x] != '#') {
                        robotPos = new Point(x, y);
                    }
                }
            }
        }
        
        return sum;
    }
    
    private boolean isScaffolding(int x, int y) {
        char[] line = y >= 0 && y < map.length ? map[y] : null;
        return line != null && x >= 0 && x < line.length && isScaffolding(line[x]);
    }
    
    private boolean isScaffolding(char c) {
        return c == '#' || c == '^' || c == '>' || c == 'v' || c == '<';
    }
    
    private void visualize() {
        System.out.println();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                System.out.print(map[y][x]);
            }
            System.out.println();
        }
    }
    
    @RequiredArgsConstructor
    enum Direction {
        NORTH('^', 0, -1),
        EAST('>', 1, 0),
        SOUTH('v', 0, 1),
        WEST('<', -1, 0),
        ;
        
        private final char sym;
        private final int dx, dy;

        public Point apply(Point droidPos) {
            return new Point(droidPos.getX() + dx, droidPos.getY() + dy);
        }
        
        public Direction rotate() {
            Direction[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
        
        public Direction rotateCCW() {
            Direction[] values = values();
            return values[((ordinal() - 1) + values.length) % values.length];
        }
    }
    
    Direction dir;
    private int turn(Point p) {
        Point next = dir.apply(p);
        if (isScaffolding(next.getX(), next.getY())) return 0;
        for (Direction d : Direction.values()) {
            next = d.apply(p);
            if (isScaffolding(next.getX(), next.getY())) {
                if (d == dir) {
                    return 0;
                } else if (d == dir.rotate()) {
                    return 1;
                } else if (d == dir.rotateCCW()) {
                    return -1;
                }
            }
        }
        return 2;
    }
    
    @Value
    class Movement {
        boolean right;
        int amount;
        
        public String toString() {
            return (right ? 'R' : 'L') + "," + amount;
        }
    }
    
    @Value
    class Moveset {
        Movement[] movements;
        @Wither
        int occurances;
        
        public String movestring() {
            return Arrays.stream(movements).map(Object::toString).collect(Collectors.joining(","));
        }
        
        public String toString() {
            return "[" + movestring() + "]x" + occurances;
        }
    }
    
    @Override
    protected Object part2() {
        input[0] = 2;
        for (Direction d : Direction.values()) {
            if (map[robotPos.getY()][robotPos.getX()] == d.sym) {
                dir = d;
                break;
            }
        }
        int turn;
        List<Movement> movements = new ArrayList<>();
        boolean currentTurn = false;
        int currentAmount = 0;
        while ((turn = turn(robotPos)) != 2) {
            if (turn == 0xDEAD) {
                throw new IllegalStateException();
            }
            if (turn == 0) {
                currentAmount++;
                robotPos = dir.apply(robotPos);
            } else {
                if (currentAmount > 0) {
                    movements.add(new Movement(currentTurn, currentAmount));
                    currentAmount = 0;
                }
                currentTurn = turn == 1;
                dir = currentTurn ? dir.rotate() : dir.rotateCCW();
            }
        }
        if (currentAmount > 0) {
            movements.add(new Movement(currentTurn, currentAmount));
        }
        System.out.println(movements.stream().map(Object::toString).collect(Collectors.joining(",")));
//        Moveset[] sets = new Moveset[3];
//        int idx = 0;
//        for (int i = 1; i < movements.size() / 2; i++) {
//            List<Object> intermediate = new ArrayList<>(movements);
//            main:
//            for (int j = 0; j < intermediate.size() - i; j++) {
//                for (int k = 0; k < i; k++) {
//                    if (intermediate.get(j + k) instanceof Moveset) {
//                        continue main;
//                    }
//                }
//                Moveset set = new Moveset(intermediate.subList(j, j + i).toArray(new Movement[0]), 0);
//                outer:
//                for (int k = j; k < intermediate.size() - i; k++) {
//                    for (int l = 0; l < i; l++) {
//                        if (!set.getMovements()[l].equals(intermediate.get(k + l))) {
//                            continue outer;
//                        }
//                    }
//                    for (int l = 0; l < i; l++) {
//                        intermediate.remove(k);
//                    }
//                    intermediate.add(k, set = set.withOccurances(set.getOccurances() + 1));
//                }
//                if (set.getOccurances() > 1 && set.movestring().length() <= 20) {
//                    sets[idx] = set;
//                    idx = (idx + 1) % sets.length;
//                    System.out.println(Arrays.toString(sets));
//                }
//                System.out.println(intermediate);
//            }
//            if (Arrays.stream(sets).mapToInt(s -> s.getMovements().length * s.getOccurances()).sum() == movements.size()) {
//                break;
//            }
//        }
//        System.out.println(Arrays.toString(sets));
        
        List<Object> intermediate = new ArrayList<>(movements);
        List<Moveset> subpatterns = new ArrayList<>();
        subpatterns.add(new Moveset(new Movement[] {
                new Movement(true, 8),
                new Movement(false, 10),
                new Movement(false, 12),
                new Movement(true, 4)}, 0));
        subpatterns.add(new Moveset(new Movement[] {
                new Movement(true, 8),
                new Movement(false, 12),
                new Movement(true, 4),
                new Movement(true, 4)}, 0));
        subpatterns.add(new Moveset(new Movement[] {
                new Movement(true, 8),
                new Movement(false, 10),
                new Movement(true, 8)}, 0));
        int patternIdx = 0;
        for (Moveset set : subpatterns) {
            Movement[] pattern = set.getMovements();
            outer:
            for (int i = 0; i <= intermediate.size() - pattern.length; i++) {
                for (int j = 0; j < pattern.length; j++) {
                    if (!pattern[j].equals(intermediate.get(i + j))) {
                        continue outer;
                    }
                }
                for (int j = 0; j < pattern.length; j++) {
                    intermediate.remove(i);
                }
                intermediate.add(i, Character.valueOf((char) ('A' + patternIdx)));
            }
            patternIdx++;
        }
        String program = intermediate.stream().map(Object::toString).collect(Collectors.joining(",")) + "\n";
        List<String> functions = subpatterns.stream().map(Moveset::movestring).map(s -> s + "\n").collect(Collectors.toList());
        StringBuilder progInput = new StringBuilder();
        progInput.append(program);
        functions.forEach(progInput::append);
        progInput.append("n\n");
        return new IntcodeInterpreter(progInput.chars().mapToLong(i -> i).toArray()).execute(input);
    }
}
