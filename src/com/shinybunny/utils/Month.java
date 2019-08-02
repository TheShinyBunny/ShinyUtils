package com.shinybunny.utils;

public enum Month {
    JANUARY(0,31),
    FEBRUARY(1,28),
    MARCH(2,31),
    APRIL(3,30),
    MAY(4,31),
    JUNE(5,30),
    JULY(6,31),
    AUGUST(7,31),
    SEPTEMBER(8,30),
    OCTOBER(9,31),
    NOVEMBER(10,30),
    DECEMBER(11,31);

    private final int index;
    private final int days;

    Month(int index, int days) {
        this.index = index;
        this.days = days;
    }

    public static Month byId(int index) {
        return ListUtils.firstMatch(values(), m->m.index == index);
    }

    public int getIndex() {
        return index;
    }

    public int getDays(int year) {
        if (this == FEBRUARY) {
            return TimeStamp.isLeapYear(year) ? 29 : days;
        }
        return days;
    }

    public Month next() {
        return byId((index+1)%values().length);
    }

    public Month previous() {
        return byId(index == 0 ? values().length-1 : index-1);
    }
}
