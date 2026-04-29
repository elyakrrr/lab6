package server.collection;

import shared.model.Country;
import shared.model.Location;
import shared.model.Person;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final HashSet<Person> collection;
    private final LocalDateTime initializationDate;

    public CollectionManager() {
        this.collection = new HashSet<>();
        this.initializationDate = LocalDateTime.now();
    }

    public HashSet<Person> getAll() {
        return new HashSet<>(collection);
    }

    public String getInfo() {
        return "Тип коллекции: " + collection.getClass().getName() + "\n" +
                "Дата инициализации: " + initializationDate + "\n" +
                "Количество элементов: " + collection.size() + "\n" +
                "Тип элементов: " + Person.class.getName();
    }

    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }


    /**
     * Возвращает отсортированный список элементов по местоположению.
     * Сравнение: сначала по x, затем по y, затем по имени.
     */
    public List<Person> getSortedByLocation() {
        return collection.stream()
                .sorted(this::compareByLocation)
                .collect(Collectors.toList());
    }

    private int compareByLocation(Person p1, Person p2) {
        Location loc1 = p1.getLocation();
        Location loc2 = p2.getLocation();

        if (loc1 == null && loc2 == null) return 0;
        if (loc1 == null) return -1;
        if (loc2 == null) return 1;

        int xCompare = Float.compare(loc1.getX(), loc2.getX());
        if (xCompare != 0) return xCompare;

        int yCompare = Long.compare(loc1.getY(), loc2.getY());
        if (yCompare != 0) return yCompare;

        String name1 = loc1.getName() == null ? "" : loc1.getName();
        String name2 = loc2.getName() == null ? "" : loc2.getName();
        return name1.compareTo(name2);
    }

    public Person getById(Integer id) {
        if (id == null) return null;
        return collection.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }

    public boolean containsId(Integer id) {
        return getById(id) != null;
    }

    public Person getMin() {
        return collection.stream()
                .min(Person::compareTo)
                .orElse(null);
    }

    public boolean add(Person person) {
        if (person == null) return false;

        if (person.getId() == null || containsId(person.getId())) {
            person.setId(generateNewId());
        }
        return collection.add(person);
    }

    public boolean update(Integer id, Person newPerson) {
        if (id == null || newPerson == null) return false;

        Person existing = getById(id);
        if (existing == null) return false;

        collection.remove(existing);
        newPerson.setId(id);
        return collection.add(newPerson);
    }

    public boolean removeById(Integer id) {
        if (id == null) return false;
        return collection.removeIf(p -> id.equals(p.getId()));
    }

    public void clear() {
        collection.clear();
    }

    public boolean addIfMin(Person person) {
        if (person == null) return false;

        Person min = getMin();
        if (min == null || person.compareTo(min) < 0) {
            return add(person);
        }
        return false;
    }

    public int removeGreater(Person person) {
        if (person == null) return 0;
        int before = collection.size();
        collection.removeIf(p -> p.compareTo(person) > 0);
        return before - collection.size();
    }

    public int removeLower(Person person) {
        if (person == null) return 0;
        int before = collection.size();
        collection.removeIf(p -> p.compareTo(person) < 0);
        return before - collection.size();
    }

    public long countLessThanNationality(Country nationality) {
        if (nationality == null) return 0;
        return collection.stream()
                .map(Person::getNationality)
                .filter(Objects::nonNull)
                .filter(n -> n.ordinal() < nationality.ordinal())
                .count();
    }

    public Set<Country> getUniqueNationalities() {
        return collection.stream()
                .map(Person::getNationality)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public List<Date> getBirthdaysDescending() {
        return collection.stream()
                .map(Person::getBirthday)
                .filter(Objects::nonNull)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    public Integer generateNewId() {
        if (collection.isEmpty()) {
            return 1;
        }
        int maxId = collection.stream()
                .mapToInt(Person::getId)
                .max()
                .orElse(0);
        return maxId + 1;
    }

    @Override
    public String toString() {
        return "CollectionManager{size=" + collection.size() + "}";
    }
}