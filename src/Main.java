import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import net.HttpTaskServer;
import net.KVServer;
import net.util.CustomGson;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {


        TaskManager taskManager = new InMemoryTaskManager();
        Gson gson = CustomGson.getGson();

        Epic epic = new Epic("Epic", "description");
        taskManager.addToList(epic);
        String jsonEpic = gson.toJson(epic);
        System.out.println(jsonEpic);

        Subtask subtask = new Subtask(epic, "Subtask", "desc", "01.02.2023", "04:00", 10);
        taskManager.addToList(subtask);
        String jsonSubtask = gson.toJson(subtask);
        System.out.println(jsonSubtask);
        Task subtaskFromJson = gson.fromJson(jsonSubtask, Subtask.class);
        System.out.println(subtaskFromJson);
        Task epicFromJson = gson.fromJson(jsonEpic, Epic.class);
        System.out.println(epicFromJson);




/*        int first = taskManager.addToList(task1);
        int second = taskManager.addToList(task2);
        int third = taskManager.addToList(epic);
        int fourth = taskManager.addToList(subtask);
        taskManager.getEpicById(third);
        taskManager.getTaskById(second);
        taskManager.getTaskById(first);
        taskManager.getSubtaskById(fourth);*/

        Type token = new TypeToken<Map<Integer, Task>>() {
        }.getType();


/*        new KVServer().start();
        Thread.sleep(2000);
        new HttpTaskServer().start();*/

    }

}
