package client.io;

import client.ClientApp;
import shared.model.Person;
import shared.network.Response;
import client.CommandProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ScriptExecutor {

    private static final String CMD_EXECUTE_SCRIPT = "execute_script";
    private static final String CMD_EXIT = "exit";
    private static final String CMD_SAVE = "save";

    private final ClientApp clientApp;
    private final Set<String> executingScripts;

    public ScriptExecutor(ClientApp clientApp) {
        this.clientApp = clientApp;
        this.executingScripts = new HashSet<>();
    }

    public void execute(String fileName) {
        File file = new File(fileName);

        if (isRecursive(file)) {
            System.out.println("Ошибка: обнаружена рекурсия в скрипте " + fileName);
            return;
        }

        if (!validateFile(file, fileName)) {
            return;
        }

        executingScripts.add(file.getAbsolutePath());

        try (ScriptReader scriptReader = new ScriptReader(file)) {
            System.out.println("Выполнение скрипта: " + fileName);
            executeScriptLines(scriptReader);

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден - " + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка чтения скрипта: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка десериализации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
        } finally {
            executingScripts.remove(file.getAbsolutePath());
        }
    }

    private boolean isRecursive(File file) {
        return executingScripts.contains(file.getAbsolutePath());
    }

    private boolean validateFile(File file, String fileName) {
        if (!file.exists()) {
            System.out.println("Ошибка: файл " + fileName + " не существует");
            return false;
        }
        if (!file.canRead()) {
            System.out.println("Ошибка: нет прав на чтение файла " + fileName);
            return false;
        }
        return true;
    }

    private void executeScriptLines(ScriptReader reader) throws IOException, ClassNotFoundException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                processLine(line, reader);
            } catch (IOException e) {
                System.out.println("Ошибка ввода-вывода: " + e.getMessage());
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Ошибка десериализации: " + e.getMessage());
                break;
            }
        }
    }

    private void processLine(String line, ScriptReader reader) throws IOException, ClassNotFoundException {
        String[] parts = line.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        if (handleSpecialCommand(commandName, args)) {
            return;
        }

        Person person = null;
        if (CommandProcessor.COMMANDS_WITH_ELEMENT.contains(commandName)) {
            person = reader.readPersonFromScript();
        }

        try {
            Response response = clientApp.getClient().sendRequest(commandName, args, person);
            if (!response.isSuccess()) {
                System.out.println("Ошибка в команде " + commandName + ": " + response.getMessage());
            } else if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                System.out.println(response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Ошибка сети при выполнении команды " + commandName + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка десериализации при выполнении команды " + commandName + ": " + e.getMessage());
        }
    }

    private boolean handleSpecialCommand(String commandName, String[] args) {
        switch (commandName) {
            case CMD_EXECUTE_SCRIPT -> {
                handleExecuteScript(args);
                return true;
            }
            case CMD_EXIT -> {
                handleExit();
                return true;
            }
            case CMD_SAVE -> {
                handleSave();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void handleExecuteScript(String[] args) {
        if (args.length == 1) {
            execute(args[0]);
        } else {
            System.out.println("Неверное использование execute_script в скрипте");
        }
    }

    private void handleExit() {
        System.out.println("Команда exit в скрипте. Выполнение скрипта прервано");
        clientApp.stop();
        System.out.println("Клиент завершён");
    }

    private void handleSave() {
        System.out.println("Команда save недоступна на клиенте");
    }
}