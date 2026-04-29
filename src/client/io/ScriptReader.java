package client.io;

import shared.utils.Validator;
import shared.model.*;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class ScriptReader implements AutoCloseable {
    private final Scanner scanner;

    public ScriptReader(File file) throws FileNotFoundException {
        this.scanner = new Scanner(file);
    }

    public String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return null;
    }

    private String readRequiredLine(String fieldName) throws IOException {
        String line = readLine();
        if (line == null) {
            throw new IOException("Неожиданный конец файла при чтении " + fieldName);
        }
        return line;
    }

    /**
     * Читает объект Person из скрипта.
     */
    public Person readPersonFromScript() throws IOException {
        try {
            String name = Validator.validateNotEmpty(readRequiredLine("имени"), "Имя");
            Coordinates coordinates = readCoordinatesFromScript();

            String heightLine = readRequiredLine("роста");
            float height = Validator.validateFloat(heightLine, "Рост", 0f, false);

            String birthdayLine = readLine();
            LocalDate localDate = Validator.validateDate(birthdayLine);
            Date birthday = (localDate != null) ? java.sql.Date.valueOf(localDate) : null;

            String hairColorLine = readLine();
            Color hairColor = Validator.validateColor(hairColorLine);

            String nationalityLine = readLine();
            Country nationality = Validator.validateCountry(nationalityLine);

            String locationMarker = readLine();
            Location location;
            if (locationMarker == null || locationMarker.equals("null")) {
                location = null;
            } else {
                location = readLocationFromScript(locationMarker);
            }

            return new Person(name, coordinates, height, birthday, hairColor, nationality, location);

        } catch (IllegalArgumentException e) {
            throw new IOException("Ошибка валидации данных в скрипте: " + e.getMessage());
        }
    }

    private Coordinates readCoordinatesFromScript() throws IOException {
        try {
            String xLine = readRequiredLine("X координаты");
            Integer x = Validator.validateInteger(xLine, "X координаты", -746, false);

            String yLine = readRequiredLine("Y координаты");
            Integer y = Validator.validateInteger(yLine, "Y координаты", null, false);

            return new Coordinates(x, y);
        } catch (IllegalArgumentException e) {
            throw new IOException("Ошибка валидации координат: " + e.getMessage());
        }
    }

    private Location readLocationFromScript(String firstLine) throws IOException {
        try {
            Float x = Validator.validateFloat(firstLine, "X локации", null, false);

            String yLine = readRequiredLine("Y локации");
            Long y = Validator.validateLong(yLine, "Y локации", null, false);

            String nameLine = readLine();
            String name = (nameLine == null || nameLine.isEmpty() || "null".equals(nameLine)) ? null : nameLine;

            return new Location(x, y, name);
        } catch (IllegalArgumentException e) {
            throw new IOException("Ошибка валидации локации: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
