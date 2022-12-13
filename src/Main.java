import model.*;
import service.TaskManager;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Создали новый Эпик и две подзадачи.");
        Epic becomeDeveloper = new Epic(taskManager.getNextID(), "Стать разработчиком",
                "Освоить технологию Java и устроиться на работу по специальности");
        taskManager.addToList(becomeDeveloper);
        Subtask studyJava = new Subtask(taskManager.getNextID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.NEW);
        Subtask findJob = new Subtask(taskManager.getNextID(), becomeDeveloper,
                "Найти работу по специальности","Специальность Java developer", TaskStatus.NEW);
        taskManager.addToList(studyJava);
        taskManager.addToList(findJob);

        studyJava = new Subtask(studyJava.getTaskID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.IN_PROGRESS);
        taskManager.addToList(studyJava);
        System.out.println("--------------------------------------");
        studyJava = new Subtask(studyJava.getTaskID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.DONE);
        findJob = new Subtask(findJob.getTaskID(), becomeDeveloper, "Найти работу по специальности",
                "Специальность Java developer", TaskStatus.DONE);

        System.out.println("Получим списки всех типов задач:");
        List<Task> taskList = taskManager.getTaskList();
        List<Task> epicList = taskManager.getEpicTaskList();
        List<Task> subtaskList = becomeDeveloper.getSubtaskList();
        taskList.forEach(System.out::println);
        epicList.forEach(System.out::println);
        subtaskList.forEach(System.out::println);
        System.out.println("--------------------------------");

    }

}
