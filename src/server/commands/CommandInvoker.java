package server.commands;

import server.collection.CollectionManager;
import server.io.FileManager;
import shared.exceptions.FileAccessException;
import shared.model.Person;

import java.util.HashMap;
import java.util.Map;

public class CommandInvoker {
    private final Map<CommandType, Command> commands;
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public CommandInvoker(CollectionManager collectionManager, FileManager fileManager) {
        this.commands = new HashMap<>();
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;

        initializeCommands();
    }

    private void initializeCommands() {
        registerCommand(CommandType.HELP, new HelpCommand(this));
        registerCommand(CommandType.INFO, new InfoCommand(collectionManager));
        registerCommand(CommandType.SHOW, new ShowCommand(collectionManager));
        registerCommand(CommandType.ADD, new AddCommand(collectionManager));
        registerCommand(CommandType.UPDATE, new UpdateCommand(collectionManager));
        registerCommand(CommandType.REMOVE_BY_ID, new RemoveByIdCommand(collectionManager));
        registerCommand(CommandType.CLEAR, new ClearCommand(collectionManager));
        registerCommand(CommandType.ADD_IF_MIN, new AddIfMinCommand(collectionManager));
        registerCommand(CommandType.REMOVE_GREATER, new RemoveGreaterCommand(collectionManager));
        registerCommand(CommandType.REMOVE_LOWER, new RemoveLowerCommand(collectionManager));
        registerCommand(CommandType.COUNT_LESS_THAN_NATIONALITY, new CountLessThanNationalityCommand(collectionManager));
        registerCommand(CommandType.PRINT_UNIQUE_NATIONALITY, new PrintUniqueNationalityCommand(collectionManager));
        registerCommand(CommandType.PRINT_FIELD_DESCENDING_BIRTHDAY, new PrintFieldDescendingBirthdayCommand(collectionManager));
    }

    public void registerCommand(CommandType type, Command command) {
        commands.put(type, command);
    }

    public Object executeCommand(CommandType commandType, String[] args, Person person) {
        Command command = commands.get(commandType);
        if (command == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + commandType);
        }

        CommandResult result = new CommandResult();
        setResultCapture(command, result);

        if (person != null && command instanceof ElementCommand) {
            ((ElementCommand) command).executeWithElement(args, person);
        } else {
            command.execute(args);
        }

        return result.getMessage();
    }

    public void saveCollection() throws FileAccessException {
        fileManager.saveCollection(collectionManager.getAll());
    }

    private void setResultCapture(Command command, CommandResult result) {
        if (command instanceof ResultCapturingCommand) {
            ((ResultCapturingCommand) command).setResultCapture(result);
        }
    }

    public Map<CommandType, Command> getCommands() {
        return commands;
    }

    public interface ResultCapturingCommand {
        void setResultCapture(CommandResult result);
    }

    public interface ElementCommand {
        void executeWithElement(String[] args, Person person);
    }

    public static class CommandResult {
        private final StringBuilder message = new StringBuilder();

        public void append(String text) {
            message.append(text);
        }

        public String getMessage() {
            return message.toString();
        }
    }
}