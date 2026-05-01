package client;

import client.io.UserInputReader;
import client.handler.ResponseHandler;
import shared.model.Person;
import shared.network.Response;

import java.io.IOException;
import java.util.Set;

public class CommandProcessor {

    public static final Set<String> COMMANDS_WITH_ELEMENT = Set.of(
            "add", "update", "add_if_min", "remove_greater", "remove_lower"
    );

    private static final Set<String> COMMANDS_NO_ARGS = Set.of(
            "help", "info", "show", "clear", "print_unique_nationality", "print_field_descending_birthday"
    );

    private final Client client;
    private final UserInputReader inputReader;
    private final ResponseHandler responseHandler;

    public CommandProcessor(Client client, UserInputReader inputReader, ResponseHandler responseHandler) {
        this.client = client;
        this.inputReader = inputReader;
        this.responseHandler = responseHandler;
    }

    public void process(String commandName, String[] args) throws IOException, ClassNotFoundException {

        if (COMMANDS_NO_ARGS.contains(commandName)) {
            if (args.length > 0) {
                System.out.println("Ошибка: команда " + commandName + " не принимает аргументы");
                return;
            }
            Response response = client.sendRequest(commandName, new String[0], null);
            responseHandler.handle(response);
            return;
        }

        switch (commandName) {
            case "help" -> processHelp();
            case "remove_by_id" -> processRemoveById(args);
            case "count_less_than_nationality" -> processCountLessThanNationality(args);
            case "update" -> processUpdate(args);
            default -> processDefault(commandName, args);
        }
    }

    private void processHelp() throws IOException, ClassNotFoundException {
        Response response = client.sendRequest("help", new String[0], null);
        String helpMessage = response.getMessage().trim();
        System.out.println(helpMessage);
        System.out.println("execute_script file_name - выполнить скрипт из файла");
        System.out.println("exit - завершить программу");
    }

    private void processRemoveById(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 1) {
            System.out.println("Использование: remove_by_id id");
            return;
        }
        if (!args[0].matches("\\d+")) {
            System.out.println("Ошибка: ID должен быть числом > 0");
            return;
        }
        Response response = client.sendRequest("remove_by_id", args, null);
        responseHandler.handle(response);
    }

    private void processCountLessThanNationality(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 1) {
            System.out.println("Использование: count_less_than_nationality nationality");
            return;
        }
        Response response = client.sendRequest("count_less_than_nationality", args, null);
        responseHandler.handle(response);
    }

    private void processUpdate(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 1) {
            System.out.println("Использование: update id");
            return;
        }
        if (!args[0].matches("\\d+")) {
            System.out.println("Ошибка: ID должен быть числом > 0");
            return;
        }

        Response checkResponse = client.sendRequest("update", args, null);
        if (!checkResponse.isSuccess()) {
            System.out.println(checkResponse.getMessage());
            return;
        }

        Person person = inputReader.readPerson();
        Response response = client.sendRequest("update", args, person);
        responseHandler.handle(response);
    }

    private void processDefault(String commandName, String[] args) throws IOException, ClassNotFoundException {
        Person person = null;

        if (COMMANDS_WITH_ELEMENT.contains(commandName)) {
            if (args.length > 0) {
                System.out.println("Ошибка: команда " + commandName + " не принимает аргументы");
                return;
            }
            person = inputReader.readPerson();
        }

        Response response = client.sendRequest(commandName, args, person);
        responseHandler.handle(response);
    }
}