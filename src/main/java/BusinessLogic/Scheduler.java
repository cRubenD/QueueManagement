package BusinessLogic;

import dataModels.Server;
import dataModels.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

public class Scheduler {
    private final List<Server> servers;
    private int maxTasksPerServer;
    private Strategy strategy;

    // max # of queues, and max # of person per queue
    public Scheduler(int maxNoServers, int maxTasksPerServer, CyclicBarrier barrier, Strategy.SelectionPolicy selectionPolicy){
        this.servers = new ArrayList<>();
        changeStrategy(selectionPolicy);
        for(int i = 0; i < maxNoServers; i++){
            BlockingQueue<Task> queue = new ArrayBlockingQueue<>(maxTasksPerServer);
            Server server = new Server(queue, barrier);
            assert false;
            this.servers.add(server);
            Thread thread = new Thread(server);
            thread.start();
        }
    }

    // define the strategy
    public void changeStrategy(Strategy.SelectionPolicy policy){
        if(policy == Strategy.SelectionPolicy.SHORTEST_QUEUE){
            this.strategy = new ConcreteStrategyQueue();
        }
        if(policy == Strategy.SelectionPolicy.SHORTEST_TIME){
            this.strategy = new ConcreteStrategyTime();
        }
    }

    // call the addTask method
    public void dispatchTask(Task task){
        this.strategy.addTask(this.servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }
}
