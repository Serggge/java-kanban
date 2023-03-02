import net.HttpTaskServer;
import net.KVServer;
import net.KVTaskClient;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        new KVServer().start();
        new HttpTaskServer().start();
/*        KVTaskClient client = new KVTaskClient("http://localhost:8078");
        System.out.println(client.getApiToken());*/
    }

}
