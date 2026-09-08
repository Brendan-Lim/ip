# HabpyDuck User Guide

HabpyDuck is a task-tracking chatbot that helps you remember todos, deadlines, and events.

## Quick Start

1. Launch HabpyDuck.
2. Type a command in the input box or console.
3. Press Enter to run the command.

## Command Summary

| Action | Format |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DD/MM/YYYY HHmm` |
| Add an event | `event DESCRIPTION /from START /to END` |
| List tasks | `list` |
| Mark a task as done | `mark TASK_NUMBER` |
| Mark a task as not done | `unmark TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` |
| Find matching tasks | `find KEYWORD` |
| Tag a task | `tag TASK_NUMBER #TAG...` |
| Remove a tag | `untag TASK_NUMBER #TAG` |
| Find tagged tasks | `findtag #TAG` |
| Exit | `bye` |

## Adding Todos

Adds a task without a date or time.

Example:

```text
todo read book
```

Expected output:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

## Adding Deadlines

Adds a task that must be completed by a specific date and time. Use `DD/MM/YYYY HHmm` format for the date and time.

Example:

```text
deadline return book /by 25/8/2026 1800
```

Expected output:

```text
Got it. I've added this task:
  [D][ ] return book (by: Aug 25 2026, 6:00pm)
Now you have 2 tasks in the list.
```

## Adding Events

Adds a task that starts and ends at given times.

Example:

```text
event project meeting /from Mon 2pm /to 4pm
```

Expected output:

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
```

## Listing Tasks

Shows all saved tasks.

Example:

```text
list
```

Expected output:

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 25 2026, 6:00pm)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Marking And Unmarking Tasks

Marks a task as done or not done. Task numbers are shown by the `list` command.

Examples:

```text
mark 1
unmark 1
```

Expected output for `mark 1`:

```text
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] read book
```

Expected output for `unmark 1`:

```text
OK, I've marked this task as not done yet, all the best friend:
  [T][ ] read book
```

## Deleting Tasks

Removes a task from the list. Task numbers are shown by the `list` command.

Example:

```text
delete 1
```

Expected output:

```text
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
```

## Finding Tasks

Shows tasks whose descriptions contain the keyword. The search is case-insensitive.

Example:

```text
find book
```

Expected output:

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 25 2026, 6:00pm)
```

## Tagging Tasks

Adds one or more tags to a task. Tags must start with `#` and can use letters, numbers, hyphens, and underscores.
Tags are stored in lowercase, so `#Fun` and `#fun` are treated as the same tag.

Example:

```text
tag 1 #fun #school_work
```

Expected output:

```text
Got it. I've tagged this task:
  [T][ ] read book #fun #school_work
```

## Removing Tags

Removes one tag from a task.

Example:

```text
untag 1 #fun
```

Expected output:

```text
Got it. I've removed that tag from this task:
  [T][ ] read book #school_work
```

## Finding Tagged Tasks

Shows tasks that have the given tag. Normal `find` still searches task descriptions only.

Example:

```text
findtag #school_work
```

Expected output:

```text
Here are the tasks with that tag:
1.[T][ ] read book #school_work
```

## Exiting

Ends the current HabpyDuck session.

Example:

```text
bye
```

Expected output:

```text
Bye friend. Hope to see you again soon!
```
