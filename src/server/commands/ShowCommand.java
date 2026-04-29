package server.commands;

import server.collection.CollectionManager;

public class ShowCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public ShowCommand(CollectionManager collectionManager) {
        super(collectionManager, "show", "вывести все элементы коллекции");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "show")) return;

        if (resultCapture != null) {
            resultCapture.append("");
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}