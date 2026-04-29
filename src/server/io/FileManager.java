package server.io;

import shared.exceptions.FileAccessException;
import shared.exceptions.InvalidDataException;
import shared.model.Person;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

public class FileManager {
    private final File file;
    private final XMLParser xmlParser;

    public FileManager(String envVarName) throws FileAccessException {
        String fileName = System.getenv(envVarName);
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new FileAccessException("Переменная окружения " + envVarName + " не установлена");
        }
        this.file = new File(fileName);
        this.xmlParser = new XMLParser();
    }

    public HashSet<Person> loadCollection() throws FileAccessException, InvalidDataException {
        StringBuilder content = new StringBuilder();

        if (!file.exists()) {
            throw new FileAccessException("Файл не существует: " + file.getPath());
        }
        if (!file.canRead()) {
            throw new FileAccessException("Нет прав на чтение файла: " + file.getPath());
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new FileAccessException("Файл не найден: " + file.getPath(), e);
        } catch (SecurityException e) {
            throw new FileAccessException("Ошибка безопасности при чтении файла: " + file.getPath(), e);
        }

        if (content.isEmpty()) {
            return new HashSet<>();
        }

        return xmlParser.parseToCollection(content.toString());
    }


    public void saveCollection(HashSet<Person> collection) throws FileAccessException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new FileAccessException("Не удалось создать директорию: " + parentDir);
            }
        }

        if (file.exists() && !file.canWrite()) {
            throw new FileAccessException("Нет прав на запись в файл: " + file.getPath());
        }

        String xmlContent = xmlParser.parseToXML(collection);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(xmlContent);
        } catch (IOException e) {
            throw new FileAccessException("Ошибка при записи в файл: " + file.getPath(), e);
        }
    }

    public boolean fileExists() {
        return file.exists();
    }

    public boolean canRead() {
        return file.exists() && file.canRead();
    }

}