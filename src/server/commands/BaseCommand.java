package server.commands;

import server.collection.CollectionManager;
import shared.utils.Validator;

public abstract class BaseCommand implements Command {
    protected final CollectionManager collectionManager;
    protected final String name;
    protected final String description;

    public BaseCommand(CollectionManager collectionManager, String name, String description) {
        this.collectionManager = collectionManager;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    protected boolean validateArgs(String[] args, int expectedCount, String usage) {
        if (args.length != expectedCount) {
            System.out.println("Ошибка: неверное количество аргументов");
            System.out.println("Использование: " + usage);
            return false;
        }
        return true;
    }

    protected Integer parseId(String idStr) {
        try {
            return Validator.validateInteger(idStr, "ID", 0, false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, description);
    }
}