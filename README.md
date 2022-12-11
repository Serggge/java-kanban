# java-kanban
## Это репозиторий проекта "ТРЕКЕР ЗАДАЧ"

Наше приложение **умеет**:
1. Создавать различные типы задач.
* Простые задачи.
* Большие задачи (Эпики).
* Подзадачи Эпиков.
2. Автоматически рассчитывать статус выполнения Эпиков в зависимости от статуса его подзадач.

Приложение написано на Java. Пример кода:
```java
    public static void createTask(Task task) {
        taskID++;
        task.setTaskID(taskID);
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Epic.class) {
            Epic epicTask = (Epic) task;
            epicTaskList.put(task.getTaskID(), epicTask);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            subtask.getParentTask().getSubtaskList().put(subtask.getTaskID(), subtask);
            subtaskList.put(task.getTaskID(), subtask);
            checkEpicTaskStatus(subtask.getParentTask());
        }
    }
```

-----------
Автор работы: *Сергей Власов* (aka Serggge)