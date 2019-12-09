package com.tterrag.advent2019.util.intcode;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.intcode.IntcodeInterpreter.Context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
@Accessors(fluent = true)
enum Opcode {
    ADD(1, 3, (args, ptr) -> {
        args.set(2, args.get(0) + args.get(1));
        return ptr;
    }),
    MUL(2, 3, (args, ptr) -> {
        args.set(2, args.get(0) * args.get(1));
        return ptr;
    }),
    INPUT(3, 1, (args, ptr) -> {
        args.set(0, args.input());
        return ptr;
    }),
    OUTPUT(4, 1, (args, ptr) -> {
        args.output(args.get(0));
        return ptr;
    }),
    JNZ(5, 2, (args, ptr) -> IntcodeInterpreter.safeCast(args.get(0) != 0L ? args.get(1) : ptr)),
    JEZ(6, 2, (args, ptr) -> IntcodeInterpreter.safeCast(args.get(0) == 0 ? args.get(1) : ptr)),
    LT(7, 3, (args, ptr) -> {
        args.set(2, args.get(0) < args.get(1) ? 1 : 0);
        return ptr;
    }),
    EQ(8, 3, (args, ptr) -> {
        args.set(2, args.get(0) == args.get(1) ? 1 : 0);
        return ptr;
    }),
    ADJ(9, 1, (args, ptr) -> {
        args.adjustRelativeBase((int) args.get(0));
        return ptr;
    }),
    HALT(99, 0, null),
    ;
    
    static Map<Integer, Opcode> byId = Arrays.stream(values()).collect(Collectors.toMap(o -> o.id, Function.identity()));
    
    @Getter
    private final int id;
    @Getter
    private final int args;
    @Delegate
    private final BiFunction<Context, Integer, Integer> func;
}