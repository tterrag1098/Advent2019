package com.tterrag.advent2019.util.intcode;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.tterrag.advent2019.util.intcode.Opcode.OpcodeEnum;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class IntcodeInterpreter {
    
    private final LongSupplier input;
    private final LongConsumer output;
    
    private long[] program;
    @Getter
    private long lastOutput;
    private int relativeBase;
    
    public IntcodeInterpreter() {
        this(() -> 0);
    }
    
    public IntcodeInterpreter(LongSupplier input) {
        this(input, i -> System.out.printf("Program output: %d\n", i));
    }
    
    public IntcodeInterpreter(LongSupplier input, LongConsumer output) {
        this.input = input;
        this.output = output.andThen(i -> lastOutput = i);
    }
    
    @RequiredArgsConstructor
    enum ParameterMode {
        POSITION((i, interpreter) -> interpreter.program[i.intValue()]),
        IMMEDIATE((i, interpreter) -> i),
        RELATIVE((i, interpreter) -> 
        interpreter.program[(int) (i + interpreter.relativeBase)])
        ;
        
        private final BiFunction<Long, IntcodeInterpreter, Long> func;
        
        long apply(long reg, IntcodeInterpreter interpreter) {
            return func.apply(reg, interpreter);
        }
    }
    
    @Value
    @Getter(AccessLevel.NONE)
    class Argument {
        ParameterMode mode;
        long arg;
        
        long get() {
            return mode.apply(arg, IntcodeInterpreter.this);
        }
        
        void set(long val) {
            if (mode == ParameterMode.RELATIVE) {
                IntcodeInterpreter.this.program[(int) arg + relativeBase] = val;
            } else {
                IntcodeInterpreter.this.program[(int) arg] = val;
            }
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
    
    public long execute(long[] program) {
        this.program = Arrays.copyOf(program, program.length + 1000);
        int ptr = 0;
        while (true) {
            long instr = this.program[ptr];
            int id = (int) (instr % 100L);
            long modes = instr / 100;
            OpcodeEnum op = Opcodes.byId.get(id);
            if (op == null) throw new IllegalStateException("Invalid opcode: " + id + " @ " + ptr);
            if (op == Opcodes.HALT) break;
            Argument[] args = new Argument[op.args()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Argument(ParameterMode.values()[(int) (modes % 10)], this.program[ptr + i + 1]);
                modes /= 10;
            }
            int prevPtr = ptr;
            ptr = op.run(new Context(args), ptr);
            if (prevPtr == ptr) {
                ptr += op.args() + 1;
            }
        }
        return lastOutput;
    }
}
