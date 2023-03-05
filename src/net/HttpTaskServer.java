package net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.handlers.*;
import service.Managers;
import service.TaskManager;
import service.exceptions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    TaskManager manager;

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            manager = Managers.getDefault();
            httpServer.createContext("/tasks", new TasksHandler(this, manager));
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            throw new HttpServerException("Сбой при попытке создать Http-сервер", e);
        }
    }

    public TaskManager getManager() {
        return manager;
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public void writeResponse(HttpExchange httpExchange, Map.Entry<String, Integer> answer) {
        String responseString = answer.getKey();
        int responseCode = answer.getValue();
        byte[] byteData = responseString.getBytes(DEFAULT_CHARSET);
        try {
            httpExchange.sendResponseHeaders(responseCode, byteData.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(byteData);
            outputStream.close();
        } catch (IOException e) {
            throw new ServerResponseException("Произошла ошибка при формировании ответа клиенту", e);
        }
        httpExchange.close();
    }

}
