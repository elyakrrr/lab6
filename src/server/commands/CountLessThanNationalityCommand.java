package server.commands;

import server.collection.CollectionManager;
import shared.model.Country;
import shared.utils.Validator;

public class CountLessThanNationalityCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public CountLessThanNationalityCommand(CollectionManager collectionManager) {
        super(collectionManager, "count_less_than_nationality",
                "count_less_than_nationality nationality - вывести количество элементов, значение поля nationality которых меньше заданного");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 1, "count_less_than_nationality nationality")) return;

        try {
            Country nationality = Validator.validateCountry(args[0]);
            long count = collectionManager.countLessThanNationality(nationality);
            String msg = "Количество элементов с национальностью меньше " + nationality + ": " + count;
            if (resultCapture != null) resultCapture.append(msg);
        } catch (IllegalArgumentException e) {
            String msg = "Ошибка: " + e.getMessage();
            if (resultCapture != null) resultCapture.append(msg);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}