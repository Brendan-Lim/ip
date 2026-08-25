import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    private static final String CHATBOT_NAME = "HabpyDuck";
    private static final String SEPARATOR = "____________________________________________________________";
    private static final Path SAVE_FILE_PATH = Path.of("data", "habpyduck.txt");

    public static void main(String[] args) {
        String banner = " _   _       _                 ____             _    \n"
                + "| | | | __ _| |__  _ __  _   _|  _ \\ _   _  ___| | __\n"
                + "| |_| |/ _` | '_ \\| '_ \\| | | | | | | | | |/ __| |/ /\n"
                + "|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < \n"
                + "|_| |_|\\__,_|_.__/| .__/ \\__, |____/ \\__,_|\\___|_|\\_\\\n"
                + "                  |_|    |___/                       \n";
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hi friend! I'm " + CHATBOT_NAME + ".");
        System.out.println("What can I do for you today?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = loadTasks();
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (getCommandType(command) == CommandType.BYE) {
                break;
            }

            System.out.println(SEPARATOR);
            try {
                handleCommand(command, tasks);
            } catch (HabpyDuckException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(SEPARATOR);
        }

        System.out.println(SEPARATOR);
        System.out.println("Bye friend. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    /**
     * Runs one user command.
     *
     * @param command the command entered by the user
     * @param tasks the list that stores all tasks
     * @throws HabpyDuckException if the command is invalid
     */
    private static void handleCommand(String command, ArrayList<Task> tasks) throws HabpyDuckException {
        switch (getCommandType(command)) {
        case LIST:
            printTaskList(tasks);
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
            System.out.println("YAY GOOD JOB!!! I've marked this task as done:");
            System.out.println("  " + tasks.get(taskIndex));
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
            System.out.println("OK, I've marked this task as not done yet, all the best friend:");
            System.out.println("  " + tasks.get(unmarkTaskIndex));
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
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
            break;
        case TODO:
            String description = command.length() > 4 ? command.substring(5).trim() : "";
            addTask(new Todo(requireText(description, "The description of a todo cannot be empty.")), tasks);
            break;
        case DEADLINE:
            addDeadline(command, tasks);
            break;
        case EVENT:
            addEvent(command, tasks);
            break;
        case UNKNOWN:
            if (command.isBlank()) {
                throw new HabpyDuckException("Please enter a command.");
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
     * Prints all tasks in the task list.
     *
     * @param tasks the list that stores all tasks
     */
    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Creates and stores a deadline task from a deadline command.
     *
     * @param command the full deadline command
     * @param tasks the list that stores all tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addDeadline(String command, ArrayList<Task> tasks) throws HabpyDuckException {
        String taskDetails = command.length() > 8 ? command.substring(9) : "";
        int byIndex = taskDetails.indexOf(" /by ");
        if (byIndex == -1) {
            throw new HabpyDuckException("Please use this format: deadline DESCRIPTION /by WHEN :)");
        }

        String description = requireText(taskDetails.substring(0, byIndex).trim(),
                "The description of a deadline cannot be empty. Try again my friend!");
        String by = requireText(taskDetails.substring(byIndex + 5).trim(),
                "The deadline time cannot be empty. Try again my friend!");
        addTask(new Deadline(description, by), tasks);
    }

    /**
     * Creates and stores an event task from an event command.
     *
     * @param command the full event command
     * @param tasks the list that stores all tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addEvent(String command, ArrayList<Task> tasks) throws HabpyDuckException {
        String taskDetails = command.length() > 5 ? command.substring(6) : "";
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = taskDetails.indexOf(" /to ", fromIndex + 7);
        if (fromIndex == -1 || toIndex == -1) {
            throw new HabpyDuckException("Please use this format: event DESCRIPTION /from START /to END :)");
        }

        String description = requireText(taskDetails.substring(0, fromIndex).trim(),
                "The description of an event cannot be empty. Try again my friend!");
        String from = requireText(taskDetails.substring(fromIndex + 7, toIndex).trim(),
                "The start time of an event cannot be empty. Try again my friend!");
        String to = requireText(taskDetails.substring(toIndex + 5).trim(),
                "The end time of an event cannot be empty. Try again my friend!");
        addTask(new Event(description, from, to), tasks);
    }

    /**
     * Stores a task and prints the standard message for added tasks.
     *
     * @param task the task to add
     * @param tasks the list that stores all tasks
     */
    private static void addTask(Task task, ArrayList<Task> tasks) throws HabpyDuckException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (HabpyDuckException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks.get(tasks.size() - 1));
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
            throw new HabpyDuckException("Could not save tasks to " + SAVE_FILE_PATH + ".");
        }
    }

    /**
     * Loads saved tasks from disk when the chatbot starts.
     *
     * @return the tasks stored in the save file, or an empty list if there is no save file yet
     */
    private static ArrayList<Task> loadTasks() {
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
                    System.out.println("Skipping saved task on line " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load tasks from " + SAVE_FILE_PATH + ".");
        }
        return tasks;
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
            task = new Deadline(unescapeFileField(parts[2]), unescapeFileField(parts[3]));
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
            throw new HabpyDuckException("Please tell me which task to " + commandWord + ", like: "
                    + commandWord + " 2");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            int taskIndex = taskNumber - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new HabpyDuckException("Task " + taskNumber + " does not exist in your list.");
            }
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new HabpyDuckException("Please use a number after " + commandWord + ", like: "
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
