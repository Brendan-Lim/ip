/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data", "habpyduck.txt");
        Parser parser = new Parser();
        ui.showWelcome();

        TaskList tasks = new TaskList(storage.loadTasks());
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (parser.getCommandType(command) == CommandType.BYE) {
                break;
            }

            ui.showSeparator();
            try {
                handleCommand(command, tasks, ui, storage, parser);
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
     * @param parser the parser used to interpret command details
     * @throws HabpyDuckException if the command is invalid
     */
    private static void handleCommand(String command, TaskList tasks, Ui ui, Storage storage, Parser parser)
            throws HabpyDuckException {
        switch (parser.getCommandType(command)) {
        case LIST:
            tasks.replaceAll(storage.loadTasks(true, ui));
            ui.showTaskList(tasks.asList());
            break;
        case MARK:
            int taskIndex = parser.parseTaskIndex(command, "mark", tasks.size());
            tasks.markAsDone(taskIndex);
            try {
                storage.saveTasks(tasks.asList());
            } catch (HabpyDuckException e) {
                tasks.markAsNotDone(taskIndex);
                throw e;
            }
            ui.showTaskMarked(tasks.get(taskIndex));
            break;
        case UNMARK:
            int unmarkTaskIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
            tasks.markAsNotDone(unmarkTaskIndex);
            try {
                storage.saveTasks(tasks.asList());
            } catch (HabpyDuckException e) {
                tasks.markAsDone(unmarkTaskIndex);
                throw e;
            }
            ui.showTaskUnmarked(tasks.get(unmarkTaskIndex));
            break;
        case DELETE:
            int deleteTaskIndex = parser.parseTaskIndex(command, "delete", tasks.size());
            Task removedTask = tasks.delete(deleteTaskIndex);
            try {
                storage.saveTasks(tasks.asList());
            } catch (HabpyDuckException e) {
                tasks.insert(deleteTaskIndex, removedTask);
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
            addDeadline(command, tasks, ui, storage, parser);
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
     * Creates and stores a deadline task from a deadline command.
     *
     * @param command the full deadline command
     * @param tasks the list that stores all tasks
     * @param ui the UI used to show command results
     * @param storage the storage used to save tasks
     * @param parser the parser used to interpret command details
     * @throws HabpyDuckException if the command is missing required parts
     */
    private static void addDeadline(String command, TaskList tasks, Ui ui, Storage storage, Parser parser)
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
        addTask(new Deadline(description, parser.parseUserDeadlineDateTime(by)), tasks, ui, storage);
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
    private static void addEvent(String command, TaskList tasks, Ui ui, Storage storage)
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
    private static void addTask(Task task, TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            tasks.removeLast();
            throw e;
        }
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
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
