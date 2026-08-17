# UI Test Plan

## Test Case: task types and status changes

### Aim

Verify that the chatbot can add todos, deadlines, and events; list them with type icons; mark a task as done; and unmark it again.

### Inputs

```text
todo read book
deadline return book /by Sunday
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
  [D][ ] return book (by: Sunday)
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
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet, all the best friend:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
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
deadline return book /by June 6th
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
  [D][ ] return book (by: June 6th)
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
  [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
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
2.[D][X] return book (by: June 6th)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Please tell me which task to delete, like: delete 2
____________________________________________________________
____________________________________________________________
Please use a number after delete, like: delete 2
____________________________________________________________
____________________________________________________________
Task 9 does not exist in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
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
deadline beta /by Friday
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
The description of a todo cannot be empty.
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
  [D][ ] beta (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: 1pm to: 2pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Please use this format: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [D][X] beta (by: Friday)
____________________________________________________________
____________________________________________________________
Task 99 does not exist in your list.
____________________________________________________________
____________________________________________________________
Please use a number after unmark, like: unmark 2
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet, all the best friend:
  [D][ ] beta (by: Friday)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: Friday)
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
deadline do homework /by no idea :-p
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
Please enter a command.
____________________________________________________________
____________________________________________________________
Please use this format: deadline DESCRIPTION /by WHEN :)
____________________________________________________________
____________________________________________________________
Please use this format: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
Please tell me which task to unmark, like: unmark 2
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
Task 0 does not exist in your list.
____________________________________________________________
____________________________________________________________
Task -1 does not exist in your list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done:
  [T][X] keep notes
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
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
2.[D][ ] do homework (by: no idea :-p)
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
deadline return book /by Sunday
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
The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, or delete!
____________________________________________________________
____________________________________________________________
Please tell me which task to mark, like: mark 2
____________________________________________________________
____________________________________________________________
Please use a number after mark, like: mark 2
____________________________________________________________
____________________________________________________________
Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
Please use this format: deadline DESCRIPTION /by WHEN :)
____________________________________________________________
____________________________________________________________
Please use this format: deadline DESCRIPTION /by WHEN :)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Please use this format: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Sunday)
2.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```
