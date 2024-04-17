package BusinessLogic;

import Helper.Convert;
import Helper.Exception;
import Helper.Logger;
import dataModels.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static Helper.Convert.convertTextField;

public class SimulationManager implements Runnable {

    // all the inputs in the application
    private int numberOfClients;
    private int numberOfQueues;
    private int simulationTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int minServiceTime;
    private int maxServiceTime;

    // entity responsible with the queue management and client distribution
    private Scheduler scheduler;
    // pool of tasks
    private List<Task> generatedTasks;

    // real time of the simulation
    private static final AtomicInteger currentTime = new AtomicInteger(0);
    private CyclicBarrier barrier;
    private Logger logger;

    // we can change the strategy of the application here
    public Strategy.SelectionPolicy selectionPolicy = Strategy.SelectionPolicy.SHORTEST_QUEUE;

    public SimulationManager() {

    }

    public List<Task> getGeneratedTasks() {
        return generatedTasks;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public synchronized static AtomicInteger getCurrentTime() {
        return currentTime;
    }

    // method to take all the input data from the interface
    // we also verify if the data is valid
    public void SimulationManagerInit(TextField NOClients, TextField NOQueues, TextField SimulationTime,
                                      TextField minArrivalTime, TextField maxArrivalTime, TextField minServiceTime,
                                      TextField maxServiceTime) throws Exception {
        if(!Convert.verify(NOClients, NOQueues, SimulationTime, minArrivalTime, maxArrivalTime,
                    minServiceTime, maxServiceTime)){
            throw new Exception("Invalid input data! All inputs must be valid numbers!");
        }
        this.numberOfClients = convertTextField(NOClients);
        this.numberOfQueues = convertTextField(NOQueues);
        this.simulationTime = convertTextField(SimulationTime);
        this.minArrivalTime = convertTextField(minArrivalTime);
        this.maxArrivalTime = convertTextField(maxArrivalTime);
        this.minServiceTime = convertTextField(minServiceTime);
        this.maxServiceTime = convertTextField(maxServiceTime);

    }

    // initialize the Simulation with all the resources that we need
    public void SimulationManagerMethod(Logger logger, ProgressBar progress){
        this.logger = logger;
        this.barrier = new CyclicBarrier(numberOfQueues + 1);
        this.scheduler = new Scheduler(getNumberOfQueues(), getNumberOfClients(), barrier, selectionPolicy);
        this.generatedTasks = new ArrayList<>();

        generateNRandomTasks(this.numberOfClients, this.generatedTasks);
    }

    // method to generate Clients
    private void generateNRandomTasks(int numberOfClients, List<Task> generatedTasks){

        this.numberOfClients = numberOfClients;
        this.generatedTasks = generatedTasks;
        Random random = new Random();
        for(int i = 0; i < numberOfClients; i++){
            Task task = new Task();
            task.setID(i + 1);
            int arrivalTime = random.nextInt(minArrivalTime, maxArrivalTime);
            int serviceTime = random.nextInt(minServiceTime, maxServiceTime);

            task.setArrivalTime(arrivalTime);
            task.setServiceTime(serviceTime);

            generatedTasks.add(task);
        }
        // comparator.comparing - abstract method from Comparator class, Task::getArrivalTime is equal to
        // lambda expression task->task.getArrivalTime
        generatedTasks.sort(Comparator.comparing(Task::getArrivalTime));
    }

    // main thread of the application (beside the 'main' one) responsible for dispatching clients
    // in all the queues at the right time. Also, currentTime is general used
    @Override
    public void run() {

        while(currentTime.get() <= simulationTime && (!(generatedTasks.isEmpty()) || !(this.scheduler.getServers().isEmpty()))) {

            // dispatching tasks at the right time
            List<Task> copy = new ArrayList<>(generatedTasks);
            for(Task tasks: copy){
                if(tasks.getArrivalTime() == currentTime.get()){
                    this.scheduler.dispatchTask(tasks);
                    generatedTasks.remove(tasks);
                }
            }
            // barrier ensures synchronization between all servers
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            logger.Logger(currentTime.get(), generatedTasks, this.scheduler.getServers());
            currentTime.incrementAndGet();
            try {
                barrier.await();
                Thread.sleep(1000);
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
