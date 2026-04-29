package server.commands;

public enum CommandType {
    HELP,
    INFO,
    SHOW,
    ADD,
    UPDATE,
    REMOVE_BY_ID,
    CLEAR,
    ADD_IF_MIN,
    REMOVE_GREATER,
    REMOVE_LOWER,
    COUNT_LESS_THAN_NATIONALITY,
    PRINT_UNIQUE_NATIONALITY,
    PRINT_FIELD_DESCENDING_BIRTHDAY;

    public static CommandType fromString(String commandName) {
        if (commandName == null) return null;

        return switch (commandName.toLowerCase()) {
            case "help" -> HELP;
            case "info" -> INFO;
            case "show" -> SHOW;
            case "add" -> ADD;
            case "update" -> UPDATE;
            case "remove_by_id" -> REMOVE_BY_ID;
            case "clear" -> CLEAR;
            case "add_if_min" -> ADD_IF_MIN;
            case "remove_greater" -> REMOVE_GREATER;
            case "remove_lower" -> REMOVE_LOWER;
            case "count_less_than_nationality" -> COUNT_LESS_THAN_NATIONALITY;
            case "print_unique_nationality" -> PRINT_UNIQUE_NATIONALITY;
            case "print_field_descending_birthday" -> PRINT_FIELD_DESCENDING_BIRTHDAY;
            default -> null;
        };
    }
}