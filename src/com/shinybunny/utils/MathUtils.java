package com.shinybunny.utils;

public class MathUtils {

    /**
     * Changes the given <code>num</code> to be in bounds of "min" and "max".
     * @param num the number to change
     * @param min the minimum getValue
     * @param max the maximum getValue
     * @return <code>num</code> if it's in bounds of min and max. min if it's smaller than min, or max if bigger than max.
     */
    public static double clamp(double num, double min, double max) {
        if (num > max) return max;
        if (num < min) return min;
        return num;
    }

    /**
     * Changes the given <code>num</code> to be in bounds of "min" and "max".
     * @param num the number to change
     * @param min the minimum getValue
     * @param max the maximum getValue
     * @return <code>num</code> if it's in bounds of min and max. min if it's smaller than min, or max if bigger than max.
     */
    public static int clamp(int num, int min, int max) {
        if (num > max) return max;
        if (num < min) return min;
        return num;
    }

    public static boolean inRange(double num, double min, double max) {
        return num >= min && num <= max;
    }

    public static int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return factorial(n - 1) * n;
    }

    public static double sum(double... d) {
        double r = 0;
        for (double v : d) {
            r += v;
        }
        return r;
    }

    public static double average(double... d) {
        return sum(d) / d.length;
    }


}
