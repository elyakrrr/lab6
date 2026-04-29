package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;

public class RemoveLowerCommand extends BaseCommand implements CommandInvoker.ElementCommand, CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public RemoveLowerCommand(CollectionManager collectionManager) {
        super(collectionManager, "remove_lower", "удалить элементы, меньшие чем заданный");
    }

    @Override
    public void execute(String[] args) {
        String msg = "Ошибка: команда remove_lower должна использоваться с элементом";
        if (resultCapture != null) resultCapture.append(msg);
    }

    @Override
    public void executeWithElement(String[] args, Person person) {
        if (person == null) {
            String msg = "Ошибка: не передан объект Person";
            if (resultCapture != null) resultCapture.append(msg);
            return;
        }

        int removed = collectionManager.removeLower(person);
        String msg = "Удалено элементов: " + removed;
        if (resultCapture != null) resultCapture.append(msg);
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}