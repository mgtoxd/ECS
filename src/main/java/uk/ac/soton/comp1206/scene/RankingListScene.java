package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class RankingListScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(RankingListScene.class);

    /**
     * Create a new menu scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public RankingListScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Score Scene");
    }

    private static void addText(AnchorPane anchorPane, AtomicInteger count, String text) {
        count.getAndIncrement();
        // 创建新的 Text 对象
        Text newText = new Text(text);
        anchorPane.getChildren().add(newText);
        newText.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
        newText.setFill(Color.WHITE);

        // 计算新 Text 对象的垂直位置，并水平居中
        double textHeight = newText.getLayoutBounds().getHeight();
        double topOffset = 100.0 + (count.get() - 1) * (textHeight + 10.0);
        double leftOffset = (anchorPane.getWidth() - newText.getLayoutBounds().getWidth()) / 2.0;

        // 设置新 Text 对象的位置
        AnchorPane.setTopAnchor(newText, topOffset);
        AnchorPane.setLeftAnchor(newText, leftOffset);

    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMaxWidth(gameWindow.getWidth());
        anchorPane.setMaxHeight(gameWindow.getHeight());
        anchorPane.getStyleClass().add("menu-background");

        root.getChildren().add(anchorPane);
        AtomicInteger count = new AtomicInteger();
        this.gameWindow.getCommunicator().addListener(communication -> {
            if (communication.startsWith("HISCORES")) {
                logger.info("HISCORES received");

                // 创建一个 Map 对象来存储高分记录
                Map<String, Integer> highScores = new HashMap<>();

                // 将数据按行拆分
                String[] lines = communication.split("\\n");

                // 遍历每一行数据
                for (int i = 0; i < lines.length; i++) {
                    // 如果是第一行数据，删除 "HISCORES " 字符串
                    if (i == 0 && lines[i].startsWith("HISCORES ")) {
                        lines[i] = lines[i].substring(9);
                    }

                    // 将每一行数据按冒号拆分，得到用户名和分数
                    String[] parts = lines[i].split(":");
                    String username = parts[0];
                    int score = Integer.parseInt(parts[1]);

                    // 将用户名和分数存储到 Map 中
                    highScores.put(username, score);
                }

                // 打印高分记录
                for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                    logger.info(entry.getKey() + ": " + entry.getValue());

                    Platform.runLater(() -> {
                        // 依次添加到屏幕中央
                        addText(anchorPane, count, entry.getKey() + ": " + entry.getValue());
                    });
                }
            }
        });

        this.gameWindow.getCommunicator().send("HISCORES");
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

}
