package server.commands;

import server.collection.CollectionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PrintFieldDescendingBirthdayCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private CommandInvoker.CommandResult resultCapture;

    public PrintFieldDescendingBirthdayCommand(CollectionManager collectionManager) {
        super(collectionManager, "print_field_descending_birthday",
                "вывести значения поля birthday всех элементов в порядке убывания");
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "print_field_descending_birthday")) return;

        List<Date> birthdays = collectionManager.getBirthdaysDescending();
        StringBuilder sb = new StringBuilder();

        if (birthdays.isEmpty()) {
            sb.append("Нет элементов с датой рождения");
        } else {
            sb.append("*Дни рождения в порядке убывания*\n");
            for (Date birthday : birthdays) {
                sb.append(DATE_FORMAT.format(birthday)).append("\n");
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