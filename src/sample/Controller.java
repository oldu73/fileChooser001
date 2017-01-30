package sample;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    @FXML
    private Button button;

    @FXML
    private Label label;

    private File file;

    public void initialize() {

        System.out.println("Controller initialize..");

        button.setOnAction(event -> doBrowse2());

    }

    private void doBrowse1() {

        // SRC: http://stackoverflow.com/questions/37393642/creating-a-javafx-dialog-inside-a-javafx-task
        Task<Void> browse = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Open File");

                        File file = fileChooser.showOpenDialog(Main.primaryStage);

                        label.setText(file.getName());

                    }
                });

                return null;
            }
        };

        // SRC: http://stackoverflow.com/questions/13838089/file-chooser-dialog-not-closing
        Thread th = new Thread(browse);
        th.setDaemon(true);
        th.start();

    }

    private void doBrowse2() {

        // SRC: http://fabrice-bouye.developpez.com/tutoriels/javafx/gui-service-tache-de-fond-thread-javafx/

        Scene scene = Main.scene;

        final Cursor oldCursor = scene.getCursor();
        scene.setCursor(Cursor.WAIT);

        System.out.println("1");

        //calculateItem.setDisable(true);
        //calculateButton.setDisable(true);

        final Service<Void> browseService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {

                                System.out.println("2");

                                FileChooser fileChooser = new FileChooser();
                                fileChooser.setTitle("Open File");

                                File file = null;

                                while (file == null) {
                                    file = fileChooser.showOpenDialog(Main.primaryStage);
                                }

                                final int maxIterations = 1000000;
                                for (int iterations = 0; iterations < maxIterations; iterations ++) {
                                    System.out.println(iterations);
                                }

                                label.setText(file.getName());

                                System.out.println("3");

                            }
                        });

                        /*
                        final int maxIterations = 1000000;
                        for (int iterations = 0; iterations < maxIterations; iterations ++) {
                            System.out.println(iterations);
                        }
                        */

                        return null;
                    }
                };
            }
        };

        browseService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    scene.setCursor(oldCursor);

                    System.out.println("4");

                    //calculateItem.setDisable(false);
                    //calculateButton.setDisable(false);

                    break;
            }
        });

        browseService.start();

    }

}
