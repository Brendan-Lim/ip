package habpyduck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests task list operations that change stored task order and status.
 */
public class TaskListTest {
    @Test
    public void addDeleteInsertAndRemoveLast_mixedOperations_updatesListOrder() {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        Todo insertedTask = new Todo("inserted");

        tasks.add(firstTask);
        tasks.add(secondTask);
        Task deletedTask = tasks.delete(0);
        tasks.insert(0, insertedTask);
        Task removedTask = tasks.removeLast();

        assertEquals(firstTask, deletedTask);
        assertEquals(secondTask, removedTask);
        assertEquals(1, tasks.size());
        assertEquals(insertedTask, tasks.get(0));
    }

    @Test
    public void markAsDoneAndNotDone_existingTask_updatesStatus() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.markAsDone(0);
        assertEquals("[T][X] read book", tasks.get(0).toString());

        tasks.markAsNotDone(0);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void constructorAndReplaceAll_givenSourceLists_copiesTasksIntoList() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("initial"));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.add(new Todo("should not appear"));

        ArrayList<Task> replacementTasks = new ArrayList<>();
        replacementTasks.add(new Todo("replacement"));
        tasks.replaceAll(replacementTasks);
        replacementTasks.add(new Todo("should also not appear"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] replacement", tasks.get(0).toString());
    }

    @Test
    public void asList_returnedListIsChanged_originalTaskListIsUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> copiedTasks = tasks.asList();
        copiedTasks.add(new Todo("extra"));
        copiedTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }
}
