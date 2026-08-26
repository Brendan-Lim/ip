package habpyduck.ui;

import java.util.ArrayList;
import java.util.Scanner;

import habpyduck.task.Task;

/**
 * Handles all direct interactions with the user through the console.
 */
public class Ui {
    private static final String CHATBOT_NAME = "HabpyDuck";
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Prints the greeting shown when the chatbot starts.
     */
    public void showWelcome() {
        String banner = " _   _       _                 ____             _    \n"
                + "| | | | __ _| |__  _ __  _   _|  _ \\ _   _  ___| | __\n"
                + "| |_| |/ _` | '_ \\| '_ \\| | | | | | | | | |/ __| |/ /\n"
                + "|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < \n"
                + "|_| |_|\\__,_|_.__/| .__/ \\__, |____/ \\__,_|\\___|_|\\_\\\n"
                + "                  |_|    |___/                       \n";
        showSeparator();
        System.out.print(banner);
        System.out.println("Hi friend! I'm " + CHATBOT_NAME + ".");
        System.out.println("What can I do for you today?");
        showSeparator();
    }

    /**
     * Prints the farewell message shown when the chatbot exits.
     */
    public void showBye() {
        showSeparator();
        System.out.println("Bye friend. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Returns whether there is another command available to read.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return the command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the standard separator line.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Prints an error message.
     *
     * @param message the message to show
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints all tasks in the task list.
     *
     * @param tasks the tasks to show
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the standard message after adding a task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks now in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints the standard message after marking a task as done.
     *
     * @param task the task that was marked
     */
    public void showTaskMarked(Task task) {
        System.out.println("YAY GOOD JOB!!! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Prints the standard message after marking a task as not done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet, all the best friend:");
        System.out.println("  " + task);
    }

    /**
     * Prints the standard message after deleting a task.
     *
     * @param task the task that was deleted
     * @param taskCount the number of tasks now in the list
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
