import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    private static final Path SAVE_FILE_PATH = Path.of("data", "habpyduck.txt");
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        ArrayList<Task> tasks = loadTasks();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (getCommandType(command) == CommandType.BYE) {
                break;
            }

            ui.showSeparator();
            try {
                handleCommand(command, tasks, ui);
            } catch (HabpyDuckException e) {
                ui.showError(e.getMessage());
            }
            ui.showSeparator();
        }

        ui.showBye();
    }

    /**
     * Runs one user command.
     *
     * @param command the command entered by the user
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @throws HabpyDuckException if the command is invalid
     */
    private static void handleCommand(String command, ArrayList<Task> tasks, Ui ui) throws HabpyDuckException {
        switch (getCommandType(command)) {
        case LIST:
            tasks.clear();
            tasks.addAll(loadTasks(true, ui));
            ui.showTaskList(tasks);
            break;
        case MARK:
            int taskIndex = parseTaskIndex(command, "mark", tasks.size());
            tasks.get(taskIndex).markAsDone();
            try {
                saveTasks(tasks);
            } catch (HabpyDuckException e) {
                tasks.get(taskIndex).markAsNotDone();
                throw e;
            }
            ui.showTaskMarked(tasks.get(taskIndex));
            break;
        case UNMARK:
            int unmarkTaskIndex = parseTaskIndex(command, "unmark", tasks.size());
            tasks.get(unmarkTaskIndex).markAsNotDone();
            try {
                saveTasks(tasks);
            } catch (HabpyDuckException e) {
                tasks.get(unmarkTaskIndex).markAsDone();
                throw e;
            }
            ui.showTaskUnmarked(tasks.get(unmarkTaskIndex));
            break;
        case DELETE:
            int deleteTaskIndex = parseTaskIndex(command, "delete", tasks.size());
            Task removedTask = tasks.remove(deleteTaskIndex);
            try {
                saveTasks(tasks);
            } catch (HabpyDuckException e) {
                tasks.add(deleteTaskIndex, removedTask);
                throw e;
            }
            ui.showTaskDeleted(removedTask, tasks.size());
            break;
        case TODO:
            String description = command.length() > 4 ? command.substring(5).trim() : "";
            addTask(new Todo(requireText(description,
                    "OH NO!!! A todo needs a description, friend. Try something like: todo read book")), tasks, ui);
            break;
        case DEADLINE:
            addDeadline(command, tasks, ui);
            break;
        case EVENT:
            addEvent(command, tasks, ui);
            break;
        case UNKNOWN:
            if (command.isBlank()) {
                throw new HabpyDuckException("OH NO!!! I didn't catch a command, friend. Please type something for me.");
            }
            throw new HabpyDuckException("OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, or delete!");
        case BYE:
            break;
        }
    }

    /**
     * Finds the command type for the user's input.
     *
     * @param command the full command entered by the user
     * @return the matching command type
     */
    private static CommandType getCommandType(String command) {
        String trimmedCommand = command.trim();
        String commandWord = trimmedCommand.split(" ", 2)[0];
        return CommandType.fromCommandWord(commandWord);
    }

    /**
     * Creates and stores a deadline task from a deadline command.
     *
     * @param command the full deadline command
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addDeadline(String command, ArrayList<Task> tasks, Ui ui) throws HabpyDuckException {
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
        addTask(new Deadline(description, parseUserDeadlineDateTime(by)), tasks, ui);
    }

    /**
     * Creates and stores an event task from an event command.
     *
     * @param command the full event command
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addEvent(String command, ArrayList<Task> tasks, Ui ui) throws HabpyDuckException {
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
        addTask(new Event(description, from, to), tasks, ui);
    }

    /**
     * Stores a task and prints the standard message for added tasks.
     *
     * @param task the task to add
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     */
    private static void addTask(Task task, ArrayList<Task> tasks, Ui ui) throws HabpyDuckException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (HabpyDuckException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Saves the current tasks to disk, replacing the old file contents.
     *
     * @param tasks the list of tasks to save
     * @throws HabpyDuckException if the file cannot be written
     */
    private static void saveTasks(ArrayList<Task> tasks) throws HabpyDuckException {
        try {
            Files.createDirectories(SAVE_FILE_PATH.getParent());
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileString());
            }
            Files.write(SAVE_FILE_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HabpyDuckException("OH NO!!! I could not save your tasks to " + SAVE_FILE_PATH + ".");
        }
    }

    /**
     * Loads saved tasks from disk when the chatbot starts.
     *
     * @param shouldShowWarnings whether to print warnings for malformed saved tasks
     * @param ui the UI used to show loading warnings
     * @return the tasks stored in the save file, or an empty list if there is no save file yet
     */
    private static ArrayList<Task> loadTasks(boolean shouldShowWarnings, Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE_PATH)) {
            return tasks;
        }

        try {
            ArrayList<String> lines = new ArrayList<>(Files.readAllLines(SAVE_FILE_PATH, StandardCharsets.UTF_8));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(parseTaskFromFile(line));
                } catch (HabpyDuckException e) {
                    if (shouldShowWarnings && ui != null) {
                        ui.showError("OH NO!!! I had trouble loading saved task on line "
                                + (i + 1) + ": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            if (shouldShowWarnings && ui != null) {
                ui.showError("OH NO!!! I could not load tasks from " + SAVE_FILE_PATH + ".");
            }
        }
        return tasks;
    }

    /**
     * Loads saved tasks without printing warnings.
     *
     * @return the tasks stored in the save file, or an empty list if there is no save file yet
     */
    private static ArrayList<Task> loadTasks() {
        return loadTasks(false, null);
    }

    /**
     * Converts one saved text line back into a task object.
     *
     * @param line one line from the save file
     * @return the task represented by that line
     * @throws HabpyDuckException if the saved line is not in the expected format
     */
    private static Task parseTaskFromFile(String line) throws HabpyDuckException {
        String[] parts = line.split(" \\| ", -1);
        validateSavedTaskParts(parts);

        Task task;
        switch (parts[0]) {
        case "D":
            task = new Deadline(unescapeFileField(parts[2]), parseSavedDeadlineDateTime(unescapeFileField(parts[3])));
            break;
        case "E":
            task = new Event(unescapeFileField(parts[2]), unescapeFileField(parts[3]), unescapeFileField(parts[4]));
            break;
        case "T":
            task = new Todo(unescapeFileField(parts[2]));
            break;
        default:
            throw new HabpyDuckException("unknown task type '" + parts[0] + "'");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts deadline text entered by the user into a LocalDateTime.
     *
     * @param dateTimeText the date and time entered by the user
     * @return the parsed date and time
     * @throws HabpyDuckException if the date and time is not in d/M/yyyy HHmm format
     */
    private static LocalDateTime parseUserDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new HabpyDuckException(
                    "OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800");
        }
    }

    /**
     * Converts deadline text from the save file into a LocalDateTime.
     *
     * @param dateTimeText the saved date and time text
     * @return the parsed date and time
     * @throws HabpyDuckException if the saved value is not an ISO date or date-time
     */
    private static LocalDateTime parseSavedDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText);
        } catch (DateTimeParseException dateTimeError) {
            try {
                return LocalDate.parse(dateTimeText).atStartOfDay();
            } catch (DateTimeParseException dateError) {
                throw new HabpyDuckException("saved deadline date and time must use yyyy-MM-ddTHH:mm format");
            }
        }
    }

    /**
     * Checks that a saved task line has a known type, valid done status, and correct number of fields.
     *
     * @param parts the saved line split into fields
     * @throws HabpyDuckException if the saved line is malformed
     */
    private static void validateSavedTaskParts(String[] parts) throws HabpyDuckException {
        if (parts.length < 2) {
            throw new HabpyDuckException("missing task type or status");
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new HabpyDuckException("status must be 0 or 1");
        }

        int expectedPartCount;
        switch (parts[0]) {
        case "T":
            expectedPartCount = 3;
            break;
        case "D":
            expectedPartCount = 4;
            break;
        case "E":
            expectedPartCount = 5;
            break;
        default:
            throw new HabpyDuckException("unknown task type '" + parts[0] + "'");
        }
        if (parts.length != expectedPartCount) {
            throw new HabpyDuckException("expected " + expectedPartCount + " fields but found " + parts.length);
        }
        for (int i = 2; i < parts.length; i++) {
            if (unescapeFileField(parts[i]).isBlank()) {
                throw new HabpyDuckException("task details cannot be empty");
            }
        }
    }

    /**
     * Escapes special characters so user text can be stored safely on one line.
     *
     * @param field the task text to save
     * @return the escaped text
     */
    public static String escapeFileField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("|", "\\|");
    }

    /**
     * Restores special characters that were escaped for the save file.
     *
     * @param field the saved text to restore
     * @return the unescaped text
     */
    private static String unescapeFileField(String field) {
        StringBuilder result = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (!isEscaping && character == '\\') {
                isEscaping = true;
                continue;
            }
            if (isEscaping) {
                switch (character) {
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                default:
                    result.append(character);
                    break;
                }
                isEscaping = false;
                continue;
            }
            result.append(character);
        }
        if (isEscaping) {
            result.append('\\');
        }
        return result.toString();
    }

    /**
     * Converts a user-facing task number into an array index.
     *
     * @param command the full mark or unmark command
     * @param commandWord the command word, either mark or unmark
     * @param taskCount the number of tasks currently stored
     * @return the zero-based array index of the requested task
     * @throws HabpyDuckException if the task number is missing or invalid
     */
    private static int parseTaskIndex(String command, String commandWord, int taskCount) throws HabpyDuckException {
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

    /**
     * Checks that a required piece of user input is not blank.
     *
     * @param text the text to check
     * @param errorMessage the message to show if the text is blank
     * @return the text, if it is not blank
     * @throws HabpyDuckException if the text is blank
     */
    private static String requireText(String text, String errorMessage) throws HabpyDuckException {
        if (text.isBlank()) {
            throw new HabpyDuckException(errorMessage);
        }
        return text;
    }
}
