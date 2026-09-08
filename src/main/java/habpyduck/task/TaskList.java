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
        assert !tasks.isEmpty() : "Task list should not be empty when removing the last task";
        return tasks.remove(tasks.size() - 1);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex the index of the task to remove.
     * @return the removed task.
     */
    public Task delete(int taskIndex) {
        assert isValidIndex(taskIndex) : "Task index should be valid before deleting a task";
        return tasks.remove(taskIndex);
    }

    /**
     * Inserts a task at the given zero-based index.
     *
     * @param taskIndex the index where the task should be inserted.
     * @param task the task to insert.
     */
    public void insert(int taskIndex, Task task) {
        assert taskIndex >= 0 && taskIndex <= tasks.size() : "Task index should be valid before inserting a task";
        tasks.add(taskIndex, task);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param taskIndex the index of the task to mark.
     */
    public void markAsDone(int taskIndex) {
        assert isValidIndex(taskIndex) : "Task index should be valid before marking a task";
        tasks.get(taskIndex).markAsDone();
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param taskIndex the index of the task to unmark.
     */
    public void markAsNotDone(int taskIndex) {
        assert isValidIndex(taskIndex) : "Task index should be valid before unmarking a task";
        tasks.get(taskIndex).markAsNotDone();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword the text to search for in task descriptions.
     * @return tasks with descriptions that contain the keyword.
     */
    public ArrayList<Task> findByKeyword(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerCaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param taskIndex the index of the task to return.
     * @return the requested task.
     */
    public Task get(int taskIndex) {
        assert isValidIndex(taskIndex) : "Task index should be valid before retrieving a task";
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

    /**
     * Returns whether an index points to an existing task.
     *
     * @param taskIndex the zero-based index to check.
     * @return true if the index points to a task in the list.
     */
    private boolean isValidIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size();
    }
}
