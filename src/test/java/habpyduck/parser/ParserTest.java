package habpyduck.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import habpyduck.HabpyDuckException;
import habpyduck.command.AddCommand;
import habpyduck.command.Command;
import habpyduck.command.DeleteCommand;
import habpyduck.command.ExitCommand;
import habpyduck.command.ListCommand;
import habpyduck.command.MarkCommand;
import habpyduck.command.UnmarkCommand;

/**
 * Tests how raw user input is parsed into command types, command objects, dates,
 * and task indexes.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void getCommandType_knownCommands_returnsMatchingCommandTypes() {
        assertEquals(CommandType.LIST, parser.getCommandType("list"));
        assertEquals(CommandType.MARK, parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, parser.getCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, parser.getCommandType("delete 1"));
        assertEquals(CommandType.TODO, parser.getCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, parser.getCommandType("deadline return book /by 25/8/2026 1800"));
        assertEquals(CommandType.EVENT, parser.getCommandType("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.BYE, parser.getCommandType("bye"));
    }

    @Test
    public void getCommandType_commandWithExtraSpaces_returnsMatchingCommandType() {
        assertEquals(CommandType.TODO, parser.getCommandType("   todo read book   "));
    }

    @Test
    public void getCommandType_unknownOrBlankCommand_returnsUnknownCommandType() {
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("hello"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("   "));
    }

    @Test
    public void parse_validNonAddCommands_returnsMatchingCommandClasses() throws HabpyDuckException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    public void parse_validAddCommands_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 25/8/2026 1800"));
        assertInstanceOf(AddCommand.class, parser.parse("event meeting /from 2pm /to 4pm"));
    }

    @Test
    public void parse_byeCommand_returnsExitCommandThatExits() throws HabpyDuckException {
        Command command = parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertEquals(true, command.isExit());
    }

    @Test
    public void parse_blankCommand_exceptionThrown() {
        assertParseExceptionMessage("   ",
                "OH NO!!! I didn't catch a command, friend. Please type something for me.");
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertParseExceptionMessage("hello",
                "OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, or delete!");
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("todo",
                "OH NO!!! A todo needs a description, friend. Try something like: todo read book");
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        assertParseExceptionMessage("deadline return book",
                "OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY HHmm :)");
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("deadline  /by 25/8/2026 1800",
                "OH NO!!! A deadline needs a description, friend. Try again!");
    }

    @Test
    public void parse_deadlineWithoutDateTime_exceptionThrown() {
        assertParseExceptionMessage("deadline return book /by ",
                "OH NO!!! A deadline needs a date and time, friend. Try something like: 25/8/2026 1800");
    }

    @Test
    public void parse_deadlineWithInvalidDateTime_exceptionThrown() {
        assertParseExceptionMessage("deadline return book /by 2026-08-25",
                "OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800");
    }

    @Test
    public void parse_eventWithoutRequiredMarkers_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 2pm",
                "OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)");
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("event  /from 2pm /to 4pm",
                "OH NO!!! An event needs a description, friend. Try again!");
    }

    @Test
    public void parse_eventWithoutStart_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from  /to 4pm",
                "OH NO!!! An event needs a start time, friend. Try again!");
    }

    @Test
    public void parse_eventWithoutEnd_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 2pm /to ",
                "OH NO!!! An event needs an end time, friend. Try again!");
    }

    @Test
    public void parseUserDeadlineDateTime_validDateTime_returnsLocalDateTime() throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 25, 18, 0);

        assertEquals(expected, parser.parseUserDeadlineDateTime("25/8/2026 1800"));
    }

    @Test
    public void parseUserDeadlineDateTime_validSingleDigitDateAndTime_returnsLocalDateTime()
            throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 1, 5, 9, 30);

        assertEquals(expected, parser.parseUserDeadlineDateTime("5/1/2026 0930"));
    }

    @Test
    public void parseUserDeadlineDateTime_invalidFormat_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseUserDeadlineDateTime("2026-08-25 1800"));

        assertEquals("OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800",
                exception.getMessage());
    }

    @Test
    public void parseUserDeadlineDateTime_blankText_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseUserDeadlineDateTime("   "));

        assertEquals("OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_firstTask_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark", 3));
    }

    @Test
    public void parseTaskIndex_lastTask_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(2, parser.parseTaskIndex("mark 3", "mark", 3));
    }

    @Test
    public void parseTaskIndex_extraSpaces_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(1, parser.parseTaskIndex("delete    2   ", "delete", 3));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseTaskIndex("unmark", "unmark", 3));

        assertEquals("OH NO!!! Please tell me which task to unmark, like: unmark 2",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseTaskIndex("delete abc", "delete", 3));

        assertEquals("OH NO!!! Please use a number after delete, like: delete 2",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseTaskIndex("mark 0", "mark", 3));

        assertEquals("OH NO!!! Task 0 does not exist in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_negativeTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseTaskIndex("mark -1", "mark", 3));

        assertEquals("OH NO!!! Task -1 does not exist in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class,
                () -> parser.parseTaskIndex("mark 4", "mark", 3));

        assertEquals("OH NO!!! Task 4 does not exist in your list.", exception.getMessage());
    }

    private void assertParseExceptionMessage(String command, String expectedMessage) {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class, () -> parser.parse(command));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
