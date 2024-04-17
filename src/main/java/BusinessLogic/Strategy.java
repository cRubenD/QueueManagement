package BusinessLogic;

import dataModels.Server;
import dataModels.Task;

import java.util.List;

public interface Strategy {

    void addTask(List<Server> servers, Task t);

    enum SelectionPolicy{
        SHORTEST_QUEUE, SHORTEST_TIME;
    }
}
