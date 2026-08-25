/**
 * Represents a task that needs to be done before a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description the text that describes the task
     * @param by the date or time by which the task should be done
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file
     */
    @Override
    public String toFileString() {
        return "D | " + getDoneStatus() + " | " + description + " | " + by;
    }

    /**
     * Returns the deadline task in the format shown to users.
     *
     * @return the task type, status icon, description, and deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
