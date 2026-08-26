package habpyduck.task;

import habpyduck.storage.Storage;

/**
 * Represents a task that starts at a specific date or time and ends at another.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description the text that describes the event.
     * @param from the date or time when the event starts.
     * @param to the date or time when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    @Override
    public String toFileString() {
        return "E | " + getDoneStatus() + " | " + Storage.escapeFileField(description)
                + " | " + Storage.escapeFileField(from) + " | " + Storage.escapeFileField(to);
    }

    /**
     * Returns the event task in the format shown to users.
     *
     * @return the task type, status icon, description, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
