import model.*;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;
import java.util.Random;

public class Main {

    public static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {

        Task goWalk = new Task("Пойти на прогулку", "Минут 15-30 спокойной ходьбы");
        Task cleanRoom = new Task("Прибраться в квартире", "Не забыть пропылесосить!");
        taskManager.addToList(goWalk);
        taskManager.addToList(cleanRoom);
        Epic becomeDeveloper = new Epic( "Стать разработчиком",
                "Освоить технологию Java и устроиться на работу по специальности");
        taskManager.addToList(becomeDeveloper);
        Subtask studyJava = new Subtask(becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java");
        taskManager.addToList(studyJava);
        Subtask findJob = new Subtask(becomeDeveloper,
                "Найти работу по специальности","Специальность Java developer");
        taskManager.addToList(findJob);
        Epic findSock = new Epic("Найти второй носок после стирки", "Крайне маловероятно");
        taskManager.addToList(findSock);

        fillHistory();
        browseHistory();
        System.out.println("-------------------------------------------------------------------");

        for (int i = 0; i < 10; i++) {
            getRandomTask();
        }
        browseHistory();
        System.out.println("-------------------------------------------------------------------");

        taskManager.deleteTask(1);
        taskManager.deleteEpicTask(3);
        browseHistory();

    }

    public static void fillHistory() {
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(6);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
    }

    public static void getRandomTask() {
        Random random = new Random();
        int index = random.nextInt(6) + 1;
        System.out.println("Запрошена задача с ID = " + index);
        taskManager.getTask(index);
    }

    public static void browseHistory() {
        InMemoryTaskManager itm = (InMemoryTaskManager) taskManager;
        itm.printHistory();
    }

}
