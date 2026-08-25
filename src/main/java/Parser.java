import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of raw command text entered by the user.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Finds the command type for the user's input.
     *
     * @param command the full command entered by the user
     * @return the matching command type
     */
    public CommandType getCommandType(String command) {
        String trimmedCommand = command.trim();
        String commandWord = trimmedCommand.split(" ", 2)[0];
        return CommandType.fromCommandWord(commandWord);
    }

    /**
     * Converts deadline text entered by the user into a LocalDateTime.
     *
     * @param dateTimeText the date and time entered by the user
     * @return the parsed date and time
     * @throws HabpyDuckException if the date and time is not in d/M/yyyy HHmm format
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
     * @param command the full mark or unmark command
     * @param commandWord the command word, such as mark, unmark, or delete
     * @param taskCount the number of tasks currently stored
     * @return the zero-based array index of the requested task
     * @throws HabpyDuckException if the task number is missing or invalid
     */
    public int parseTaskIndex(String command, String commandWord, int taskCount) throws HabpyDuckException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HabpyDuckException("OH NO!!! Please tell me which task to " + commandWord + ", like: "
                    + commandWord + " 2");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            int taskIndex = taskNumber - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new HabpyDuckException("OH NO!!! Task " + taskNumber + " does not exist in your list.");
            }
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new HabpyDuckException("OH NO!!! Please use a number after " + commandWord + ", like: "
                    + commandWord + " 2");
        }
    }
}
