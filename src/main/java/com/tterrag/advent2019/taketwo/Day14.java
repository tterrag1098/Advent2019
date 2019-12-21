//package com.tterrag.advent2019.taketwo;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import com.tterrag.advent2019.util.Day;
//
//import lombok.ToString;
//import lombok.Value;
//
//public class Day14 extends Day {
//    
//    @Value
//    @ToString(includeFieldNames = false)
//    static class Ingredient {
//        long amount;
//        String name;
//        
//        public static Ingredient parse(String string) {
//            String[] split = string.trim().split(" ");
//            return new Ingredient(Integer.parseInt(split[0]), split[1]);
//        }
//        
//        public static Ingredient[] input(String string) {
//            return Arrays.stream(string.trim().split(", ")).map(Ingredient::parse).toArray(Ingredient[]::new);
//        }
//    }
//    
//    @Value
//    @ToString(includeFieldNames = false)
//    static class Reaction {
//        Ingredient[] input;
//        Ingredient output;
//    }
//    
//    List<Reaction> reactions;
//    Map<String, List<Reaction>> byInput = new HashMap<>();
//    Map<String, Reaction> byOutput = new HashMap<>();
//    
//    @Override
//    protected Result doParts() {
//        reactions = parse(s -> s.split("=>"))
//                .map(s -> new Reaction(Ingredient.input(s[0]), Ingredient.parse(s[1])))
//                .collect(Collectors.toList());
//        
//        for (Reaction r : reactions) {
//            for (Ingredient input : r.getInput()) {
//                byInput.computeIfAbsent(input.getName(), $ -> new ArrayList<>()).add(r);
//            }
//            byOutput.put(r.getOutput().getName(), r);
//        }
//        
//        return super.doParts();
//    }
//
//    long react(long times) {
//        Reaction root = byOutput.get("FUEL")
//    }
//    
//    @Override
//    protected Object part1() {
//        return oreCost(new Ingredient(1, "FUEL"));
//    }
//    
//    @Override
//    protected Object part2() {
//        return reactWithAvailableOre(byOutput.get("FUEL"), 1_000_000_000_000L);
//    }
//}
