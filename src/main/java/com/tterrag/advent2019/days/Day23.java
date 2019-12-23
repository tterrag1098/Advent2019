package com.tterrag.advent2019.days;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

import lombok.RequiredArgsConstructor;
import lombok.Value;

public class Day23 extends Day {
    
    final long[] input = csvLongArray();
    
    @Value
    class Packet {
        long x, y;
    }
    
    Queue<Packet>[] packetBuffer;
    long part1, part2;
    
    @RequiredArgsConstructor
    class InputHandler implements LongSupplier {
        
        final int id;
        
        boolean sentId = false;
        boolean x = true;
        
        @Override
        public long getAsLong() {
            if (!sentId) {
                sentId = true;
                return id;
            }
            Packet pkt;
            if (x) {
                pkt = packetBuffer[id].peek();
            } else {
                pkt = packetBuffer[id].poll();
            }
            if (pkt == null) {
                Thread.yield();
                return -1;
            }
            long res = x ? pkt.getX() : pkt.getY();
            x = !x;
            return res;
        }
    }
    
    class OutputHandler implements LongConsumer {
        long id, x;
        int step = 0;
        
        @Override
        public void accept(long value) {
            switch(step++) {
            case 0:
                id = value;
                break;
            case 1:
                x = value;
                break;
            case 2:
                Packet p = new Packet(x, value);
                if (id == 255) {
                    if (part1 == 0) {
                        part1 = value;
                    }
                    natBuffer = p;
                } else {
                    packetBuffer[(int) id].add(p);
                }
                step = 0;
                break;
            }
        }
    }
    
    Packet natBuffer;
    
    @Override
    protected Result doParts() {
        Thread[] threads = new Thread[50];
        packetBuffer = new Queue[threads.length];
        for (int i = 0; i < threads.length; i++) {
            packetBuffer[i] = new LinkedBlockingQueue<>();
            final int id = i;
            threads[i] = new Thread(() -> new IntcodeInterpreter(new InputHandler(id), new OutputHandler()).execute(input));
            threads[i].setDaemon(true);
        }
        final Set<Long> seenYValues = new HashSet<>();
        Thread nat = new Thread(() -> {
            while (true) {
                if (natBuffer != null) {
                    boolean allEmpty = true;
                    for (Queue<Packet> queue : packetBuffer) {
                        if (!queue.isEmpty()) {
                            allEmpty = false;
                            break;
                        }
                    }
                    if (allEmpty) {
                        if (!seenYValues.add(natBuffer.getY())) {
                            part2 = natBuffer.getY();
                        }
                        packetBuffer[0].add(natBuffer);
                        natBuffer = null;
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        nat.setDaemon(true);
        nat.start();
        
        Arrays.stream(threads).forEach(Thread::start);
        
        while (part1 == 0 || part2 == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new Result(part1, part2);
    }
}
