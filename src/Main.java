import model.*;
import service.TaskManager;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Создали новый Эпик и две подзадачи.");
        Epic becomeDeveloper = new Epic("Стать разработчиком",
                "Освоить технологию Java и устроиться на работу по специальности");
        taskManager.createTask(becomeDeveloper);
        Subtask studyJava = new Subtask(becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.NEW);
        Subtask findJob = new Subtask(becomeDeveloper, "Найти работу по специальности",
                "Специальность Java developer", TaskStatus.NEW);
        taskManager.createTask(studyJava);
        taskManager.createTask(findJob);
        System.out.println("Получим списки всех типов задач:");
        ArrayList<Task> taskList = taskManager.getTaskList();
        ArrayList<Epic> epicList = taskManager.getEpicTaskList();
        ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
        taskList.forEach(System.out::println);
        epicList.forEach(System.out::println);
        subtaskList.forEach(System.out::println);
        System.out.println("--------------------------------");

        System.out.println("Проверили список подзадач Эпика:");
        ArrayList<Subtask> subtasks = taskManager.getTaskListOfEpic(becomeDeveloper);
        subtasks.forEach(System.out::println);
        System.out.println("--------------------------------");

        System.out.println("Обновили статус первой подзадачи на: \"в прогрессе\"");
        studyJava = new Subtask(studyJava.getTaskID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курс Яндекс.Практикум по разработке на Java", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(studyJava);
        System.out.println("Получим и выведем каждую задачу по ID:");
        Task epicTask = taskManager.getTaskByID(1);
        Task firstSubtask = taskManager.getTaskByID(2);
        Task secondSubtask = taskManager.getTaskByID(3);
        System.out.println(epicTask);
        System.out.println(firstSubtask);
        System.out.println(secondSubtask);
        System.out.println("--------------------------------");

        System.out.println("Проверка статуса Эпик, когда все подзадачи выполнены:");
        studyJava = new Subtask(studyJava.getTaskID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курс Яндекс.Практикум по разработке на Java", TaskStatus.DONE);
        findJob = new Subtask(findJob.getTaskID(), becomeDeveloper, "Найти работу по специальности",
                "Специальность Java developer", TaskStatus.DONE);
        taskManager.updateTask(studyJava);
        taskManager.updateTask(findJob);
        System.out.println("Выведем Эпик и подзадачи Эпика по его ID");
        epicTask = taskManager.getTaskByID(1);
        subtasks = taskManager.getSubtaskListOfEpicByID(1);
        System.out.println(epicTask);
        subtasks.forEach(System.out::println);
        System.out.println("--------------------------------");

        System.out.println("Провека корректности статуса Эпик при удалении подзадачи:");
        taskManager.deleteSubtasks();
        studyJava = new Subtask(becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.NEW);
        taskManager.createTask(studyJava);
        System.out.println("После создания подзадачи:");
        taskManager.printEpicTaskList();
        taskManager.printSubtaskList();
        System.out.println("После выполнения задачи:");
        studyJava = new Subtask(4, becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.DONE);
        taskManager.updateTask(studyJava);
        taskManager.printEpicTaskList();
        taskManager.printSubtaskList();
        System.out.println("После удаления подзадачи по ID:");
        taskManager.deleteTaskByID(4);
        taskManager.printEpicTaskList();
        taskManager.printSubtaskList();
        System.out.println("--------------------------------");

        System.out.println("Проверка удаления Эпика по ID и его связанных подзадач:");
        System.out.println("Создадим второй Эпик и подзадачу к нему:");
        Epic testEpic = new Epic("test Epic", "second Epic");
        taskManager.createTask(testEpic);
        Subtask subtaskTestEpic = new Subtask(testEpic, "test subtask",
                "subtask of second Epic", TaskStatus.NEW);
        taskManager.createTask(subtaskTestEpic);
        System.out.println("Создадим подзадачу для первого эпика:");
        studyJava = new Subtask(becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.NEW);
        taskManager.createTask(studyJava);
        System.out.println("Проверим текущее состояние:");
        printAllTasks(taskManager);
        System.out.println("После удаление первого Эпика:");
        taskManager.deleteEpicTaskByID(1);
        printAllTasks(taskManager);
        System.out.println("--------------------------------");

        System.out.println("Проверка удаления подзадач при удалении списка Эпиков\nСоздали и вывели:");
        Epic epic1 = new Epic("one", "comment");
        Epic epic2 = new Epic("two", "comment");
        taskManager.createTask(epic1);
        taskManager.createTask(epic2);
        firstSubtask = new Subtask(epic1, "first", "comment", TaskStatus.NEW);
        secondSubtask = new Subtask(epic1, "second", "comment", TaskStatus.NEW);
        Subtask thirdSubtask = new Subtask(epic2, "third", "comment", TaskStatus.NEW);
        Subtask fourthSubtask = new Subtask(epic2, "fourth", "comment", TaskStatus.NEW);
        taskManager.createTask(firstSubtask);
        taskManager.createTask(secondSubtask);
        taskManager.createTask(thirdSubtask);
        taskManager.createTask(fourthSubtask);
        printAllTasks(taskManager);
        System.out.println("Удалили Эпики и проверили список подзадач:");
        taskManager.deleteEpicTasks();
        taskManager.printSubtaskList();
        System.out.println("--------------------------------");

        System.out.println("Проверка удаления информации о подзадачах в Эпике");
        epic1 = new Epic("one", "comment");
        taskManager.createTask(epic1);
        firstSubtask = new Subtask(epic1, "first", "comment", TaskStatus.NEW);
        secondSubtask = new Subtask(epic1, "second", "comment", TaskStatus.NEW);
        taskManager.createTask(firstSubtask);
        taskManager.createTask(secondSubtask);
        taskManager.deleteSubtasks();
        taskManager.printEpicTaskList();
        taskManager.printSubtaskListOfEpic(epic1);
    }
    public static void printAllTasks(TaskManager taskManager) {
        taskManager.printTaskList();
        taskManager.printEpicTaskList();
        taskManager.printSubtaskList();
    }

}
