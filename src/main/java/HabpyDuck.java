import java.util.Scanner;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    private static final String CHATBOT_NAME = "HabpyDuck";
    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        String command = scanner.nextLine();
        while (!command.equals("bye")) {
            System.out.println(SEPARATOR);
            try {
                taskCount = handleCommand(command, tasks, taskCount);
            } catch (HabpyDuckException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(SEPARATOR);
            command = scanner.nextLine();
        }

        System.out.println(SEPARATOR);
        System.out.println("Bye friend. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    /**
     * Runs one user command and returns the updated number of tasks.
     *
     * @param command the command entered by the user
     * @param tasks the array that stores all tasks
     * @param taskCount the number of tasks currently stored
     * @return the updated number of tasks
     * @throws HabpyDuckException if the command is invalid
     */
    private static int handleCommand(String command, Task[] tasks, int taskCount) throws HabpyDuckException {
        if (command.equals("list")) {
            printTaskList(tasks, taskCount);
            return taskCount;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            int taskIndex = parseTaskIndex(command, "mark", taskCount);
            tasks[taskIndex].markAsDone();
            System.out.println("YAY GOOD JOB!!! I've marked this task as done:");
            System.out.println("  " + tasks[taskIndex]);
            return taskCount;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            int taskIndex = parseTaskIndex(command, "unmark", taskCount);
            tasks[taskIndex].markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet, all the best friend:");
            System.out.println("  " + tasks[taskIndex]);
            return taskCount;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.length() > 4 ? command.substring(5).trim() : "";
            return addTask(new Todo(requireText(description, "The description of a todo cannot be empty.")),
                    tasks, taskCount);
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return addDeadline(command, tasks, taskCount);
        } else if (command.equals("event") || command.startsWith("event ")) {
            return addEvent(command, tasks, taskCount);
        } else if (command.isBlank()) {
            throw new HabpyDuckException("Please enter a command.");
        } else {
            throw new HabpyDuckException("OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, or unmark!");
        }
    }

    /**
     * Prints all tasks in the task list.
     *
     * @param tasks the array that stores all tasks
     * @param taskCount the number of tasks currently stored
     */
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Creates and stores a deadline task from a deadline command.
     *
     * @param command the full deadline command
     * @param tasks the array that stores all tasks
     * @param taskCount the number of tasks currently stored
     * @return the updated number of tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static int addDeadline(String command, Task[] tasks, int taskCount) throws HabpyDuckException {
        String taskDetails = command.length() > 8 ? command.substring(9) : "";
        int byIndex = taskDetails.indexOf(" /by ");
        if (byIndex == -1) {
            throw new HabpyDuckException("Please use this format: deadline DESCRIPTION /by WHEN :)");
        }

        String description = requireText(taskDetails.substring(0, byIndex).trim(),
                "The description of a deadline cannot be empty. Try again my friend!");
        String by = requireText(taskDetails.substring(byIndex + 5).trim(),
                "The deadline time cannot be empty. Try again my friend!");
        return addTask(new Deadline(description, by), tasks, taskCount);
    }

    /**
     * Creates and stores an event task from an event command.
     *
     * @param command the full event command
     * @param tasks the array that stores all tasks
     * @param taskCount the number of tasks currently stored
     * @return the updated number of tasks
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static int addEvent(String command, Task[] tasks, int taskCount) throws HabpyDuckException {
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
        return addTask(new Event(description, from, to), tasks, taskCount);
    }

    /**
     * Stores a task and prints the standard message for added tasks.
     *
     * @param task the task to add
     * @param tasks the array that stores all tasks
     * @param taskCount the number of tasks currently stored
     * @return the updated number of tasks
     * @throws HabpyDuckException if the task list is already full
     */
    private static int addTask(Task task, Task[] tasks, int taskCount) throws HabpyDuckException {
        if (taskCount >= MAX_TASKS) {
            throw new HabpyDuckException("Your task list is full. Please finish some tasks before adding more!");
        }

        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks[taskCount - 1]);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
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
