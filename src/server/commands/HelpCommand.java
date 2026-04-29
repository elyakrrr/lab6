package server.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand extends BaseCommand implements CommandInvoker.ResultCapturingCommand {
    private final CommandInvoker invoker;
    private CommandInvoker.CommandResult resultCapture;

    public HelpCommand(CommandInvoker invoker) {
        super(null, "help", "вывести справку по доступным командам");
        this.invoker = invoker;
    }

    @Override
    public void execute(String[] args) {
        if (!validateArgs(args, 0, "help")) return;

        StringBuilder sb = new StringBuilder();
        sb.append("*Доступные команды*\n");

        Map<CommandType, Command> commandMap = invoker.getCommands();
        List<Command> commandList = new ArrayList<>(commandMap.values());

        for (Command command : commandList) {
            sb.append(command).append("\n");
        }

        String result = sb.toString();

        if (resultCapture != null) {
            resultCapture.append(result);
        } else {
            System.out.print(result);
        }
    }

    @Override
    public void setResultCapture(CommandInvoker.CommandResult result) {
        this.resultCapture = result;
    }
}