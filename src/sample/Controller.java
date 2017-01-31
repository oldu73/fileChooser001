package sample;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class Controller {

    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    ProgressIndicator progressIndicator;

    private File file;

    public void initialize() {

        System.out.println("Controller initialize..");

        button.setOnAction(event -> doBrowse());

    }

    private void doBrowse() {

        // SRC: http://stackoverflow.com/questions/37393642/creating-a-javafx-dialog-inside-a-javafx-task
        // SRC: http://stackoverflow.com/questions/13838089/file-chooser-dialog-not-closing
        // SRC: http://fabrice-bouye.developpez.com/tutoriels/javafx/gui-service-tache-de-fond-thread-javafx/
        // SRC: http://stackoverflow.com/questions/16978557/wait-until-platform-runlater-is-executed-using-latch
        // SRC: http://tutorials.jenkov.com/java-util-concurrent/countdownlatch.html

        final CountDownLatch latch = new CountDownLatch(1);

        button.setDisable(true);

        final Service<Void> browseService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        Platform.runLater(() -> {
                            progressIndicator.setVisible(true);

                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Open File");

                            File file1 = fileChooser.showOpenDialog(Main.primaryStage);
                            label.setText(file1.getName());

                            latch.countDown();
                        });

                        return null;
                    }
                };
            }
        };

        browseService.start();

        // asynchronous thread waiting for the process (browseService) to finish
        new Thread(() -> {

            // debug mode
            //System.out.println("Await");

            try {
                latch.await();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

            // queuing the done notification into the javafx thread

            Platform.runLater(() -> {

                // debug mode
                //System.out.println("Done");

                button.setDisable(false);
                progressIndicator.setVisible(false);
            });
        }).start();

    }
}
