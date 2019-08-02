package com.shinybunny.utils;

import java.util.Scanner;

public class ScannerUtils {

    private static final Scanner in = new Scanner(System.in);

    public static int requestInt(String msg) {
        System.out.println(msg);
        return in.nextInt();
    }

    public static int requestInt(String msg, int min, int max, String outOfBounds) {
        System.out.println(msg);
        int i = in.nextInt();
        if (i < min || i > max) {
            System.out.println(outOfBounds);
            return requestInt(msg,min,max,outOfBounds);
        }
        return i;
    }

    public static boolean condition(String msg, String trueInput, String falseInput, String error) {
        System.out.println(msg);
        String next = in.nextLine();
        if (next.equalsIgnoreCase(trueInput)) {
            return true;
        } else if (next.equalsIgnoreCase(falseInput)) {
            return false;
        }
        System.out.println(error);
        return condition(msg,trueInput,falseInput,error);
    }

    public static String request(String msg) {
        System.out.println(msg);
        return in.nextLine();
    }

    public static void close() {
        in.close();
    }
}
