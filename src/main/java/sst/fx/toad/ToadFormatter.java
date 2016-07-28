package sst.fx.toad;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ToadFormatter extends Application {

    private static final double SCENE_WIDTH = 1536.0;
    private static final double SCENE_HEIGHT = 900.0;
    private static final double SCENE_WIDTH_RATIO = 0.9275;
    private static final double SCENE_HEIGHT_RATIO = 0.40;
    private static final double CONTROLS_WIDTH = SCENE_WIDTH * SCENE_WIDTH_RATIO;
    private static final double CONTROLS_HEIGHT = SCENE_HEIGHT * SCENE_HEIGHT_RATIO;

    private TextArea inputText = new TextArea();
    private TextArea outputText = new TextArea();
    private ProgressBar progressBar = new ProgressBar();
    private Button processButton = null;
    private Button cancelButton = null;
    private GridPane grid = new GridPane();
    private Text rows = new Text("0");
    private Text columns = new Text("0");
    private Scene scene = null;
    private ToadFormatterThread tfThread = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Toad FX Formatter");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        createGridPane();

        scene = new Scene(grid, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(ToadFormatter.class.getResource("tf.css").toExternalForm());

        primaryStage.setScene(scene);

        addInputText();
        addRowsLines();
        addButtons();
        addOutputText();
        addProgressBar();
        primaryStage.show();
    }

    private void createGridPane() {
        ColumnConstraints colInfo = new ColumnConstraints();
        colInfo.setPercentWidth(25);

        RowConstraints rowInfo40 = new RowConstraints();
        rowInfo40.setPercentHeight(44);
        RowConstraints rowInfo10 = new RowConstraints();
        rowInfo10.setPercentHeight(4);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(false);

        grid.getColumnConstraints().add(colInfo);
        grid.getColumnConstraints().add(colInfo);
        grid.getColumnConstraints().add(colInfo);
        grid.getColumnConstraints().add(colInfo);

        grid.getRowConstraints().add(rowInfo40);
        grid.getRowConstraints().add(rowInfo10);
        grid.getRowConstraints().add(rowInfo10);
        grid.getRowConstraints().add(rowInfo40);
        grid.getRowConstraints().add(rowInfo10);
    }

    private void addInputText() {
        System.out.println("inputText.setPrefWidth() = " + inputText.getPrefWidth());
        System.out.println("inputText.getPrefHeight() = " + inputText.getPrefHeight());
        inputText.setEditable(false);
        inputText.setPrefWidth(CONTROLS_WIDTH);
        inputText.setPrefHeight(CONTROLS_HEIGHT);
        // progressBar.setPrefWidth(450);

        grid.add(inputText, 0, 0, 4, 1);
    }

    private void addButtons() {
        processButton = new Button("Process");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(processButton);
        processButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                grid.setCursor(Cursor.WAIT);
                tfThread = new ToadFormatterThread(inputText, outputText, progressBar, processButton, cancelButton, rows, columns);
                tfThread.start();
                grid.setCursor(Cursor.DEFAULT);
            }
        });
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().add(hbBtn);
        grid.add(sp, 1, 2);

        cancelButton = new Button("Cancel");
        hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(cancelButton);
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (null != tfThread) {
                    tfThread.stop();
                    tfThread = null;
                }
                grid.setCursor(Cursor.DEFAULT);
                cancelButton.setDisable(true);
                processButton.setDisable(false);
            }
        });
        cancelButton.setDisable(true);
        grid.add(hbBtn, 2, 2);

        Button exitButton = new Button("Exit");
        hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(exitButton);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setDisable(false);
        grid.add(hbBtn, 3, 2);
    }

    private void addOutputText() {
        outputText.setEditable(false);
        outputText.setPrefWidth(CONTROLS_WIDTH);
        outputText.setPrefHeight(CONTROLS_HEIGHT);
        // outputText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        grid.add(outputText, 0, 3, 4, 1);
    }

    private void addProgressBar() {
        progressBar.setPrefWidth(CONTROLS_WIDTH);
        // progressBar.setLayoutX(215);
        progressBar.setLayoutX(CONTROLS_WIDTH);
        progressBar.setLayoutY(45);
        progressBar.setProgress(0);

        grid.add(progressBar, 0, 4, 4, 1);
    }

    private void addRowsLines() {
        GridPane rlGrid = new GridPane();
        rlGrid.setAlignment(Pos.CENTER);
        rlGrid.setHgap(10);
        rlGrid.setVgap(10);
        rlGrid.setPadding(new Insets(25, 25, 25, 25));
        rlGrid.setPrefWidth(CONTROLS_WIDTH);
        rlGrid.setPrefHeight(45);

        rlGrid.add(new Label("Rows : "), 0, 0);
        rlGrid.add(rows, 1, 0);
        // rows.setMaxSize(140, 40);
        // rows.setAlignment(Pos.CENTER);
        // rows.setDisable(true);

        rlGrid.add(new Label("Columns : "), 2, 0);
        rlGrid.add(columns, 3, 0);
        // columns.setMaxSize(140, 40);
        // columns.setAlignment(Pos.CENTER);
        // columns.setDisable(true);

        grid.add(rlGrid, 0, 1, 4, 1);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
