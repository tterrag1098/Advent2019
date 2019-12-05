package com.tterrag.advent2019.util.intcode;

import com.tterrag.advent2019.util.intcode.IntcodeInterpreter.Context;

interface Opcode {
    int run(Context args, int ptr);
    
    interface OpcodeEnum extends Opcode {
        
        int id();
        
        int args();
    }
}