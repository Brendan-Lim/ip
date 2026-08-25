/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the task with the given user-facing number.
     *
     * @param taskNumber the one-based task number entered by the user
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as done, saves the updated list, and reports the change.
     *
     * @param tasks the task list to update
     * @param ui the UI used to show command results
     * @param storage the storage used to save tasks
     * @throws HabpyDuckException if the task does not exist or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        tasks.markAsDone(taskIndex);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            tasks.markAsNotDone(taskIndex);
            throw e;
        }
        ui.showTaskMarked(tasks.get(taskIndex));
    }
}
