package server.commands;

import server.collection.CollectionManager;

public class InfoCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public InfoCommand(CollectionManager collectionManager) {
        super(collectionManager, "info", "вывести информацию о коллекции");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "info")) return;

        String info = collectionManager.getInfo();

        if (resultCapture != null) {
            resultCapture.append(info);
        } else {
            System.out.println(info);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}