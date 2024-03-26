package ru.task.three;

import java.util.Objects;

public class StateObject {
    private String object;
    private long startTime;

    public StateObject(String object, long startTime) {
        this.object = object;
        this.startTime = startTime;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final StateObject other = (StateObject) obj;
        return Objects.equals(this.object, other.object);
    }

    @Override
    public String toString() {
        return "StateObject{" +
                "object='" + object + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
