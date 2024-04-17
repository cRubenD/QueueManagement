package Helper;

import javafx.scene.control.TextField;

public class Convert {
    public static int convertTextField(TextField textField) throws Exception {

        String text = textField.getText();
        int number = Integer.parseInt(text);
        return number;
    }

    public static boolean verify(TextField NOClients, TextField NOQueues, TextField SimulationTime,
                              TextField minArrivalTime, TextField maxArrivalTime, TextField minServiceTime,
                              TextField maxServiceTime) throws Exception {

        int clients = Integer.parseInt(NOClients.getText());
        int queues = Integer.parseInt(NOQueues.getText());
        int simulationTime = Integer.parseInt(SimulationTime.getText());
        int minArrival = Integer.parseInt(minArrivalTime.getText());
        int maxArrival = Integer.parseInt(maxArrivalTime.getText());
        int minService = Integer.parseInt(minServiceTime.getText());
        int maxService = Integer.parseInt(maxServiceTime.getText());

        if (clients < 0 || queues < 0 || simulationTime < 0 || minArrival < 0 || maxArrival < 0 || minService < 0 || maxService < 0) {
            return false;
        }

        if (minArrival > maxArrival || minService > maxService) {
            return false;
        }
        return true;
    }
}
