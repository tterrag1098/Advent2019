package com.tterrag.advent2019.days;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

import lombok.RequiredArgsConstructor;

public class Day05 extends Day {
    
    @RequiredArgsConstructor
    enum ParameterMode {
        POSITION((i, data) -> data[i]),
        IMMEDIATE((i, data) -> i),
        ;
        
        private final BiFunction<Integer, int[], Integer> func;
        
        int apply(int reg, int[] data) {
            return func.apply(reg, data);
        }
    }
    
    interface Op {
        int run(ParameterMode[] modes, int[] args, int[] data, int ptr);
    }
    
    @RequiredArgsConstructor
    enum Opcode {
        ADD(1, 3, (modes, args, data, ptr) -> {
            data[args[2]] = modes[0].apply(args[0], data) + modes[1].apply(args[1], data);
            return ptr;
        }),
        MUL(2, 3, (modes, args, data, ptr) -> {
            data[args[2]] = modes[0].apply(args[0], data) * modes[1].apply(args[1], data);
            return ptr;
        }),
        INPUT(3, 1, (modes, args, data, ptr) -> {
            data[args[0]] = inputParam;
            return ptr;
        }),
        OUTPUT(4, 1, (modes, args, data, ptr) -> {
            output = modes[0].apply(args[0], data);
            return ptr;
        }),
        JNZ(5, 2, (modes, args, data, ptr) -> modes[0].apply(args[0], data) != 0 ? modes[1].apply(args[1], data) : ptr),
        JEZ(6, 2, (modes, args, data, ptr) -> modes[0].apply(args[0], data) == 0 ? modes[1].apply(args[1], data) : ptr),
        LT(7, 3, (modes, args, data, ptr) -> {
            if (modes[0].apply(args[0], data) < modes[1].apply(args[1], data)) {
                data[args[2]] = 1;
            } else {
                data[args[2]] = 0;
            }
            return ptr;
        }),
        EQ(8, 3, (modes, args, data, ptr) -> {
            if (modes[0].apply(args[0], data) == modes[1].apply(args[1], data)) {
                data[args[2]] = 1;
            } else {
                data[args[2]] = 0;
            }
            return ptr;
        }),
        HALT(99, 0, (modes, args, data, ptr) -> ptr),
        ;
        
        static Map<Integer, Opcode> byId = Arrays.stream(values()).collect(Collectors.toMap(o -> o.id, Function.identity()));
        
        private final int id;
        private final int args;
        private final Op func;
    }
    
    private final int[] input = csvLongs().mapToInt(l -> (int) l).toArray();
    
    private static int inputParam;
    private static int output;
    
    @Override
    protected Object part1() {
        output = 0;
        inputParam = 1;
        simulate();
        return output;
    }
    
    @Override
    protected Object part2() {
        output = 0;
        inputParam = 5;
        simulate();
        return output;
    }
    
    private void simulate() {
        int[] data = Arrays.copyOf(input, input.length);
        int ptr = 0;
        while (true) {
            int instr = data[ptr];
            int id = instr % 100;
            int modes = instr / 100;
            Opcode op = Opcode.byId.get(id);
            if (op == null) throw new IllegalStateException("Invalid opcode: " + id + " @ " + ptr);
            if (op == Opcode.HALT) break;
            int[] args = new int[op.args];
            ParameterMode[] argModes = new ParameterMode[op.args];
            for (int i = 0; i < args.length; i++) {
                args[i] = data[ptr + i + 1];
                argModes[i] = ParameterMode.values()[(modes / ((int) Math.pow(10, i))) % 10];
            }
            int prevPtr = ptr;
            if (output != 0) {
                throw new IllegalStateException("Non-zero error code: " + output + " @ " + ptr);
            }
            ptr = op.func.run(argModes, args, data, ptr);
            if (prevPtr == ptr) {
                ptr += op.args + 1;
            }
        }
    }
}
