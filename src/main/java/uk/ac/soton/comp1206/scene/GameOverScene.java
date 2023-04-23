package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class GameOverScene extends BaseScene {
    private Integer score;

    private TextField notification;

    private static final Logger logger = LogManager.getLogger(IntroScene.class);

    public GameOverScene(GameWindow gameWindow, Integer score) {
        super(gameWindow);
        this.score = score;
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

        Text text = new Text("GameOver");
        text.setFont(Font.font("Orbitron", FontWeight.BOLD, 50));
        text.setFill(Color.WHITE);

        Text scoreText = new Text("Your score:" + score);
        scoreText.setFont(Font.font("Orbitron", FontWeight.BOLD, 30));
        scoreText.setFill(Color.WHITE);

        notification = new TextField("YOUR NAME");
        notification.setMaxWidth(200);
        notification.getStyleClass().add("gameOver-notification");

        Button sendScoreBtn = new Button();
        sendScoreBtn.getStyleClass().add("gameOver-button");
        sendScoreBtn.setText("Upload score");

        Button returnMenu = new Button("返回主菜单");
        returnMenu.setOnAction(actionEvent -> gameWindow.loadScene(new MenuScene(gameWindow)));


        VBox vBox = new VBox();
        vBox.getChildren().addAll(text, scoreText, notification, sendScoreBtn, returnMenu);
        // vBox 位置：水平居中、距离顶部 20%
        vBox.setPadding(new Insets(gameWindow.getHeight() * 0.30, 0, 0, 0));
        vBox.setAlignment(Pos.TOP_CENTER);

        borderPane.setCenter(vBox);


        sendScoreBtn.setOnAction(this::sendScore);
    }

    /**
     * 将分数发送到记分板
     * @param actionEvent
     */
    private void sendScore(ActionEvent actionEvent) {
        this.gameWindow.getCommunicator().send("HISCORE " + notification.getText() + ":" + score);
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising intro");
    }
}
