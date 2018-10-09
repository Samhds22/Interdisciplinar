package com.ceunsp.app.projeto.Activity.Calendar;

import android.support.annotation.Nullable;

public class Events {

    private Long id;
    private int color;
    private long timeInMillis;
    private Object data;

    public Events(int color, long timeInMillis) {
        this.color = color;
        this.timeInMillis = timeInMillis;
    }

    public Events(Long id,int color, long timeInMillis, Object data) {
        this.id = id;
        this.color = color;
        this.timeInMillis = timeInMillis;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Events events = (Events) o;

        if (color != events.color) return false;
        if (timeInMillis != events.timeInMillis) return false;
        if (data != null ? !data.equals(events.data) : events.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeInMillis=" + timeInMillis +
                ", data=" + data +
                '}';
    }
}

