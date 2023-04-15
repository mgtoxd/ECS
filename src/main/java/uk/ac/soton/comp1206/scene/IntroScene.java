package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class IntroScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(IntroScene.class);

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating intro Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        AnchorPane anchorPane=new AnchorPane();
        anchorPane.setMaxWidth(gameWindow.getWidth());
        anchorPane.setMaxHeight(gameWindow.getHeight());
        anchorPane.getStyleClass().add("menu-background");

        root.getChildren().add(anchorPane);

        Text text = new Text("Introduction");
        text.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
        text.setFill(Color.WHITE);
        AnchorPane.setTopAnchor(text, 20.0);
        anchorPane.getChildren().add(text);
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising intro");
    }

}
