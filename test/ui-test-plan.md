# UI Test Plan

## Test Case: task types and status changes

### Aim

Verify that the chatbot can add todos, deadlines, and events; list them with type icons; mark a task as done; and unmark it again.

### Inputs

```text
todo read book
deadline return book /by 25/8/2026 1800
event project meeting /from Mon 2pm /to 4pm
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Aug 25 2026, 6:00pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Aug 25 2026, 6:00pm)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet, all the best friend:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 25 2026, 6:00pm)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: saved tasks load on startup

### Aim

Verify that the chatbot loads todo, deadline, and event tasks from the saved file when it starts, including their done statuses.

### Initial saved file content

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T09:00
E | 1 | project meeting | Aug 6th 2pm | 4pm
```

### Inputs

```text
list
todo join sports club
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019, 9:00am)
3.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019, 9:00am)
3.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T09:00
E | 1 | project meeting | Aug 6th 2pm | 4pm
T | 0 | join sports club
```

## Test Case: malformed saved tasks are skipped

### Aim

Verify that malformed saved task lines are reported and skipped, while valid saved tasks still load.

### Initial saved file content

```text
T | 1 | read book
D | 2 | bad status | tomorrow
X | 0 | bad type
D | 0 | missing date
D | 0 | invalid date | tomorrow
E | 1 | project \| meeting | C:\\start | 4\|5pm
T | 0 |    
```

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! I had trouble loading saved task on line 2: status must be 0 or 1
OH NO!!! I had trouble loading saved task on line 3: unknown task type 'X'
OH NO!!! I had trouble loading saved task on line 4: expected 4 fields but found 3
OH NO!!! I had trouble loading saved task on line 5: saved deadline date and time must use yyyy-MM-ddTHH:mm format
OH NO!!! I had trouble loading saved task on line 7: task details cannot be empty
Here are the tasks in your list:
1.[T][X] read book
2.[E][X] project | meeting (from: C:\start to: 4|5pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: special file characters are escaped when saved

### Aim

Verify that task text containing pipes and backslashes can be saved without breaking the file format.

### Inputs

```text
todo read | book
deadline path \ home /by 8/8/2026 0830
event sync | call /from room \A /to 4 | 5pm
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read | book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] path \ home (by: Aug 8 2026, 8:30am)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] sync | call (from: room \A to: 4 | 5pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read | book
2.[D][ ] path \ home (by: Aug 8 2026, 8:30am)
3.[E][ ] sync | call (from: room \A to: 4 | 5pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 0 | read \| book
D | 0 | path \\ home | 2026-08-08T08:30
E | 0 | sync \| call | room \\A | 4 \| 5pm
```

## Test Case: empty input exits cleanly

### Aim

Verify that the chatbot does not crash if standard input ends before the user enters bye.

### Inputs

```text

```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: delete removes a task and preserves remaining state

### Aim

Verify that deleting a middle task removes only that task, renumbers the remaining tasks, keeps their done statuses, and handles invalid delete commands without changing the list.

### Inputs

```text
todo read book
deadline return book /by 6/6/2019 0900
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 1
mark 2
mark 4
list
delete 3
list
delete
delete abc
delete 9
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 6 2019, 9:00am)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [D][X] return book (by: Jun 6 2019, 9:00am)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019, 9:00am)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019, 9:00am)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to delete, like: delete 2
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after delete, like: delete 2
____________________________________________________________
____________________________________________________________
OH NO!!! Task 9 does not exist in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019, 9:00am)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 1 | read book
D | 1 | return book | 2019-06-06T09:00
T | 1 | join sports club
```

## Test Case: invalid commands do not change task state

### Aim

Verify that invalid commands interleaved with valid commands do not add extra tasks or change the done status of existing tasks.

### Inputs

```text
list
todo alpha
todo
blah
list
deadline beta /by 13/12/2019 2359
event gamma /from 1pm /to 2pm
event delta /from 3pm
mark 2
mark 99
unmark abc
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! A todo needs a description, friend. Try something like: todo read book
____________________________________________________________
____________________________________________________________
OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, or delete!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: Dec 13 2019, 11:59pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: 1pm to: 2pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [D][X] beta (by: Dec 13 2019, 11:59pm)
____________________________________________________________
____________________________________________________________
OH NO!!! Task 99 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after unmark, like: unmark 2
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet, all the best friend:
  [D][ ] beta (by: Dec 13 2019, 11:59pm)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: Dec 13 2019, 11:59pm)
3.[E][ ] gamma (from: 1pm to: 2pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: command boundary cases with valid recovery

### Aim

Verify that blank commands, missing command details, and invalid task number boundaries are handled, and that later valid commands still update the list correctly.

### Inputs

```text

deadline
event
unmark
todo keep notes
unmark 1
mark 0
mark -1
mark 1
deadline do homework /by 4/10/2019 1200
event orientation week /from 4/10/2019 /to 11/10/2019
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! I didn't catch a command, friend. Please type something for me.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY HHmm :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to unmark, like: unmark 2
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep notes
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet, all the best friend:
  [T][ ] keep notes
____________________________________________________________
____________________________________________________________
OH NO!!! Task 0 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Task -1 does not exist in your list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] keep notes
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: Oct 4 2019, 12:00pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] keep notes
2.[D][ ] do homework (by: Oct 4 2019, 12:00pm)
3.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid command handling

### Aim

Verify that the chatbot reports user-friendly errors for empty task descriptions, unknown commands, invalid task numbers, and malformed deadline/event commands.

### Inputs

```text
todo
blah
mark
mark abc
mark 1
deadline return book
deadline /by Sunday
deadline return book /by 2019-10-15
deadline return book /by 25/8/2026 1800
event meeting /from Mon 2pm
event meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! A todo needs a description, friend. Try something like: todo read book
____________________________________________________________
____________________________________________________________
OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, or delete!
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to mark, like: mark 2
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after mark, like: mark 2
____________________________________________________________
____________________________________________________________
OH NO!!! Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY HHmm :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY HHmm :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please enter the deadline date and time in DD/MM/YYYY HHmm format, like: 25/8/2026 1800
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Aug 25 2026, 6:00pm)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Aug 25 2026, 6:00pm)
2.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```
