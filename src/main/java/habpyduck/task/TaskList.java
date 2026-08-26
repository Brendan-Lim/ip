package habpyduck.task;

import java.util.ArrayList;

/**
 * Stores the user's tasks and provides operations for changing the task list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list using tasks that were already loaded.
     *
     * @param tasks the initial tasks to store.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Replaces the current tasks with another set of tasks.
     *
     * @param newTasks the tasks that should now be stored.
     */
    public void replaceAll(ArrayList<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the last task in the list.
     *
     * @return the removed task.
     */
    public Task removeLast() {
        return tasks.remove(tasks.size() - 1);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex the index of the task to remove.
     * @return the removed task.
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Inserts a task at the given zero-based index.
     *
     * @param taskIndex the index where the task should be inserted.
     * @param task the task to insert.
     */
    public void insert(int taskIndex, Task task) {
        tasks.add(taskIndex, task);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param taskIndex the index of the task to mark.
     */
    public void markAsDone(int taskIndex) {
        tasks.get(taskIndex).markAsDone();
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param taskIndex the index of the task to unmark.
     */
    public void markAsNotDone(int taskIndex) {
        tasks.get(taskIndex).markAsNotDone();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param taskIndex the index of the task to return.
     * @return the requested task.
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns the number of tasks stored.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the tasks as an ArrayList for UI display and storage.
     *
     * @return a copy of the stored tasks.
     */
    public ArrayList<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
