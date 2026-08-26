package habpyduck.task;

import habpyduck.storage.Storage;

/**
 * Represents one task in the user's task list.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description the text that describes the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show whether this task is done.
     *
     * @return X if the task is done, or a blank space if it is not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the text that describes the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    public String toFileString() {
        return "T | " + getDoneStatus() + " | " + Storage.escapeFileField(description);
    }

    /**
     * Returns the numeric status used in the save file.
     *
     * @return 1 if this task is done, or 0 if it is not done.
     */
    protected String getDoneStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the task in the format shown to users.
     *
     * @return the task status icon and description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
