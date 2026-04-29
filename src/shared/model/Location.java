package shared.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Float x;
    private long y;
    private String name;

    public Location(Float x, long y, String name) {
        setX(x);
        this.y = y;
        this.name = name;
    }

    public Location() {
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        if (x == null) {
            throw new IllegalArgumentException("Поле x не может быть null");
        }
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return y == location.y &&
                Objects.equals(x, location.x) &&
                Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name);
    }

    @Override
    public String toString() {
        return String.format("Location{x=%.2f, y=%d, name='%s'}",
                x, y, name != null ? name : "null");
    }
}