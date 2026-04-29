package server.commands;

import server.collection.CollectionManager;

public class RemoveByIdCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        super(collectionManager, "remove_by_id", "remove_by_id id - удалить элемент по id");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 1, "remove_by_id id")) return;

        Integer id;
        try {
            id = parseId(args[0]);
        } catch (IllegalArgumentException e) {
            return;
        }

        if (collectionManager.removeById(id)) {
            String msg = "Элемент с ID " + id + " успешно удален";
            if (resultCapture != null) resultCapture.append(msg);
        } else {
            String msg = "Элемент с ID " + id + " не найден";
            if (resultCapture != null) resultCapture.append(msg);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}