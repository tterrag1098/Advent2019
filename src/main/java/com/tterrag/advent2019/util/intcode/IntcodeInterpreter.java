package com.tterrag.advent2019.util.intcode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

public class IntcodeInterpreter {
    
    private static final int MEMORY_BUFFER = 1000;
    
    private final LongSupplier input;
    private final LongConsumer output;
    
    long[] program;
    @Getter
    private long lastOutput;
    int relativeBase;
    
    public IntcodeInterpreter() {
        this(0);
    }
    
    public IntcodeInterpreter(long... input) {
        this(Arrays.stream(input)::iterator);
    }
    
    public IntcodeInterpreter(Iterable<Long> input) {
        this(supplyFromIterable(input));
    }
    
    public IntcodeInterpreter(LongSupplier input) {
        this(input, i -> System.out.printf("Program output: %d\n", i));
    }
    
    public IntcodeInterpreter(LongConsumer output, long... input) {
        this(Arrays.stream(input)::iterator, output);
    }
    
    public IntcodeInterpreter(Iterable<Long> input, LongConsumer output) {
        this(supplyFromIterable(input), output);
    }
    
    public IntcodeInterpreter(LongSupplier input, LongConsumer output) {
        this.input = input;
        this.output = output.andThen(i -> lastOutput = i);
    }
    
    private static LongSupplier supplyFromIterable(Iterable<Long> iterable) {
        return new LongSupplier() {
            Iterator<Long> itr = iterable.iterator();
            @Override
            public long getAsLong() {
                return itr.next();
            }
        };
    }
    
    @Value
    @Getter(AccessLevel.NONE)
    class Argument {
        ParameterMode mode;
        long arg;
        
        long get() {
            return mode.read(arg, IntcodeInterpreter.this);
        }
        
        void set(long val) {
            IntcodeInterpreter.this.program[mode.write(arg, IntcodeInterpreter.this)] = val;
        }
    }
    
    @Value
    @Getter(AccessLevel.NONE)
    class Context {
        Argument[] args;
        
        long input() {
            return input.getAsLong();
        }
        
        void output(long out) {
            output.accept(out);
        }
        
        long get(int arg) {
            return args[arg].get();
        }
        
        void set(int arg, long val) {
            args[arg].set(val);
        }
        
        void adjustRelativeBase(int adj) {
            relativeBase += adj;
        }
    }
    
    static int safeCast(long l) {
        int ret = (int) l;
        if (ret != l) throw new IllegalArgumentException("Invalid 64-bit value " + l + " expected int");
        return ret;
    }
    
    public long execute(long[] program) {
        this.program = Arrays.copyOf(program, program.length + MEMORY_BUFFER);
        int ptr = 0;
        while (true) {
            long instr = this.program[ptr];
            int id = (int) (instr % 100L);
            long modes = instr / 100;
            Opcode op = Opcode.byId.get(id);
            if (op == null) throw new IllegalStateException("Invalid opcode: " + id + " @ " + ptr);
            if (op == Opcode.HALT) break;
            Argument[] args = new Argument[op.args()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Argument(ParameterMode.values()[(int) (modes % 10L)], this.program[ptr + i + 1]);
                modes /= 10;
            }
            int prevPtr = ptr;
            ptr = op.apply(new Context(args), ptr);
            if (prevPtr == ptr) {
                ptr += op.args() + 1;
            }
        }
        return lastOutput;
    }
}
