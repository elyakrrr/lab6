package server.commands;

import server.collection.CollectionManager;

public class ClearCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public ClearCommand(CollectionManager collectionManager) {
        super(collectionManager, "clear", "очистить коллекцию");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "clear")) return;

        int size = collectionManager.size();
        collectionManager.clear();

        String msg = "Коллекция очищена. Удалено элементов: " + size;
        if (resultCapture != null) resultCapture.append(msg);
        else System.out.println(msg);
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}
