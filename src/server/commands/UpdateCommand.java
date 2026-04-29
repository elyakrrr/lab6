package server.commands;

import server.collection.CollectionManager;
import shared.model.Person;

public class UpdateCommand extends BaseCommand implements CommandInvoker.ElementCommand, CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public UpdateCommand(CollectionManager collectionManager) {
        super(collectionManager, "update", "update id - обновить значение элемента по id");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) return;

        Integer id;
        try {
            id = parseId(args[0]);
        } catch (IllegalArgumentException e) {
            return;
        }

        if (collectionManager.getById(id) == null) {
            if (resultCapture != null) resultCapture.append("Элемент с ID " + id + " не найден");
        }
    }

    @Override
    public void executeWithElement(String[] args, Person person) {
        if (!validateArgs(args, 1, "update id")) return;

        Integer id;
        try {
            id = parseId(args[0]);
        } catch (IllegalArgumentException e) {
            return;
        }

        Person existing = collectionManager.getById(id);
        if (existing == null) {
            String msg = "Элемент с ID " + id + " не найден";
            if (resultCapture != null) resultCapture.append(msg);
            return;
        }

        if (collectionManager.update(id, person)) {
            String msg = "Элемент с ID " + id + " успешно обновлен";
            if (resultCapture != null) resultCapture.append(msg);
        } else {
            String msg = "Ошибка при обновлении элемента";
            if (resultCapture != null) resultCapture.append(msg);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}