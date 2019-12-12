package com.tterrag.advent2019.days;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

public class Day12 extends Day {
    
    @ToString
    @EqualsAndHashCode
    class Moon {
        private int x, y, z;
        private int velX, velY, velZ;
        
        Moon(int[] xyz) {
            this(xyz[0], xyz[1], xyz[2]);
        }
        
        Moon(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        private int signum(int x) {
            return x < 0 ? -1 : x > 0 ? 1 : 0;
        }
        
        void applyGravity(Moon other) {
            int xDir = signum(other.x - x);
            int yDir = signum(other.y - y);
            int zDir = signum(other.z - z);
            velX += xDir;
            other.velX -= xDir;
            velY += yDir;
            other.velY -= yDir;
            velZ += zDir;
            other.velZ -= zDir;
        }
        
        void move() {
            x += velX;
            y += velY;
            z += velZ;
        }
        
        long getEnergy() {
            return absAdd(x, y, z) * absAdd(velX, velY, velZ);
        }
        
        private long absAdd(int... components) {
            long ret = 0;
            for (int i : components) {
                ret += Math.abs(i);
            }
            return ret;
        }
    }
    
    @Value
    class AxisValue {
        int pos, vel;
    }
    
    private List<Moon> moons;
    private Set<List<AxisValue>> xStates = new HashSet<>();
    private Set<List<AxisValue>> yStates = new HashSet<>();
    private Set<List<AxisValue>> zStates = new HashSet<>();

    @Override
    protected Result doParts() {
        moons = parse(line -> line.replaceAll("[<>]", "").replaceAll("[xyz]=", "").split(", "))
                    .map(arr -> Arrays.stream(arr).mapToInt(Integer::parseInt).toArray())
                    .map(Moon::new)
                    .collect(Collectors.toList());
        return super.doParts();
    }
    
    @Override
    protected Object part1() {
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < moons.size(); j++) {
                for (int k = j + 1; k < moons.size(); k++) {
                    Moon a = moons.get(j);
                    Moon b = moons.get(k);
                    a.applyGravity(b);
                }
            }
            for (Moon moon : moons) {
                moon.move();
            }
        }
        return moons.stream().mapToLong(Moon::getEnergy).sum();
    }
    
    @Override
    protected Object part2() {
        int xPeriod = 0;
        int yPeriod = 0;
        int zPeriod = 0;
        for (int i = 0;; i++) {
            if (xPeriod == 0 && !xStates.add(moons.stream().map(m -> new AxisValue(m.x, m.velX)).collect(Collectors.toList()))) {
                xPeriod = i;
            }
            if (yPeriod == 0 && !yStates.add(moons.stream().map(m -> new AxisValue(m.y, m.velY)).collect(Collectors.toList()))) {
                yPeriod = i;
            }
            if (zPeriod == 0 && !zStates.add(moons.stream().map(m -> new AxisValue(m.z, m.velZ)).collect(Collectors.toList()))) {
                zPeriod = i;
            }
            if (xPeriod != 0 && yPeriod != 0 && zPeriod != 0) {
                return lcm(xPeriod, lcm(yPeriod, zPeriod));
            }
            for (int j = 0; j < moons.size(); j++) {
                for (int k = j + 1; k < moons.size(); k++) {
                    Moon a = moons.get(j);
                    Moon b = moons.get(k);
                    a.applyGravity(b);
                }
            }
            for (Moon moon : moons) {
                moon.move();
            }
        }
    }
    
    private static long lcm(long a, long b) {
        return Math.multiplyExact(a, (b / gcd(a, b)));
    }
    
    private static long gcd(long a, long b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }
}
