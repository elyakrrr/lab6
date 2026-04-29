package client;

import client.io.ScriptExecutor;
import client.io.UserInputReader;
import client.handler.ResponseHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientApp {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private final Client client;
    private final UserInputReader inputReader;
    private final ScriptExecutor scriptExecutor;
    private final Scanner scanner;
    private boolean running;

    public ClientApp(String host, int port) throws SocketException {
        this.client = new Client(host, port);
        this.scanner = new Scanner(System.in);
        this.inputReader = new UserInputReader(scanner);
        this.scriptExecutor = new ScriptExecutor(this);
        this.running = true;
    }

    public void start() {
        ResponseHandler responseHandler = new ResponseHandler();
        CommandProcessor commandProcessor = new CommandProcessor(client, inputReader, responseHandler);

        System.out.println("Клиент запущен. Сервер: " + client.getHost() + ":" + client.getPort());
        System.out.println("Введите 'help' для списка команд");
        System.out.println("Введите 'exit' для выхода");

        while (running) {
            System.out.print("> ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+");
                String commandName = parts[0].toLowerCase();
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);

                if (handleSpecialCommand(commandName, args)) {
                    continue;
                }

                commandProcessor.process(commandName, args);

            } catch (NoSuchElementException e) {
                System.out.println("Обнаружен конец ввода (Ctrl + D). Завершение...");
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка десериализации ответа: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Ошибка сети: " + e.getMessage());
                System.out.println("Попробуйте снова позже или проверьте подключение к серверу");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        shutdown();
    }

    private boolean handleSpecialCommand(String commandName, String[] args) {
        switch (commandName) {
            case "exit" -> { handleExit(); return true; }
            case "save" -> { handleSave(); return true; }
            case "execute_script" -> { handleExecuteScript(args); return true; }
            default -> { return false; }
        }
    }

    private void handleExit() {
        running = false;
        System.out.println("Завершение клиентского приложения...");
    }

    private void handleSave() {
        System.out.println("Команда save недоступна на клиенте");
    }

    private void handleExecuteScript(String[] args) {
        if (args.length != 1) {
            System.out.println("Использование: execute_script file_name");
            return;
        }
        scriptExecutor.execute(args[0]);
    }

    private void shutdown() {
        client.close();
        scanner.close();
        System.out.println("Клиент завершён.");
    }

    public void stop() {
        this.running = false;
    }

    public Client getClient() {
        return client;
    }

    public static void main(String[] args) {
        try {
            new ClientApp(DEFAULT_HOST, DEFAULT_PORT).start();
        } catch (SocketException e) {
            System.err.println("Ошибка создания клиента: " + e.getMessage());
        }
    }
}