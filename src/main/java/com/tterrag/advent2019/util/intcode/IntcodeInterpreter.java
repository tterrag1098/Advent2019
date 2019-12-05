package com.tterrag.advent2019.util.intcode;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;

import com.tterrag.advent2019.util.intcode.Opcode.OpcodeEnum;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class IntcodeInterpreter {
    
    private final int input;
    private final IntConsumer output;
    private int lastOutput;
    
    public IntcodeInterpreter() {
        this(0);
    }
    
    public IntcodeInterpreter(int input) {
        this(input, i -> System.out.printf("Program output: %d\n", i));
    }
    
    public IntcodeInterpreter(int input, IntConsumer output) {
        this.input = input;
        this.output = output.andThen(i -> lastOutput = i);
    }
    
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
    
    @Value
    @Getter(AccessLevel.NONE)
    class Argument {
        ParameterMode mode;
        int arg;
        
        int get(int[] data) {
            return mode.apply(arg, data);
        }
        
        void set(int[] data, int val) {
            data[arg] = val;
        }
    }
    
    @Value
    @Getter(AccessLevel.NONE)
    class Context {
        Argument[] args;
        int[] data;
        
        int input() {
            return input;
        }
        
        void output(int out) {
            output.accept(out);
        }
        
        int get(int arg) {
            return args[arg].get(data);
        }
        
        void set(int arg, int val) {
            args[arg].set(data, val);
        }
    }
    
    public int execute(int[] program) {
        int[] data = Arrays.copyOf(program, program.length);
        int ptr = 0;
        while (true) {
            int instr = data[ptr];
            int id = instr % 100;
            int modes = instr / 100;
            OpcodeEnum op = Opcodes.byId.get(id);
            if (op == null) throw new IllegalStateException("Invalid opcode: " + id + " @ " + ptr);
            if (op == Opcodes.HALT) break;
            Argument[] args = new Argument[op.args()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Argument(ParameterMode.values()[modes % 10], data[ptr + i + 1]);
                modes /= 10;
            }
            int prevPtr = ptr;
            ptr = op.run(new Context(args, data), ptr);
            if (prevPtr == ptr) {
                ptr += op.args() + 1;
            }
        }
        return lastOutput;
    }
}
