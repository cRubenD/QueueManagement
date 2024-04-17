package Helper;

import dataModels.Server;
import dataModels.Task;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.concurrent.BlockingQueue;

// class for displaying text in the ui
public class Logger {
    public TextArea logger;
    public  Logger(TextArea logger){
        this.logger = logger;
    }
    public synchronized void Logger(int currentTime, List<Task> tasks, List<Server> servers) {

        logger.appendText("\nTime " + currentTime + ": ");

        // print the clients that are not yet dispatched in queues
        if(!tasks.isEmpty()){
            logger.appendText("Waiting clients: ");
            for (Task task : tasks) {
                printClient(task.getID(), task.getArrivalTime(), task.getServiceTime());
            }
        }

        // print all the queues
        int i = 1;
        for (Server server : servers) {
            BlockingQueue<Task> serverQueue = server.getTasks();
            logger.appendText("Queue " + i + ": ");
            if (serverQueue.isEmpty()) {
                logger.appendText("Closed ");
            } else {
                for (Task task : serverQueue) {
                    printClient(task.getID(), task.getArrivalTime(), task.getServiceTime());
                }
            }
            i++;
        }
    }

    public void print(double number){
        logger.appendText("\nAverage Waiting Time: "+ number);
    }

    private void printClient(int id, int arrivalTime, int serviceTime){
        logger.appendText("(" + id + ", " + arrivalTime + ", " + serviceTime + "); ");
    }
}
