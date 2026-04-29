package shared.model;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Person implements Comparable<Person>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private float height;
    private Date birthday;
    private Color hairColor;
    private Country nationality;
    private Location location;

    /**
     * Конструктор для создания человека.
     * @param name имя (не null, не пустое)
     * @param coordinates координаты (не null)
     * @param height рост (> 0)
     * @param birthday дата рождения (может быть null)
     * @param hairColor цвет волос (может быть null)
     * @param nationality национальность (может быть null)
     * @param location локация (может быть null)
     * @throws IllegalArgumentException при неверных данных
     */
    public Person(String name, Coordinates coordinates, float height,
                  Date birthday, Color hairColor, Country nationality, Location location) {
        this.creationDate = LocalDate.now();
        setName(name);
        setCoordinates(coordinates);
        setHeight(height);
        setBirthday(birthday);
        setHairColor(hairColor);
        setNationality(nationality);
        setLocation(location);
    }

    public Person() {
        this.creationDate = LocalDate.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть > 0");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть null или пустым");
        }
        this.name = name.trim();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null");
        }
        this.creationDate = creationDate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Рост должен быть > 0");
        }
        this.height = height;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int compareTo(Person other) {
        return Float.compare(this.height, other.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String birthdayStr = birthday != null ? DATE_FORMAT.format(birthday) : "null";
        String hairColorStr = hairColor != null ? hairColor.name() : "null";
        String nationalityStr = nationality != null ? nationality.name() : "null";
        String locationStr = location != null ? location.toString() : "null";

        return String.format(
                "Person{id=%d, name='%s', coordinates=%s, creationDate=%s, height=%.2f, " +
                        "birthday=%s, hairColor=%s, nationality=%s, location=%s}",
                id, name, coordinates, creationDate, height,
                birthdayStr, hairColorStr, nationalityStr, locationStr
        );
    }
}