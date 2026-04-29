package client.io;

import shared.utils.Validator;
import shared.model.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class UserInputReader {
    private final Scanner scanner;

    public UserInputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public Person readPerson() {
        System.out.println("*Создание нового объекта Person*");

        String name = readName();
        Coordinates coordinates = readCoordinates();
        float height = readHeight();
        Date birthday = readBirthday();
        Color hairColor = readHairColor();
        Country nationality = readNationality();
        Location location = readLocation();

        return new Person(name, coordinates, height, birthday, hairColor, nationality, location);
    }

    private String readName() {
        while (true) {
            try {
                System.out.print("Введите имя: ");
                String input = scanner.nextLine();
                return Validator.validateNotEmpty(input, "Имя");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Coordinates readCoordinates() {
        System.out.println("*Ввод координат*");
        Integer x = readCoordinateX();
        Integer y = readCoordinateY();
        return new Coordinates(x, y);
    }

    private Integer readCoordinateX() {
        while (true) {
            try {
                System.out.print("Введите координату X: ");
                String input = scanner.nextLine();
                return Validator.validateInteger(input, "X", -746, false);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Integer readCoordinateY() {
        while (true) {
            try {
                System.out.print("Введите координату Y: ");
                String input = scanner.nextLine();
                return Validator.validateInteger(input, "Y", null, false);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private float readHeight() {
        while (true) {
            try {
                System.out.print("Введите рост: ");
                String input = scanner.nextLine();
                return Validator.validateFloat(input, "Рост", 0f, false);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Date readBirthday() {
        while (true) {
            try {
                System.out.print("Введите дату рождения в формате yyyy-MM-dd или пропустите: ");
                String input = scanner.nextLine();

                LocalDate localDate = Validator.validateDate(input);
                if (localDate != null) {
                    return java.sql.Date.valueOf(localDate);
                } else {
                    return null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Color readHairColor() {
        while (true) {
            try {
                System.out.println("Доступные цвета волос: GREEN, RED, BLUE, ORANGE, BROWN");
                System.out.print("Введите цвет волос или пропустите: ");
                String input = scanner.nextLine();
                return Validator.validateColor(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Country readNationality() {
        while (true) {
            try {
                System.out.println("Доступные страны: THAILAND, SOUTH_KOREA, JAPAN");
                System.out.print("Введите национальность или пропустите: ");
                String input = scanner.nextLine();
                return Validator.validateCountry(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private Location readLocation() {
        while (true) {
            System.out.print("Хотите ввести локацию? Введите yes/no: ");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("no") || answer.isEmpty()) {
                return null;
            }

            if (!answer.equals("yes")) {
                System.out.println("Пожалуйста, введите yes/no");
                continue;
            }

            try {
                Float x = readLocationX();
                long y = readLocationY();
                String name = readLocationName();
                return new Location(x, y, name);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка при вводе локации: " + e.getMessage());
                System.out.print("Хотите попробовать снова? Введите yes/no: ");
                String retry = scanner.nextLine().trim().toLowerCase();
                if (!retry.equals("yes")) {
                    return null;
                }
            }
        }
    }

    private Float readLocationX() {
        while (true) {
            try {
                System.out.print("Введите X локации: ");
                String input = scanner.nextLine();
                return Validator.validateFloat(input, "X локации", null, false);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private long readLocationY() {
        while (true) {
            try {
                System.out.print("Введите Y локации: ");
                String input = scanner.nextLine();
                return Validator.validateLong(input, "Y локации", null, false);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private String readLocationName() {
        System.out.print("Введите название локации или пропустите: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }
}