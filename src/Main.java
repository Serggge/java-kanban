import model.*;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Managers<TaskManager> manager = new Managers<>(inMemoryTaskManager);
        TaskManager taskManager = manager.getDefault();

        Epic becomeDeveloper = new Epic(taskManager.getNextID(), "Стать разработчиком",
                "Освоить технологию Java и устроиться на работу по специальности");
        taskManager.addToList(becomeDeveloper);
        Subtask studyJava = new Subtask(taskManager.getNextID(), becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java", TaskStatus.NEW);
        Subtask findJob = new Subtask(taskManager.getNextID(), becomeDeveloper,
                "Найти работу по специальности","Специальность Java developer", TaskStatus.NEW);

        inMemoryTaskManager = (InMemoryTaskManager) taskManager;
        for (int i = 0; i < 4; i++) {
            Task task;
            task = taskManager.getEpic(1);
            task = taskManager.getSubtask(2);
            task = taskManager.getSubtask(3);
            inMemoryTaskManager.printHistory();
            System.out.println("-------------------------------------------------------------------------------");
        }
    }

}
