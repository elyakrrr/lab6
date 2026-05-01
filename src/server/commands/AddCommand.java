package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;

public class AddCommand extends BaseCommand implements CommandInvoker.ElementCommand, CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public AddCommand(CollectionManager collectionManager) {
        super(collectionManager, "add", "добавить новый элемент в коллекцию");
    }

    @Override
    public void execute(String[] args) {
        String msg = "Ошибка: команда add должна использоваться с элементом";
        if (resultCapture != null) {
            resultCapture.append(msg);
        }
    }

    @Override
    public void executeWithElement(String[] args, Person person) {
        addPerson(person);
    }

    private void addPerson(Person person) {
        if (person == null) {
            String msg = "Ошибка: не передан объект Person";
            if (resultCapture != null) resultCapture.append(msg);
            return;
        }

        if (collectionManager.add(person)) {
            String msg = "Элемент успешно добавлен. ID: " + person.getId();
            if (resultCapture != null) resultCapture.append(msg);
        } else {
            String msg = "Ошибка при добавлении элемента";
            if (resultCapture != null) resultCapture.append(msg);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}