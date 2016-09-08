package com.mariolopezjr.test;

public class RecordKeeper {
    private static int distance = 0;

    public static void setDistance(int distanceToCheck) {
        distance = distanceToCheck;
    }

    public static boolean isDistanceRecord() {
        return distance > 40;
    }
}
