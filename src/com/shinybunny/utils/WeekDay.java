package com.shinybunny.utils;

public enum WeekDay {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    private final int index;

    WeekDay(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static WeekDay byId(int index) {
        return ListUtils.firstMatch(values(), d->d.index == index);
    }

    public WeekDay tomorrow() {
        return add(1);
    }

    public WeekDay yesterday() {
        return subtract(1);
    }

    public WeekDay add(int days) {
        return byId((index+days)%values().length);
    }

    public WeekDay subtract(int days) {
        int id = index - days;
        if (id < 0) {
            id *= -1;
            id %= values().length;
            id = values().length - id;
        }

        return byId(id);
    }
}
