package habpyduck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import habpyduck.storage.Storage;

public class HabpyDuckTest {
    @TempDir
    private Path tempDir;

    @Test
    public void getResponse_addAndListTasks_returnsCommandOutput() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        String addResponse = habpyDuck.getResponse("todo read book");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), addResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list:",
                "1.[T][ ] read book"), listResponse);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessageWithoutChangingTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        String invalidResponse = habpyDuck.getResponse("todo");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals("OH NO!!! A todo needs a description, friend. Try something like: todo read book",
                invalidResponse);
        assertEquals("Here are the tasks in your list:", listResponse);
    }

    @Test
    public void getResponse_byeCommand_returnsFarewellMessage() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        assertEquals("Bye friend. Hope to see you again soon!", habpyDuck.getResponse("bye"));
    }

    @Test
    public void getResponse_addTask_savesTaskToStorage() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(saveFile.toString()));

        habpyDuck.getResponse("todo read book");

        assertEquals("T | 0 | read book", Files.readString(saveFile).stripTrailing());
    }

    @Test
    public void getCommandType_successfulCommands_returnsLastCommandClassName() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        assertEquals("AddCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("mark 1");
        assertEquals("MarkCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("delete 1");
        assertEquals("DeleteCommand", habpyDuck.getCommandType());
    }

    @Test
    public void getCommandType_invalidCommand_returnsEmptyString() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo");

        assertEquals("", habpyDuck.getCommandType());
    }
}
