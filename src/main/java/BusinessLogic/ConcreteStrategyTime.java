package BusinessLogic;

import dataModels.Server;
import dataModels.Task;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConcreteStrategyTime implements Strategy{
    // add Task to the server with the shortest processing time
    @Override
    public void addTask(List<Server> servers, Task t) {
        int minTime = Integer.MAX_VALUE;
        Server minServer = null;
        for (Server server : servers) {
            int auxTime = 0;
            BlockingQueue<Task> tasks = server.getTasks();
            int currentTime = SimulationManager.getCurrentTime().get();
            for (Task task : tasks) {
                if (task == tasks.peek()) {
                    auxTime -= currentTime;
                }
                auxTime += task.getServiceTime() + task.getArrivalTime();
            }
            if (auxTime < minTime) {
                minTime = auxTime;
                minServer = server;

            }
            minServer.addTask(t);
        }
    }
}
