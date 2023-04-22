package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class RoomScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(RoomScene.class);
    private String roomChannel;

    private TextArea msgArea;
    private String myName;

    private Boolean host;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     * @param host
     */
    public RoomScene(GameWindow gameWindow, String roomChannel, boolean host) {
        super(gameWindow);
        this.roomChannel = roomChannel;
        this.host = host;
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

        VBox playerListVBox = new VBox();
        borderPane.setLeft(playerListVBox);

        BorderPane borderPaneChat = new BorderPane();
        VBox chatVBox = new VBox();
        BorderPane borderPaneMsg = new BorderPane();
        msgArea = new TextArea();
        HBox msgBtns = new HBox();


        Button sendMsg = new Button("send msg");
        sendMsg.setOnAction(this::sendMsg);
        Button leaveBtn = new Button("leave room");
        leaveBtn.setOnAction(this::leaveRoom);
        msgBtns.getChildren().addAll(sendMsg, leaveBtn);

        if (host) {
            Button startBtn = new Button("start game");
            startBtn.setOnAction(this::startGame);
            msgBtns.getChildren().add(startBtn);
        }

        borderPane.setRight(borderPaneChat);
        borderPaneChat.setCenter(chatVBox);
        borderPaneChat.setBottom(borderPaneMsg);
        borderPaneMsg.setCenter(msgArea);
        borderPaneMsg.setBottom(msgBtns);


        gameWindow.getCommunicator().addListener(res -> {
            if (res.startsWith("USERS")) {
                Platform.runLater(() -> playerListVBox.getChildren().clear());
                res = res.replaceFirst("USERS ", "");
                String[] users = res.split("\\n");
                for (int i = 0; i < users.length; i++) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        Text text = new Text(users[finalI]);
                        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                        logger.info(myName + "eq" + users[finalI]);
                        if (users[finalI].equals(myName)) {
                            logger.info("setRed" + users[finalI]);
                            text.setFill(Color.RED);
                        } else
                            text.setFill(Color.WHITE);
                        playerListVBox.getChildren().add(text);
                    });
                }
            }
        });

        gameWindow.getCommunicator().addListener(res -> {
            if (res.startsWith("NICK")) {
                res = res.replaceFirst("NICK ", "");
                logger.info("my name is " + res);
                this.myName = res;
                gameWindow.getCommunicator().send("USERS");
            }
        });
        gameWindow.getCommunicator().send("NICK");

        gameWindow.getCommunicator().addListener(res -> {
            if (res.startsWith("MSG")) {
                res = res.replaceFirst("MSG ", "");
                String finalRes = res;
                Platform.runLater(() -> {
                    Text text = new Text(finalRes);
                    text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    text.setFill(Color.WHITE);
                    chatVBox.getChildren().add(text);
                });
            }
        });

    }

    private void startGame(ActionEvent actionEvent) {
        gameWindow.getCommunicator().send("START");
        gameWindow.loadScene(new MulityPlayerScene(gameWindow));
    }

    private void leaveRoom(ActionEvent actionEvent) {
        gameWindow.getCommunicator().send("PART");
        gameWindow.loadScene(new MulityScene(gameWindow));
    }

    private void sendMsg(ActionEvent actionEvent) {
        String text = msgArea.getText();
        if (text.length() > 0) {
            gameWindow.getCommunicator().send("MSG " + text);
            msgArea.clear();
        }
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising intro");
    }

}
