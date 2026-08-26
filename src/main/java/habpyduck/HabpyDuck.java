package habpyduck;

import habpyduck.command.Command;
import habpyduck.parser.Parser;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    /**
     * Starts the chatbot, reads user commands, executes them, and exits when the user is done.
     *
     * @param args command line arguments supplied by the Java launcher.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data", "habpyduck.txt");
        Parser parser = new Parser();
        TaskList tasks = new TaskList(storage.loadTasks());
        ui.showWelcome();

        boolean isExit = false;
        while (ui.hasNextCommand()) {
            boolean hasShownLeadingSeparator = false;
            try {
                String commandText = ui.readCommand();
                Command command = parser.parse(commandText);
                isExit = command.isExit();
                if (isExit) {
                    break;
                }
                ui.showSeparator();
                hasShownLeadingSeparator = true;
                command.execute(tasks, ui, storage);
            } catch (HabpyDuckException e) {
                if (!hasShownLeadingSeparator) {
                    ui.showSeparator();
                }
                ui.showError(e.getMessage());
            } finally {
                if (!isExit) {
                    ui.showSeparator();
                }
            }
        }
        ui.showBye();
    }
}
