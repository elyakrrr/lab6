package server.handler;

import server.collection.CollectionManager;
import server.commands.CommandInvoker;
import server.commands.CommandType;
import shared.exceptions.FileAccessException;
import shared.model.Person;
import shared.network.Request;
import shared.network.Response;

import java.util.logging.Logger;

public class RequestHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private final CommandInvoker commandInvoker;
    private final CollectionManager collectionManager;

    public RequestHandler(CommandInvoker commandInvoker, CollectionManager collectionManager) {
        this.commandInvoker = commandInvoker;
        this.collectionManager = collectionManager;
    }

    public Response handle(Request request) {
        String commandNameStr = request.getCommandName();
        String[] args = request.getArgs();
        Person person = request.getPerson();

        CommandType commandType = CommandType.fromString(commandNameStr);

        if (commandType == null) {
            logger.warning("Неизвестная команда: " + commandNameStr);
            return new Response.Builder()
                    .success(false)
                    .message("Неизвестная команда: " + commandNameStr)
                    .build();
        }

        logger.info("Выполнение команды: " + commandType);

        try {
            Object result = commandInvoker.executeCommand(commandType, args, person);
            Response.Builder builder = new Response.Builder().success(true);

            switch (commandType) {
                case SHOW:
                    builder.collection(collectionManager.getSortedByLocation());
                    break;

                case INFO:
                    builder.info(collectionManager.getInfo());
                    break;

                case PRINT_UNIQUE_NATIONALITY:
                case PRINT_FIELD_DESCENDING_BIRTHDAY:
                case COUNT_LESS_THAN_NATIONALITY:
                    if (result != null) {
                        builder.data(result);
                    }
                    break;

                default:
                    if (result != null && !result.toString().isEmpty()) {
                        String message = result.toString();
                        if (message.startsWith("Ошибка") || message.contains("не найден")) {
                            builder.success(false);
                        }
                        builder.message(message);
                    }
                    break;
            }

            return builder.build();

        } catch (IllegalArgumentException e) {
            logger.warning("Ошибка валидации: " + e.getMessage());
            return new Response.Builder()
                    .success(false)
                    .message("Ошибка валидации: " + e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.severe("Ошибка выполнения команды: " + e.getMessage());
            return new Response.Builder()
                    .success(false)
                    .message("Ошибка выполнения команды: " + e.getMessage())
                    .build();
        }
    }

    public void saveCollection() throws FileAccessException {
        commandInvoker.saveCollection();
    }
}