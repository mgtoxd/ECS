package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class MulityScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MulityScene.class);

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MulityScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating intro Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());


        BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(gameWindow.getWidth());
        borderPane.setMaxHeight(gameWindow.getHeight());
        borderPane.getStyleClass().add("menu-background");
        root.getChildren().add(borderPane);

        VBox vbox = new VBox();
        borderPane.setTop(vbox);

        HBox btnHbox = new HBox();
        TextArea roomName = new TextArea();
        roomName.setMaxWidth(200);
        roomName.setMaxHeight(20);
        Button btn = new Button("Create Room");
        btn.getStyleClass().add("game-button");
        AtomicBoolean host = new AtomicBoolean(false);


        btn.setOnAction(event -> {
            host.set(true);
            gameWindow.getCommunicator().send("CREATE " + roomName.getText());
        });
        btnHbox.getChildren().addAll(roomName, btn);
        // btnHbox 底部居中
        btnHbox.setSpacing(20);
        btnHbox.setAlignment(javafx.geometry.Pos.CENTER);
        borderPane.setBottom(btnHbox);


        gameWindow.getCommunicator().send("LIST");

        AtomicInteger atomicInteger = new AtomicInteger();
        gameWindow.getCommunicator().addListener(communication -> {
            if (communication.startsWith("CHANNELS")) {
                logger.info("Received channels");
                logger.info("Channels: " + communication);

                ArrayList<String> highScores = new ArrayList<>();

// 将数据按行拆分
                String[] lines = communication.split("\\n");

// 遍历每一行数据
                for (int i = 0; i < lines.length; i++) {
                    // 如果是第一行数据，删除 "HISCORES " 字符串
                    if (i == 0 && lines[i].startsWith("CHANNELS ")) {
                        lines[i] = lines[i].replaceFirst("CHANNELS ", "");
                    }

                    // 将用户名和分数存储到 Map 中
                    highScores.add(lines[i]);
                }
                highScores.forEach(e -> {
                    Platform.runLater(() -> {
                        addText(vbox, atomicInteger, e);
                    });
                });
            }
        });


        gameWindow.getCommunicator().addListener(communication -> {
            if (communication.startsWith("JOIN")) {
                String join = communication.replaceFirst("JOIN", "");
                Platform.runLater(() -> {
                    gameWindow.loadScene(new RoomScene(gameWindow, join, host.get()));
                });
            }
        });


    }

    public void addText(VBox vBox, AtomicInteger count, String text) {
        Text text1 = new Text(text);
        text1.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        text1.setFill(Color.WHITE);
        text1.setWrappingWidth(200);
        Button button1 = new Button("Join " + text);
        button1.getStyleClass().add("game-button");
        button1.setOnAction(event -> {
            this.joinRoom(text1.getText());
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(text1, button1);
        hbox.setSpacing(20);
        // 内容垂直居中
        hbox.setAlignment(javafx.geometry.Pos.CENTER);
        vBox.getChildren().add(hbox);
        // vBox 位于顶部居中的位置，vBox 距离顶部 30
        vBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        vBox.getStyleClass().add("gameOver-notification");
    }

    private void joinRoom(String text) {
        gameWindow.getCommunicator().send("JOIN " + text);
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising intro");
    }

}
