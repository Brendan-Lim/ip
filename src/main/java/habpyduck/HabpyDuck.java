package habpyduck;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import habpyduck.command.Command;
import habpyduck.parser.Parser;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Entry point for the HabpyDuck chatbot.
 */
public class HabpyDuck {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a chatbot that uses the default save file.
     */
    public HabpyDuck() {
        this(new Storage("data", "habpyduck.txt"));
    }

    /**
     * Creates a chatbot that uses the given storage.
     *
     * @param storage the storage used to load and save tasks.
     */
    public HabpyDuck(Storage storage) {
        this(storage, new Parser(), new Ui());
    }

    private HabpyDuck(Storage storage, Parser parser, Ui ui) {
        this.storage = storage;
        this.parser = parser;
        this.ui = ui;
        tasks = new TaskList(storage.loadTasks());
    }

    /**
     * Starts the chatbot, reads user commands, executes them, and exits when the user is done.
     *
     * @param args Command line arguments supplied by the Java launcher.
     */
    public static void main(String[] args) {
        new HabpyDuck().run();
    }

    /**
     * Returns the chatbot's response to one user command.
     *
     * @param commandText the command entered by the user.
     * @return the response text.
     */
    public String getResponse(String commandText) {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        PrintStream responseOutput = new PrintStream(responseStream, true, StandardCharsets.UTF_8);
        Ui responseUi = new Ui(responseOutput);

        try {
            Command command = parser.parse(commandText);
            if (command.isExit()) {
                return responseUi.getByeMessage();
            }
            command.execute(tasks, responseUi, storage);
        } catch (HabpyDuckException e) {
            responseUi.showError(e.getMessage());
        }

        return responseStream.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Starts the console version of the chatbot.
     */
    public void run() {
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
