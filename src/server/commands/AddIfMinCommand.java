package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;

public class AddIfMinCommand extends BaseCommand implements CommandInvoker.ElementCommand, CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public AddIfMinCommand(CollectionManager collectionManager) {
        super(collectionManager, "add_if_min", "добавить элемент, если его значение меньше наименьшего");
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Ошибка: команда add_if_min должна использоваться с элементом");
    }

    @Override
    public void executeWithElement(String[] args, Person person) {
        processAddIfMin(person);
    }

    private void processAddIfMin(Person person) {
        if (person == null) {
            String msg = "Ошибка: не передан объект Person";
            if (resultCapture != null) resultCapture.append(msg);
            else System.out.println(msg);
            return;
        }

        if (collectionManager.addIfMin(person)) {
            String msg = "Элемент добавлен (он меньше минимального). ID: " + person.getId();
            if (resultCapture != null) resultCapture.append(msg);
            else System.out.println(msg);
        } else {
            String msg = "Элемент не добавлен (он не меньше минимального)";
            if (resultCapture != null) resultCapture.append(msg);
            else System.out.println(msg);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}