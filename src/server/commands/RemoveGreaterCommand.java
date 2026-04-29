package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;

public class RemoveGreaterCommand extends BaseCommand implements CommandInvoker.ElementCommand, CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public RemoveGreaterCommand(CollectionManager collectionManager) {
        super(collectionManager, "remove_greater", "удалить элементы, превышающие заданный");
    }

    @Override
    public void execute(String[] args) {
        String msg = "Ошибка: команда remove_greater должна использоваться с элементом";
        if (resultCapture != null) resultCapture.append(msg);
    }

    @Override
    public void executeWithElement(String[] args, Person person) {
        if (person == null) {
            String msg = "Ошибка: не передан объект Person";
            if (resultCapture != null) resultCapture.append(msg);
            return;
        }

        int removed = collectionManager.removeGreater(person);
        String msg = "Удалено элементов: " + removed;
        if (resultCapture != null) resultCapture.append(msg);
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}