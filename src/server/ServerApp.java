package server;

import server.collection.CollectionManager;
import server.commands.CommandInvoker;
import server.io.FileManager;
import server.handler.RequestHandler;
import shared.exceptions.FileAccessException;
import shared.exceptions.InvalidDataException;
import shared.model.Person;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final String ENV_VAR_NAME = "PERSON_DATA";

    private CollectionManager collectionManager;
    private FileManager fileManager;
    private Server server;

    public void initialize() {
        try {
            setupLogging();
            logger.info("Инициализация сервера...");

            fileManager = new FileManager(ENV_VAR_NAME);
            collectionManager = new CollectionManager();
            loadData();

            CommandInvoker commandInvoker = new CommandInvoker(collectionManager, fileManager);

            RequestHandler requestHandler = new RequestHandler(commandInvoker, collectionManager);
            server = new Server(requestHandler);

            logger.info("Сервер успешно инициализирован");

        } catch (FileAccessException e) {
            logger.severe("Ошибка доступа к файлу: " + e.getMessage());
            System.err.println("ОШИБКА: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            logger.severe("Ошибка настройки логирования: " + e.getMessage());
            System.err.println("ОШИБКА: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.severe("Неожиданная ошибка: " + e.getMessage());
            System.err.println("ОШИБКА: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupLogging() throws IOException {
        FileHandler fileHandler = new FileHandler("server.log", true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
    }

    private void loadData() {
        if (fileManager == null) return;

        try {
            if (!fileManager.fileExists()) {
                logger.info("Файл не существует. Будет создана пустая коллекция.");
                return;
            }

            if (!fileManager.canRead()) {
                logger.warning("Нет прав на чтение файла.");
                return;
            }

            HashSet<Person> loadedCollection = fileManager.loadCollection();
            for (Person person : loadedCollection) {
                collectionManager.add(person);
            }
            logger.info("Загружено " + loadedCollection.size() + " элементов");

        } catch (FileAccessException e) {
            logger.warning("Ошибка доступа: " + e.getMessage());
        } catch (InvalidDataException e) {
            logger.warning("Ошибка в данных: " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("Доступные команды: save");

        server.start();
    }

    public void shutdown() {
        if (server != null) {
            server.stop();
            server.close();
        }
        logger.info("Сервер остановлен");
    }

    public static void main(String[] args) {
        ServerApp app = new ServerApp();
        app.initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (app.fileManager != null && app.collectionManager != null) {
                    app.fileManager.saveCollection(app.collectionManager.getAll());
                    System.out.println("Коллекция сохранена при завершении");
                }
                app.shutdown();

            } catch (FileAccessException e) {
                System.err.println("Не удалось сохранить коллекцию: " + e.getMessage());
            }
        }));

        app.run();
    }
}