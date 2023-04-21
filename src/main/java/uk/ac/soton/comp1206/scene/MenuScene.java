package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        VBox menuVBox = new VBox();
        menuVBox.setAlignment(Pos.BOTTOM_CENTER);
        var button = new Button("Play");
        var buttonIntro = new Button("Introduction");
        var buttonScoreList = new Button("ScoreList");
        var buttonExit = new Button("Exit");
        var buttonMutiGame = new Button("MutiGame");
        buttonIntro.setStyle("-fx-background-color: transparent;");
        menuVBox.getChildren().addAll(button, buttonIntro, buttonExit, buttonScoreList, buttonMutiGame);
        mainPane.setCenter(menuVBox);
        buttonIntro.setOnAction(this::startInto);
        buttonScoreList.setOnAction(this::startScoreList);
        buttonMutiGame.setOnAction(this::startMutiGame);

        //Bind the button action to the startGame method in the menu
        button.setOnAction(this::startGame);
    }

    private void startMutiGame(ActionEvent actionEvent) {
        gameWindow.loadScene(new MulityScene(gameWindow));
    }

    private void startScoreList(ActionEvent actionEvent) {
        gameWindow.loadScene(new RankingListScene(gameWindow));
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

    private void startInto(ActionEvent event) {
        gameWindow.loadScene(new IntroScene(gameWindow));
    }

}
