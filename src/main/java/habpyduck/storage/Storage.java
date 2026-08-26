package habpyduck.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import habpyduck.HabpyDuckException;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Task;
import habpyduck.task.Todo;
import habpyduck.ui.Ui;

/**
 * Handles loading tasks from disk and saving tasks to disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage object that reads from and writes to the given file.
     *
     * @param firstPathPart the first part of the relative path to the save file
     * @param otherPathParts the remaining parts of the relative path to the save file
     */
    public Storage(String firstPathPart, String... otherPathParts) {
        this.filePath = Path.of(firstPathPart, otherPathParts);
    }

    /**
     * Saves the current tasks to disk, replacing the old file contents.
     *
     * @param tasks the list of tasks to save
     * @throws HabpyDuckException if the file cannot be written
     */
    public void saveTasks(ArrayList<Task> tasks) throws HabpyDuckException {
        try {
            Files.createDirectories(filePath.getParent());
            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileString());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HabpyDuckException("OH NO!!! I could not save your tasks to " + filePath + ".");
        }
    }

    /**
     * Loads saved tasks from disk.
     *
     * @return the tasks stored in the save file, or an empty list if there is no save file yet
     */
    public ArrayList<Task> loadTasks() {
        return loadTasks(false, null);
    }

    /**
     * Loads saved tasks from disk.
     *
     * @param shouldShowWarnings whether to print warnings for malformed saved tasks
     * @param ui the UI used to show loading warnings
     * @return the tasks stored in the save file, or an empty list if there is no save file yet
     */
    public ArrayList<Task> loadTasks(boolean shouldShowWarnings, Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            ArrayList<String> lines = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
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
                ui.showError("OH NO!!! I could not load tasks from " + filePath + ".");
            }
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
    private Task parseTaskFromFile(String line) throws HabpyDuckException {
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
     * Converts deadline text from the save file into a LocalDateTime.
     *
     * @param dateTimeText the saved date and time text
     * @return the parsed date and time
     * @throws HabpyDuckException if the saved value is not an ISO date or date-time
     */
    private LocalDateTime parseSavedDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
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
    private void validateSavedTaskParts(String[] parts) throws HabpyDuckException {
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
    private String unescapeFileField(String field) {
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
}
