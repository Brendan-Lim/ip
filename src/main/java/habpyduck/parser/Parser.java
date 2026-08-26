package habpyduck.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import habpyduck.HabpyDuckException;
import habpyduck.command.AddCommand;
import habpyduck.command.Command;
import habpyduck.command.DeleteCommand;
import habpyduck.command.ExitCommand;
import habpyduck.command.FindCommand;
import habpyduck.command.ListCommand;
import habpyduck.command.MarkCommand;
import habpyduck.command.UnmarkCommand;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Todo;

/**
 * Makes sense of raw command text entered by the user.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final String UNKNOWN_COMMAND_MESSAGE = "OH NO!!! I don't understand that command friend :(. "
            + "Try todo, deadline, event, list, mark, unmark, delete, or find!";

    /**
     * Finds the command type for the user's input.
     *
     * @param command the full command entered by the user.
     * @return the matching command type.
     */
    public CommandType getCommandType(String command) {
        String trimmedCommand = command.trim();
        String commandWord = trimmedCommand.split(" ", 2)[0];
        return CommandType.fromCommandWord(commandWord);
    }

    /**
     * Converts raw user input into a command object that can be executed.
     *
     * @param command the full command entered by the user.
     * @return the command represented by the input.
     * @throws HabpyDuckException if the command is unknown or missing required details.
     */
    public Command parse(String command) throws HabpyDuckException {
        switch (getCommandType(command)) {
        case LIST:
            return new ListCommand();
        case MARK:
            return new MarkCommand(parseTaskNumber(command, "mark"));
        case UNMARK:
            return new UnmarkCommand(parseTaskNumber(command, "unmark"));
        case DELETE:
            return new DeleteCommand(parseTaskNumber(command, "delete"));
        case FIND:
            return new FindCommand(parseFindKeyword(command));
        case TODO:
            return new AddCommand(new Todo(parseTodoDescription(command)));
        case DEADLINE:
            return new AddCommand(parseDeadline(command));
        case EVENT:
            return new AddCommand(parseEvent(command));
        case BYE:
            return new ExitCommand();
        case UNKNOWN:
            if (command.isBlank()) {
                throw new HabpyDuckException(
                        "OH NO!!! I didn't catch a command, friend. Please type something for me.");
            }
            throw new HabpyDuckException(UNKNOWN_COMMAND_MESSAGE);
        default:
            throw new HabpyDuckException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Converts deadline text entered by the user into a LocalDateTime.
     *
     * @param dateTimeText the date and time entered by the user.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the date and time is not in d/M/yyyy HHmm format.
     */
    public LocalDateTime parseUserDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new HabpyDuckException(
                    "OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800");
        }
    }

    /**
     * Converts a user-facing task number into an array index.
     *
     * @param command the full mark or unmark command.
     * @param commandWord the command word, such as mark, unmark, or delete.
     * @param taskCount the number of tasks currently stored.
     * @return the zero-based array index of the requested task.
     * @throws HabpyDuckException if the task number is missing or invalid.
     */
    public int parseTaskIndex(String command, String commandWord, int taskCount) throws HabpyDuckException {
        int taskNumber = parseTaskNumber(command, commandWord);
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new HabpyDuckException("OH NO!!! Task " + taskNumber + " does not exist in your list.");
        }
        return taskIndex;
    }

    /**
     * Converts the task number part of a command into an integer.
     *
     * @param command the full mark, unmark, or delete command.
     * @param commandWord the command word, such as mark, unmark, or delete.
     * @return the task number entered by the user.
     * @throws HabpyDuckException if the task number is missing or not a number.
     */
    private int parseTaskNumber(String command, String commandWord) throws HabpyDuckException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HabpyDuckException("OH NO!!! Please tell me which task to " + commandWord + ", like: "
                    + commandWord + " 2");
        }

        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HabpyDuckException("OH NO!!! Please use a number after " + commandWord + ", like: "
                    + commandWord + " 2");
        }
    }

    /**
     * Extracts the description from a todo command.
     *
     * @param command the full todo command.
     * @return the todo description.
     * @throws HabpyDuckException if the description is blank.
     */
    private String parseTodoDescription(String command) throws HabpyDuckException {
        String description = command.length() > 4 ? command.substring(5).trim() : "";
        return requireText(description,
                "OH NO!!! A todo needs a description, friend. Try something like: todo read book");
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command the full find command.
     * @return the keyword to search for.
     * @throws HabpyDuckException if the keyword is blank.
     */
    private String parseFindKeyword(String command) throws HabpyDuckException {
        String keyword = command.length() > 4 ? command.substring(5).trim() : "";
        return requireText(keyword,
                "OH NO!!! Please tell me what keyword to find, like: find book");
    }

    /**
     * Creates a deadline task from a deadline command.
     *
     * @param command the full deadline command.
     * @return the deadline task represented by the command.
     * @throws HabpyDuckException if the command is missing required parts.
     */
    private Deadline parseDeadline(String command) throws HabpyDuckException {
        String taskDetails = command.length() > 8 ? command.substring(9) : "";
        int byIndex = taskDetails.indexOf(" /by ");
        if (byIndex == -1) {
            throw new HabpyDuckException(
                    "OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY HHmm :)");
        }

        String description = requireText(taskDetails.substring(0, byIndex).trim(),
                "OH NO!!! A deadline needs a description, friend. Try again!");
        String by = requireText(taskDetails.substring(byIndex + 5).trim(),
                "OH NO!!! A deadline needs a date and time, friend. Try something like: 25/8/2026 1800");
        return new Deadline(description, parseUserDeadlineDateTime(by));
    }

    /**
     * Creates an event task from an event command.
     *
     * @param command the full event command.
     * @return the event task represented by the command.
     * @throws HabpyDuckException if the command is missing required parts.
     */
    private Event parseEvent(String command) throws HabpyDuckException {
        String taskDetails = command.length() > 5 ? command.substring(6) : "";
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = taskDetails.indexOf(" /to ", fromIndex + 7);
        if (fromIndex == -1 || toIndex == -1) {
            throw new HabpyDuckException(
                    "OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)");
        }

        String description = requireText(taskDetails.substring(0, fromIndex).trim(),
                "OH NO!!! An event needs a description, friend. Try again!");
        String from = requireText(taskDetails.substring(fromIndex + 7, toIndex).trim(),
                "OH NO!!! An event needs a start time, friend. Try again!");
        String to = requireText(taskDetails.substring(toIndex + 5).trim(),
                "OH NO!!! An event needs an end time, friend. Try again!");
        return new Event(description, from, to);
    }

    /**
     * Checks that a required piece of user input is not blank.
     *
     * @param text the text to check.
     * @param errorMessage the message to show if the text is blank.
     * @return the text, if it is not blank.
     * @throws HabpyDuckException if the text is blank.
     */
    private String requireText(String text, String errorMessage) throws HabpyDuckException {
        if (text.isBlank()) {
            throw new HabpyDuckException(errorMessage);
        }
        return text;
    }
}
