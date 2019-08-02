package com.shinybunny.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeStamp {

    private int date;
    private Month month;
    private int year;
    private int second;
    private int minute;
    private int hour;

    public static final String SIMPLE_FORMAT = "MM/dd/yyyy 'at' hh:mm:ss a";
    public static final String FANCY_FORMAT = "EEEE, d MMMM yyyy 'at' HH:mm";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String FULL_TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String DATE_FORMAT_NO_YEAR = "MM/dd";

    public TimeStamp() {
        this(Calendar.getInstance());
    }

    public TimeStamp(Calendar c) {
        date = c.get(Calendar.DATE);
        month = Month.byId(c.get(Calendar.MONTH));
        year = c.get(Calendar.YEAR);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
    }

    public TimeStamp(String timeString) {
        String[] dateHour = timeString.split(",");
        String[] date = dateHour[0].split("/");
        String[] hour = dateHour[1].split(":");
        this.month = Month.byId(Integer.parseInt(date[0]));
        this.date = Integer.parseInt(date[1]);
        this.year = Integer.parseInt(date[2]);
        this.hour = Integer.parseInt(hour[0]);
        this.minute = Integer.parseInt(hour[1]);
        this.second = Integer.parseInt(hour[2]);
    }

    public TimeStamp(int year, Month month, int date, int hour, int minute, int second) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
    }

    public TimeStamp(long time) {
        this(new Date(time));
    }

    public TimeStamp(Date date) {
        this(toCalendar(date));
    }

    private static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static boolean isLeapYear(int year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }


    public void addSeconds(int seconds) {
        this.second += seconds;
        while (this.second >= 60) {
            this.minute++;
            this.second -= 60;
            while (this.minute >= 60) {
                this.hour++;
                this.minute -= 60;
                while (this.hour >= 24) {
                    this.date++;
                    this.hour -= 24;
                    int m = this.month.getIndex();
                    while (this.date > this.month.getDays(this.year)) {
                        m++;
                        this.date -= this.month.getDays(this.year);
                        while (m >= 12) {
                            this.year++;
                            m -= 12;
                        }
                    }
                    this.month = Month.byId(m);
                }
            }
        }
    }

    public static int get(int field) {
        return Calendar.getInstance().get(field);
    }

    public WeekDay getDayInWeek() {
        return WeekDay.byId(this.asCalendar().get(Calendar.DAY_OF_WEEK)-1);
    }

    public long asLong() {
        return asDate().getTime();
    }

    public Calendar asCalendar() {
        return toCalendar(asDate());
    }

    public Date asDate() {
        return new Date(year-1900,month.getIndex(),date,hour,minute,second);
    }

    public int getYear() {
        return year;
    }

    public boolean isThisYear() {
        return year == get(Calendar.YEAR);
    }

    public int getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TimeStamp) {
            TimeStamp t = (TimeStamp) o;
            return second == t.second && minute == t.minute && hour == t.hour && date == t.date && month == t.month && year == t.year;
        }
        return false;
    }

    public Month getMonth() {
        return month;
    }

    public boolean isSameMonth() {
        return month == Month.byId(get(Calendar.MONTH));
    }

    public boolean isInCurrentMonth() {
        return isThisYear() && isSameMonth();
    }

    public int getMinute() {
        return minute;
    }

    public boolean isSameMinute() {
        return minute == get(Calendar.MINUTE);
    }

    public boolean isInCurrentMinute() {
        return isInCurrentHour() && isSameMinute();
    }

    public int getHour() {
        return hour;
    }

    public boolean isSameHour() {
        return hour == get(Calendar.HOUR_OF_DAY);
    }

    public boolean isInCurrentHour() {
        return isInCurrentDay() && isSameHour();
    }

    public int getDate() {
        return date;
    }

    public boolean isSameDate() {
        return date == get(Calendar.DATE);
    }

    public boolean isInCurrentDay() {
        return isInCurrentMonth() && isSameDate();
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMonth(int month) {
        this.month = Month.byId(month);
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static TimeStamp now() {
        return new TimeStamp();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%d/%d/%d,%d:%d:%d",month.getIndex()+1,date,year,hour,minute,second);
    }

    public String toString(String format) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        sdf.applyPattern(format);
        return sdf.format(asDate());
    }

    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 365 * DAY_MILLIS;

    public String howLongAgo() {
        if (this.isInFuture()) {
            return "IN THE FUTURE";
        }
        long now = System.currentTimeMillis();
        long time = asLong();

        long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 55 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS) + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < DAY_MILLIS) {
            return (diff / HOUR_MILLIS) + " hours ago";
        } else if (diff < 2 * DAY_MILLIS) {
            return "Yesterday";
        } else if (diff < MONTH_MILLIS) {
            return (diff / DAY_MILLIS) + " days ago";
        } else if (diff < 2 * MONTH_MILLIS) {
            return "A month ago";
        } else if (diff < YEAR_MILLIS) {
            return (diff / MONTH_MILLIS) + " months ago";
        } else if (diff < 2 * YEAR_MILLIS) {
            return "A year ago";
        } else {
            return (diff / YEAR_MILLIS) + " years ago";
        }
    }

    public boolean isBefore(TimeStamp time) {
        return this.asLong() < time.asLong();
    }

    public int secondDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.secondDifference(this);
        int secs = this.second - timeBefore.second;
        if (secs < 0) {
            secs += 60 * minuteDifference(timeBefore);
        }
        return secs;
    }

    public int minuteDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.minuteDifference(this);
        int mins = this.minute - timeBefore.minute;
        if (mins < 0) {
            mins += 60 * hourDifference(timeBefore);
        }
        return mins;
    }

    public int hourDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.hourDifference(this);
        int hours = this.hour - timeBefore.hour;
        if (hours < 0) {
            hours += 24 * dayDifference(timeBefore);
        }
        return hours;
    }

    public int dayDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.dayDifference(this);
        int days = this.date - timeBefore.date;
        if (days < 0) {
            days += timeBefore.month.getDays(timeBefore.year) * monthDifference(timeBefore);
        }
        return days;
    }

    public int monthDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.monthDifference(this);
        int months = this.month.getIndex() - timeBefore.month.getIndex();
        if (months < 0) {
            months += 12 * yearDifference(timeBefore);
        }
        return months;
    }

    public int yearDifference(TimeStamp timeBefore) {
        if (this.isBefore(timeBefore)) return timeBefore.yearDifference(this);
        return this.year - timeBefore.year;
    }

    public boolean isAfter(TimeStamp other) {
        return other.isBefore(this);
    }

    public boolean isInPast() {
        return this.isBefore(new TimeStamp());
    }

    public boolean isInFuture() {
        return this.isAfter(new TimeStamp());
    }
}
