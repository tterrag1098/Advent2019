package com.tterrag.advent2019.util.intcode;

import java.util.function.BiFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum ParameterMode {
    POSITION((i, interpreter) -> interpreter.program[IntcodeInterpreter.safeCast(i)], (i, interpreter) -> IntcodeInterpreter.safeCast(i)),
    IMMEDIATE((i, interpreter) -> i, (i, interpreter) -> IntcodeInterpreter.safeCast(i)),
    RELATIVE((i, interpreter) -> interpreter.program[IntcodeInterpreter.safeCast(i + interpreter.relativeBase)], (i, interpreter) -> IntcodeInterpreter.safeCast(i + interpreter.relativeBase))
    ;
    
    private final BiFunction<Long, IntcodeInterpreter, Long> read;
    private final BiFunction<Long, IntcodeInterpreter, Integer> write;

    long read(long reg, IntcodeInterpreter interpreter) {
        return read.apply(reg, interpreter);
    }
    
    int write(long reg, IntcodeInterpreter interpreter) {
        return write.apply(reg, interpreter);
    }
}