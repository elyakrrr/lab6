package server.commands;

import server.collection.CollectionManager;
import shared.model.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PrintUniqueNationalityCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private CommandInvoker.CommandResult resultCapture;

    public PrintUniqueNationalityCommand(CollectionManager collectionManager) {
        super(collectionManager, "print_unique_nationality", "вывести уникальные значения поля nationality");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "print_unique_nationality")) return;

        Set<Country> nationalities = collectionManager.getUniqueNationalities();
        StringBuilder sb = new StringBuilder();

        if (nationalities.isEmpty()) {
            sb.append("Нет элементов с национальностью");
        } else {
            sb.append("*Уникальные национальности*\n");
            List<Country> sortedList = new ArrayList<>(nationalities);
            Collections.sort(sortedList);
            for (Country country : sortedList) {
                sb.append(country).append("\n");
            }
        }

        if (resultCapture != null) resultCapture.append(sb.toString());
        else System.out.print(sb.toString());
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}