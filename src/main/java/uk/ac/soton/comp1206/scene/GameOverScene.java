package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
    public GameOverScene(GameWindow gameWindow, Object score) {
        super(gameWindow);
    }


    private static final Logger logger = LogManager.getLogger(IntroScene.class);


    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        BorderPane borderPane =new BorderPane();
        borderPane.setMaxWidth(gameWindow.getWidth());
        borderPane.setMaxHeight(gameWindow.getHeight());
        borderPane.getStyleClass().add("menu-background");

        root.getChildren().add(borderPane);

        Text text = new Text("GameOver");
        text.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
        text.setFill(Color.WHITE);

        TextField notification = new TextField("YOUR NAME");
        notification.setMaxWidth(200);

        Button sendScoreBtn = new Button();
        sendScoreBtn.setText("上传分数");


        VBox vBox = new VBox();
        vBox.getChildren().addAll(notification,text,sendScoreBtn);
        vBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);


        sendScoreBtn.setOnAction(this::sendScore);
    }

    /**
     * 将分数发送到记分板
     * @param actionEvent
     */
    private void sendScore(ActionEvent actionEvent) {

    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising intro");
    }
}
