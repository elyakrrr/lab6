package shared.network;

import shared.model.Person;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String[] args;
    private final Person person;

    public Request(String commandName, String[] args, Person person) {
        this.commandName = commandName;
        this.args = args;
        this.person = person;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", person=" + person +
                '}';
    }
}