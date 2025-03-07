package com.tterrag.advent2019;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.Month;

import com.tterrag.advent2019.util.Day;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            LocalDateTime time = LocalDateTime.now();
            if (time.getMonth() == Month.DECEMBER && time.getDayOfMonth() <= 25) {
                runDay(time.getDayOfMonth());
            } else {
                throw new IllegalArgumentException("Cannot automatically determine day, since it's not between dec 1-25");
            }
        } else if (args[0].equals("all")){
            for (int i = 1; i <= 25; i++) {
                if (!runDay(i)) {
                    return;
                }
            }
        } else {
            runDay(Integer.parseInt(args[0]));
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean runDay(int dayId) {
        try {
            Class<? extends Day> dayClass;
            try {
                dayClass = (Class<? extends Day>) Class.forName(Main.class.getCanonicalName().replaceAll("Main", "taketwo.Day" + String.format("%02d", dayId)));
            } catch (ClassNotFoundException e) {
                dayClass = (Class<? extends Day>) Class.forName(Main.class.getCanonicalName().replaceAll("Main", "days.Day" + String.format("%02d", dayId)));
            }

            System.out.println("Day " + dayId + ": ");
            Day day = dayClass.getConstructor().newInstance();
            day.run();
            System.out.println();
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find day " + dayId);
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
