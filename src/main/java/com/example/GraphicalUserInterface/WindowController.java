package com.example.GraphicalUserInterface;

import BusinessLogic.Scheduler;
import BusinessLogic.SimulationManager;
import Helper.Exception;
import Helper.Logger;
import dataModels.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WindowController implements Initializable {
    @FXML
    public TextField NOClients;
    @FXML
    public TextField NOQueues;
    @FXML
    public TextField SimulationTime;
    @FXML
    public TextField MinArrivalTime;
    @FXML
    public TextField MaxArrivalTime;
    @FXML
    public TextField MinServiceTime;
    @FXML
    public TextField MaxServiceTime;
    @FXML
    public ImageView closeBtn;
    @FXML
    public Button simulationBtn;
    @FXML
    public TextArea logger;
    @FXML
    public  ProgressBar progress;

    public WindowController(){
        // Default constructor needed for FXMLoader
    }

    public TextArea getLogger() {
        return logger;
    }

    public void init(Stage stage){

        // close button to close the application
        closeBtn.setOnMouseClicked(mouseEvent -> {
            closeBtn.getScene().getWindow();
            stage.close();
        });

        simulationBtn.setOnMouseClicked(mouseEvent -> {
            Logger logger = new Logger(getLogger());
            SimulationManager gen = new SimulationManager();

            try {
                gen.SimulationManagerInit(NOClients, NOQueues, SimulationTime, MinArrivalTime, MaxArrivalTime,
                        MinServiceTime, MaxServiceTime);
            } catch (Exception e) {
                showErrorAlert("Error occurred!", e.getMessage());
            }

            gen.SimulationManagerMethod(logger, progress);
            List<dataModels.Task> tasks = gen.getGeneratedTasks();

            Thread thread = new Thread(gen);
            thread.start();
            startProcess();

            Platform.runLater(() -> {
                try {
                    thread.join();

                    // calculate the avearageWaitingTime
                    double averageWaitingTime = 0;
                    Scheduler scheduler = gen.getScheduler();
                    List<Server> serverList = scheduler.getServers();
                    for (Server server : serverList) {
                        averageWaitingTime += server.getWaitingPeriod().get();
                    }
                    for (dataModels.Task task1 : tasks) {
                        System.out.println(task1.getServiceTime());
                        averageWaitingTime += task1.getServiceTime();
                    }
                    averageWaitingTime /= Double.parseDouble(NOClients.getText());
                    logger.print(averageWaitingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

    }

    private void startProcess(){
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception, InterruptedException {
                // Aici trebuie să fie logica ta de calcul pentru progres
                // De exemplu, dacă ai o buclă sau un proces lung, actualizează periodic progresul
                while (SimulationManager.getCurrentTime().get() <= Double.parseDouble(SimulationTime.getText())) {
                    double progressValue = (double) SimulationManager.getCurrentTime().get() / Double.parseDouble(SimulationTime.getText());
                    System.out.println(progressValue);
                    Platform.runLater(() -> progress.setProgress(progressValue));
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        progress.setProgress(0);
        progress.progressProperty().unbind();
        //progress.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void showErrorAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}