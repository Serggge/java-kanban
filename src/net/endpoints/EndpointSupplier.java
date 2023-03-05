package net.endpoints;

import java.util.regex.Pattern;

public class EndpointSupplier {

    private EndpointSupplier() {

    }

    public static EndpointTasks getTasksEndpoint(String path, String query, String method) {
        EndpointTasks endpoint = EndpointTasks.UNKNOWN;
        if (query == null) {
            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/tasks/?$", path)) {
                        endpoint = EndpointTasks.GET_PRIORITIZED_TASKS;
                    } else if (Pattern.matches("^/tasks/history/?$", path)) {
                        endpoint = EndpointTasks.GET_HISTORY;
                    } else if (Pattern.matches("^/tasks/(task|subtask|epic)/?$", path)) {
                        endpoint = EndpointTasks.GET_TASKS_BY_TYPE;
                    }
                }
                break;
                case "POST": {
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/?$", path)) {
                        endpoint = EndpointTasks.ADD_TASK;
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/?$", path)) {
                        endpoint = EndpointTasks.DELETE_TASKS_BY_TYPE;
                    }
                }
            }
        } else {
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/$", path)) {
                        endpoint = EndpointTasks.GET_TASK_BY_ID;
                    } else if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        endpoint = EndpointTasks.GET_EPIC_SUBTASKS;
                    }
                    break;
                case "POST":
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/$", path)) {
                        endpoint = EndpointTasks.DELETE_TASK_BY_ID;
                    }
            }
        }
        return endpoint;
    }
}
