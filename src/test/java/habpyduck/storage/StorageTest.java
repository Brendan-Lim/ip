package habpyduck.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import habpyduck.HabpyDuckException;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Task;
import habpyduck.task.Todo;

/**
 * Tests saving and loading tasks through the text file format.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void escapeFileField_specialCharacters_returnsEscapedText() {
        String original = "read | book\\notes\nline\rend";

        assertEquals("read \\| book\\\\notes\\nline\\rend", Storage.escapeFileField(original));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveTasks_mixedTasks_writesExpectedFileFormat() throws Exception {
        Path saveFile = tempDir.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read | book");
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 8, 25, 18, 0));
        Event event = new Event("sync \\ call", "room | A", "4\\5pm");
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);

        String expected = String.join(System.lineSeparator(),
                "T | 0 | read \\| book",
                "D | 1 | return book | 2026-08-25T18:00",
                "E | 0 | sync \\\\ call | room \\| A | 4\\\\5pm");
        assertEquals(expected, Files.readString(saveFile).stripTrailing());
    }

    @Test
    public void loadTasks_validSavedTasks_returnsTaskObjectsWithStatusAndDetails() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-25T18:00",
                "E | 1 | project meeting | Mon 2pm | 4pm"));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Aug 25 2026, 6:00pm)", tasks.get(1).toString());
        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", tasks.get(2).toString());
    }

    @Test
    public void loadTasks_savedDateWithoutTime_returnsDeadlineAtStartOfDay() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, "D | 0 | return book | 2026-08-25");
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: Aug 25 2026, 12:00am)", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_escapedSavedFields_returnsUnescapedTaskText() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | read \\| book",
                "E | 0 | sync \\\\ call | room \\| A | 4\\\\5pm"));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read | book", tasks.get(0).toString());
        assertEquals("[E][ ] sync \\ call (from: room | A to: 4\\5pm)", tasks.get(1).toString());
    }

    @Test
    public void loadTasks_malformedLines_skipsInvalidTasksAndKeepsValidTasks() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | 2 | bad status | 2026-08-25T18:00",
                "X | 0 | bad type",
                "D | 0 | missing date",
                "D | 0 | invalid date | tomorrow",
                "E | 1 | project \\| meeting | C:\\\\start | 4\\|5pm",
                "T | 0 |    "));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[E][X] project | meeting (from: C:\\start to: 4|5pm)", tasks.get(1).toString());
    }

    @Test
    public void saveTasks_fileCannotBeWritten_exceptionThrown() throws Exception {
        Path directoryPath = tempDir.resolve("directory");
        Files.createDirectories(directoryPath);
        Storage storage = new Storage(directoryPath.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        HabpyDuckException exception = org.junit.jupiter.api.Assertions.assertThrows(HabpyDuckException.class,
                () -> storage.saveTasks(tasks));

        assertEquals("OH NO!!! I could not save your tasks to " + directoryPath + ".",
                exception.getMessage());
    }
}
