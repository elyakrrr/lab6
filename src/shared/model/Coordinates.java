package shared.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer x;
    private Integer y;

    public Coordinates(Integer x, Integer y) {
        setX(x);
        setY(y);
    }

    public Coordinates() {
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        if (x == null) {
            throw new IllegalArgumentException("Поле x не может быть null");
        }
        if (x <= -746) {
            throw new IllegalArgumentException("Значение поля x должно быть больше -746");
        }
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        if (y == null) {
            throw new IllegalArgumentException("Поле y не может быть null");
        }
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Coordinates{x=%d, y=%d}", x, y);
    }
}
