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
            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (command.startsWith("todo ")) {
                String description = command.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("deadline ")) {
                String taskDetails = command.substring(9);
                String[] parts = taskDetails.split(" /by ", 2);
                String description = parts[0];
                String by = parts[1];
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("event ")) {
                String taskDetails = command.substring(6);
                String[] descriptionAndTimes = taskDetails.split(" /from ", 2);
                String description = descriptionAndTimes[0];
                String[] times = descriptionAndTimes[1].split(" /to ", 2);
                String from = times[0];
                String to = times[1];
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else {
                tasks[taskCount] = new Todo(command);
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(SEPARATOR);
            command = scanner.nextLine();
        }

        System.out.println(SEPARATOR);
        System.out.println("Bye friend. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
