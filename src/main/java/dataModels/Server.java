package dataModels;

import BusinessLogic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

// in this case a server is basically a queue
public class Server implements Runnable{

    // blockingQueue - type of queue that ensures enqueue or dequeue will wait till the next
    // thread will remove or add an element if the queue is full or empty
    private final BlockingQueue<Task> tasks;
    // AtomicInteger is used in multithreaded programming to ensure thread safety when working
    // with integer variables shared among multiple threads
    private final AtomicInteger waitingPeriod;
    private final CyclicBarrier barrier;
    private AtomicInteger currentTime;

    //initialize queue and waitingPeriod
    public Server(BlockingQueue<Task> tasks, CyclicBarrier barrier){
        this.tasks = tasks;
        this.waitingPeriod = new AtomicInteger(0);
        this.barrier = barrier;
    }

    public synchronized void addTask(Task newTask){
        this.tasks.add(newTask);
    }

   @Override
    public void run() {
       //try {
           while (!Thread.currentThread().isInterrupted()) {
               try {
                   barrier.await();
                   currentTime = SimulationManager.getCurrentTime();
                   if (!tasks.isEmpty()) {
                       Task task = tasks.peek();
                       if (currentTime.get() == (task.getArrivalTime() + task.getServiceTime() + waitingPeriod.get())) {
                           // we take the first task from the queue
                           try {
                               tasks.take();
                           } catch (InterruptedException e) {
                               throw new RuntimeException(e);
                           }
                           if(!tasks.isEmpty()){
                               Task task1 = tasks.peek();
                               waitingPeriod.addAndGet(task.getServiceTime() + task.getArrivalTime() - task1.getArrivalTime());
                           }
                           barrier.await();
                           Thread.sleep(1000);
                       }
                   }
               } catch (BrokenBarrierException | InterruptedException e) {
                   Thread.currentThread().interrupt();
                   throw new RuntimeException(e);
               }
           }
    }

    public BlockingQueue<Task> getTasks(){
        return tasks;
    }

    public AtomicInteger getWaitingPeriod(){
        return waitingPeriod;
    }
}
