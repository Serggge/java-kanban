import model.*;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;
import java.util.Random;

public class Main {

    public static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {

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
