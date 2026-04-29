package server.io;

import shared.exceptions.InvalidDataException;
import shared.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class XMLParser {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final int PERSON_TAG_LENGTH = "<person>".length();

    public HashSet<Person> parseToCollection(String xmlContent) throws InvalidDataException {
        HashSet<Person> collection = new HashSet<>();

        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return collection;
        }

        try {
            String content = xmlContent.replaceFirst("<\\?xml.*\\?>", "").trim();
            String[] personBlocks = extractPersonBlocks(content);

            for (String personBlock : personBlocks) {
                if (personBlock.trim().isEmpty()) continue;
                Person person = parsePerson(personBlock);
                collection.add(person);
            }
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка парсинга XML: " + e.getMessage(), e);
        }

        return collection;
    }

    public String parseToXML(HashSet<Person> collection) {
        StringBuilder xml = new StringBuilder();
        xml.append(XML_HEADER);
        xml.append("<persons>\n");

        for (Person person : collection) {
            xml.append("  <person>\n");
            xml.append("    <id>").append(person.getId()).append("</id>\n");
            xml.append("    <name>").append(escapeXml(person.getName())).append("</name>\n");

            Coordinates coords = person.getCoordinates();
            xml.append("    <coordinates>\n");
            xml.append("      <x>").append(coords.getX()).append("</x>\n");
            xml.append("      <y>").append(coords.getY()).append("</y>\n");
            xml.append("    </coordinates>\n");

            xml.append("    <creationDate>").append(person.getCreationDate()).append("</creationDate>\n");
            xml.append("    <height>").append(person.getHeight()).append("</height>\n");

            if (person.getBirthday() != null) {
                xml.append("    <birthday>").append(formatDate(person.getBirthday())).append("</birthday>\n");
            } else {
                xml.append("    <birthday>null</birthday>\n");
            }

            if (person.getHairColor() != null) {
                xml.append("    <hairColor>").append(person.getHairColor()).append("</hairColor>\n");
            } else {
                xml.append("    <hairColor>null</hairColor>\n");
            }

            if (person.getNationality() != null) {
                xml.append("    <nationality>").append(person.getNationality()).append("</nationality>\n");
            } else {
                xml.append("    <nationality>null</nationality>\n");
            }

            if (person.getLocation() != null) {
                Location loc = person.getLocation();
                xml.append("    <location>\n");
                xml.append("      <x>").append(loc.getX()).append("</x>\n");
                xml.append("      <y>").append(loc.getY()).append("</y>\n");
                String name = loc.getName();
                xml.append("      <name>").append(name != null ? escapeXml(name) : "null").append("</name>\n");
                xml.append("    </location>\n");
            } else {
                xml.append("    <location>null</location>\n");
            }

            xml.append("  </person>\n");
        }

        xml.append("</persons>");
        return xml.toString();
    }

    private boolean isNullValue(String value) {
        return value == null || value.isEmpty() || "null".equals(value);
    }

    private String[] extractPersonBlocks(String xml) {
        String[] blocks = xml.split("</person>");
        for (int i = 0; i < blocks.length; i++) {
            int startIndex = blocks[i].lastIndexOf("<person>");
            if (startIndex >= 0) {
                blocks[i] = blocks[i].substring(startIndex + PERSON_TAG_LENGTH).trim();
            } else {
                blocks[i] = "";
            }
        }
        return blocks;
    }

    private Coordinates parseCoordinates(String coordsBlock) throws InvalidDataException {
        Coordinates coords = new Coordinates();

        String xStr = extractTag(coordsBlock, "x");
        if (isNullValue(xStr)) {
            throw new InvalidDataException("Поле coordinates.x не может быть null");
        }
        try {
            coords.setX(Integer.parseInt(xStr));
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Поле coordinates.x должно быть числом");
        }

        String yStr = extractTag(coordsBlock, "y");
        if (isNullValue(yStr)) {
            throw new InvalidDataException("Поле coordinates.y не может быть null");
        }
        try {
            coords.setY(Integer.parseInt(yStr));
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Поле coordinates.y должно быть числом");
        }

        return coords;
    }

    private Location parseLocation(String locationBlock) throws InvalidDataException {
        Location location = new Location();

        String xLocStr = extractTag(locationBlock, "x");
        if (!isNullValue(xLocStr)) {
            try {
                location.setX(Float.parseFloat(xLocStr));
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Поле location.x должно быть числом");
            }
        } else {
            throw new InvalidDataException("Поле location.x не может быть null");
        }

        String yLocStr = extractTag(locationBlock, "y");
        if (!isNullValue(yLocStr)) {
            try {
                location.setY(Long.parseLong(yLocStr));
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Поле location.y должно быть числом");
            }
        } else {
            throw new InvalidDataException("Поле location.y не может быть null");
        }

        String nameLocStr = extractTag(locationBlock, "name");
        if (!"null".equals(nameLocStr)) {
            if (nameLocStr.isEmpty()) {
                location.setName(null);
            } else {
                location.setName(nameLocStr);
            }
        }

        return location;
    }

    private Person parsePerson(String personBlock) throws InvalidDataException {
        Person person = new Person();

        try {
            String idStr = extractTag(personBlock, "id");
            if (!isNullValue(idStr)) {
                try {
                    int id = Integer.parseInt(idStr);
                    person.setId(id);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("Неверный формат ID: " + idStr);
                }
            }

            String nameStr = extractTag(personBlock, "name");
            if (isNullValue(nameStr)) {
                throw new InvalidDataException("Поле name не может быть null или пустым");
            }
            person.setName(nameStr);

            String coordsBlock = extractTag(personBlock, "coordinates");
            if (isNullValue(coordsBlock)) {
                throw new InvalidDataException("Поле coordinates не может быть null");
            }
            person.setCoordinates(parseCoordinates(coordsBlock));

            String heightStr = extractTag(personBlock, "height");
            if (isNullValue(heightStr)) {
                throw new InvalidDataException("Поле height не может быть null");
            }
            try {
                person.setHeight(Float.parseFloat(heightStr));
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Поле height должно быть числом");
            }

            String creationDateStr = extractTag(personBlock, "creationDate");
            if (!isNullValue(creationDateStr)) {
                try {
                    person.setCreationDate(LocalDate.parse(creationDateStr));
                } catch (DateTimeParseException e) {
                    throw new InvalidDataException("Неверный формат даты создания: " + creationDateStr);
                }
            }

            String birthdayStr = extractTag(personBlock, "birthday");
            if (!isNullValue(birthdayStr)) {
                try {
                    person.setBirthday(parseDate(birthdayStr));
                } catch (ParseException e) {
                    throw new InvalidDataException("Неверный формат даты рождения: " + birthdayStr);
                }
            }

            String hairColorStr = extractTag(personBlock, "hairColor");
            if (!isNullValue(hairColorStr)) {
                try {
                    person.setHairColor(Color.valueOf(hairColorStr));
                } catch (IllegalArgumentException e) {
                    throw new InvalidDataException("Неверное значение цвета волос: " + hairColorStr);
                }
            }

            String nationalityStr = extractTag(personBlock, "nationality");
            if (!isNullValue(nationalityStr)) {
                try {
                    person.setNationality(Country.valueOf(nationalityStr));
                } catch (IllegalArgumentException e) {
                    throw new InvalidDataException("Неверное значение национальности: " + nationalityStr);
                }
            }

            String locationBlock = extractTag(personBlock, "location");
            if (!isNullValue(locationBlock)) {
                person.setLocation(parseLocation(locationBlock));
            }

        } catch (InvalidDataException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Ошибка валидации данных: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidDataException("Ошибка парсинга данных: " + e.getMessage());
        }

        return person;
    }

    private String extractTag(String xml, String tag) {
        if (xml == null || tag == null) return "";

        String openTag = "<" + tag + ">";
        String closeTag = "</" + tag + ">";

        int start = xml.indexOf(openTag);
        if (start == -1) {
            return "";
        }

        int contentStart = start + openTag.length();
        int end = xml.indexOf(closeTag, contentStart);
        if (end == -1) {
            return "";
        }

        return xml.substring(contentStart, end).trim();
    }

    private Date parseDate(String str) throws ParseException {
        if (str == null || str.isEmpty() || "null".equals(str)) {
            return null;
        }
        return DATE_FORMAT.parse(str);
    }

    private String formatDate(Date date) {
        if (date == null) return "null";
        try {
            return DATE_FORMAT.format(date);
        } catch (Exception e) {
            return "null";
        }
    }

    private String escapeXml(String text) {
        if (text == null) return "null";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
