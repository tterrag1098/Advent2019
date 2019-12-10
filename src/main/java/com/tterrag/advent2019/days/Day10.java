package com.tterrag.advent2019.days;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tterrag.advent2019.util.Day;

import lombok.Value;

public class Day10 extends Day {
    
    @Value
    class Point {
        int x, y;
        
        boolean isAsteroid() {
            return asteroidField[y][x];
        }
        
        boolean canSee(Point asteroid) {
            if (!asteroid.isAsteroid()) throw new IllegalArgumentException();
            if (asteroid == this) return false; // Can't see yourself
            int xSlope = asteroid.x - x;
            int ySlope = asteroid.y - y;
            if (xSlope == 0) {
                ySlope = ySlope < 0 ? -1 : 1;
            } else if (ySlope == 0) {
                xSlope = xSlope < 0 ? -1 : 1;
            } else {
                int gcd = Math.abs(gcd(xSlope, ySlope));
                xSlope /= gcd;
                ySlope /= gcd;
            }
            for (int i = 1;; i++) {
                int x = this.x + (xSlope * i);
                int y = this.y + (ySlope * i);
                if (x == asteroid.x && y == asteroid.y) {
                    return true;
                }
                if (new Point(x, y).isAsteroid()) {
                    return false;
                }
            }
        }
        
        private int gcd(int a, int b) {
            if (b == 0)
                return a;
            return gcd(b, a % b);
        }
        
        double angleTo(Point p) {
            if (p == this) throw new IllegalArgumentException();
            if (p.x == 8 && p.y == 1) {
                System.out.println();
            }
            double twopi = Math.PI * 2;
            double theta = twopi - Math.atan2(p.x - x, p.y - y);
            if (theta < 0) {
                theta += twopi;
            }
            if (theta >= twopi) {
                theta -= twopi;
            }
            return theta;
        }
    }
    
    boolean[][] asteroidField;
    List<Point> asteroids = new ArrayList<>();

    @Override
    protected Result doParts() {
        List<String> lines = linesList();
        asteroidField = new boolean[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            asteroidField[i] = new boolean[line.length()];
            char[] chars = line.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                asteroidField[i][j] = chars[j] == '#';
                if (asteroidField[i][j]) {
                    asteroids.add(new Point(j, i));
                }
            }
        }
        return super.doParts();
    }
    
    Point station;
    
    @Override
    protected Object part1() {
        int maxLos = 0;
        Point best = null;
        for (Point p : asteroids) {
            int los = 0;
            for (Point p2 : asteroids) {
                if (p.canSee(p2)) {
                    los++;
                }
            }
            if (los > maxLos) {
                maxLos = los;
                best = p;
            }
        }
        station = best;
        return maxLos;
    }
    
    @Override
    protected Object part2() {
        asteroids.remove(station);
        asteroids.sort((p1, p2) -> Double.compare(p1.angleTo(station), p2.angleTo(station)));
        Iterator<Point> itr = asteroids.iterator();
        int cnt = 0;
        while (true) {
            if (!itr.hasNext()) {
                itr = asteroids.iterator();
            }
            Point p = itr.next();
            if (station.canSee(p)) {
                itr.remove();
                System.out.println(p);
                if (++cnt == 200) {
                    return (p.x * 100) + p.y;
                }
            }
        }
    }
}
