package BusinessLogic;

import dataModels.Server;
import dataModels.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    // add Task to the server with the least amount of tasks
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server auxServer = servers.get(0);
        int minServer = auxServer.getTasks().size();
        for(Server server: servers){
            if(server.getTasks().size() < minServer){
                auxServer = server;
                minServer = server.getTasks().size();
            }
        }
        auxServer.addTask(t);
    }
}
