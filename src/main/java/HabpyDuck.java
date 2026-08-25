import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data", "habpyduck.txt");
        ui.showWelcome();

        ArrayList<Task> tasks = storage.loadTasks();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (getCommandType(command) == CommandType.BYE) {
                break;
            }

            ui.showSeparator();
            try {
                handleCommand(command, tasks, ui, storage);
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
     * @param storage the storage used to save and load tasks
     * @throws HabpyDuckException if the command is invalid
     */
    private static void handleCommand(String command, ArrayList<Task> tasks, Ui ui, Storage storage)
            throws HabpyDuckException {
        switch (getCommandType(command)) {
        case LIST:
            tasks.clear();
            tasks.addAll(storage.loadTasks(true, ui));
            ui.showTaskList(tasks);
            break;
        case MARK:
            int taskIndex = parseTaskIndex(command, "mark", tasks.size());
            tasks.get(taskIndex).markAsDone();
            try {
                storage.saveTasks(tasks);
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
                storage.saveTasks(tasks);
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
                storage.saveTasks(tasks);
            } catch (HabpyDuckException e) {
                tasks.add(deleteTaskIndex, removedTask);
                throw e;
            }
            ui.showTaskDeleted(removedTask, tasks.size());
            break;
        case TODO:
            String description = command.length() > 4 ? command.substring(5).trim() : "";
            addTask(new Todo(requireText(description,
                    "OH NO!!! A todo needs a description, friend. Try something like: todo read book")),
                    tasks, ui, storage);
            break;
        case DEADLINE:
            addDeadline(command, tasks, ui, storage);
            break;
        case EVENT:
            addEvent(command, tasks, ui, storage);
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
     * @param storage the storage used to save tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addDeadline(String command, ArrayList<Task> tasks, Ui ui, Storage storage)
            throws HabpyDuckException {
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
        addTask(new Deadline(description, parseUserDeadlineDateTime(by)), tasks, ui, storage);
    }

    /**
     * Creates and stores an event task from an event command.
     *
     * @param command the full event command
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @param storage the storage used to save tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addEvent(String command, ArrayList<Task> tasks, Ui ui, Storage storage)
            throws HabpyDuckException {
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
        addTask(new Event(description, from, to), tasks, ui, storage);
    }

    /**
     * Stores a task and prints the standard message for added tasks.
     *
     * @param task the task to add
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @param storage the storage used to save tasks
     */
    private static void addTask(Task task, ArrayList<Task> tasks, Ui ui, Storage storage) throws HabpyDuckException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks);
        } catch (HabpyDuckException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
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
