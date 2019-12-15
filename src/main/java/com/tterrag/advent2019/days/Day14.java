package com.tterrag.advent2019.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

import lombok.ToString;
import lombok.Value;

public class Day14 extends Day {
    
    @Value
    @ToString(includeFieldNames = false)
    static class Ingredient {
        long amount;
        String name;
        
        public static Ingredient parse(String string) {
            String[] split = string.trim().split(" ");
            return new Ingredient(Integer.parseInt(split[0]), split[1]);
        }
        
        public static Ingredient[] input(String string) {
            return Arrays.stream(string.trim().split(", ")).map(Ingredient::parse).toArray(Ingredient[]::new);
        }
    }
    
    @Value
    @ToString(includeFieldNames = false)
    static class Reaction {
        Ingredient[] input;
        Ingredient output;
    }
    
    List<Reaction> reactions;
    Map<String, List<Reaction>> byInput = new HashMap<>();
    Map<String, Reaction> byOutput = new HashMap<>();
    
    @Override
    protected Result doParts() {
        reactions = parse(s -> s.split("=>"))
                .map(s -> new Reaction(Ingredient.input(s[0]), Ingredient.parse(s[1])))
                .collect(Collectors.toList());
        
        for (Reaction r : reactions) {
            for (Ingredient input : r.getInput()) {
                byInput.computeIfAbsent(input.getName(), $ -> new ArrayList<>()).add(r);
            }
            byOutput.put(r.getOutput().getName(), r);
        }
        
        return super.doParts();
    }

    long react(Reaction reaction, long target) {
        return react(reaction, new HashMap<>(), target);
    }
    
    long reactWithAvailableOre(Reaction reaction, long ore) {
        HashMap<String, Long> results = new HashMap<>();
        for (int i = 0;; i++) {
            long used = react(reaction, results, 1);
            ore -= used;
            if (ore < 0) {
                return i;
            }
            results.remove("FUEL");
        }
    }
        
    long react(Reaction reaction, Map<String, Long> results, long target) {
        Ingredient out = reaction.getOutput();
        long ore = 0;
        while (results.getOrDefault(out.getName(), 0L) < target) {
            for (Ingredient in : reaction.getInput()) {
                if (in.getName().equals("ORE")) {
                    ore += in.getAmount();
                    continue;
                }
                long avail = results.getOrDefault(in.getName(), 0L);
                if (avail < in.getAmount()) {
                    ore += react(byOutput.get(in.getName()), results, in.getAmount());
                }
                results.compute(in.getName(), ($, l) -> l - in.getAmount());
            }
            results.compute(out.getName(), ($, l) -> l == null ? out.getAmount() : l + out.getAmount());
        }
        return ore;
    }
    
    private long oreCost(Ingredient res) {
        return react(byOutput.get(res.getName()), res.getAmount());
    }
    
    @Override
    protected Object part1() {
        return oreCost(new Ingredient(1, "FUEL"));
    }
    
    @Override
    protected Object part2() {
        return reactWithAvailableOre(byOutput.get("FUEL"), 1_000_000_000_000L);
    }
}
