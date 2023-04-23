package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import uk.ac.soton.comp1206.component.GameShowNext;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.util.CountDownTimer;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class MulityPlayerScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    private GamePiece currPiece, storePiece, getPiece;

    private GameShowNext gameShowNext, gameShowStore;

    private CountDownTimer countdownTimer;

    private Integer countTime = 5000;
    private Float upgradeReductionTimeRatio = 0.1f;

    private Text time;

    private Text score;

    private Text life;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MulityPlayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        board.getStyleClass().add("gameBox");
        mainPane.setCenter(board);

        // 候选
        Double candidateWidth = Double.valueOf(gameWindow.getWidth() / 4.0);
        Double candidateHeight = Double.valueOf(gameWindow.getHeight() / 4.0);

        VBox vBox = new VBox();
        Text next = new Text("Next One");
        next.setFont(Font.font("Orbitron", FontWeight.BOLD, 30));
        // 设置字体颜色为白色
        next.setFill(Color.WHITE);
        gameShowNext = new GameShowNext(3, 3, candidateWidth, candidateHeight);
        gameShowNext.setPadding(new Insets(30, 0, 30, 0));
        Text next_2 = new Text("Next Two");
        next_2.setFont(Font.font("Orbitron", FontWeight.BOLD, 30));
        next_2.setFill(Color.WHITE);
        gameShowStore = new GameShowNext(3, 3, candidateWidth, candidateHeight);
        gameShowStore.setPadding(new Insets(30, 0, 0, 0));
        vBox.getChildren().addAll(next, gameShowNext, next_2, gameShowStore);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(0, 20, 0, 0));

        mainPane.setRight(vBox);

//        currPiece = GamePiece.createPiece(0);
        getPieceFromServ();


        HBox btns = new HBox();
        Button rote = new Button("Rote");
        rote.getStyleClass().add("game-button");
        Button storage = new Button("Change");
        storage.getStyleClass().add("game-button");
        btns.getChildren().addAll(rote, storage);
        btns.setSpacing(20);
        btns.setAlignment(Pos.CENTER);

        mainPane.setTop(btns);

        time = new Text();
        time.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
        time.setFill(Color.WHITE);
        mainPane.setBottom(time);

        VBox scoreVbox = new VBox();
        mainPane.setLeft(scoreVbox);


        life = new Text();
        life.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
        life.setFill(Color.WHITE);
        life.setText("LIFE:" + game.getLife());
        btns.getChildren().add(life);


        game.setScoreListener((observable, oldValue, newValue) -> {
            score.setText("SCORE:" + newValue);
        });
        game.setLifeListener((observable, oldValue, newValue) -> {
            life.setText("LIFE:" + newValue);
        });


        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnMouseEntered(this::blockHover);

        rote.setOnMouseClicked(this::roteClicked);
        storage.setOnMouseClicked(this::storageClicked);


        this.gameWindow.getCommunicator().addListener(res -> {
            if (res.startsWith("PIECE ")) {
                logger.info("get piece from serv");
                res = res.replaceFirst("PIECE ", "");
                currPiece = GamePiece.createPiece(Integer.parseInt(res));
                gameShowNext.show(currPiece);
            }
        });

        this.gameWindow.getCommunicator().addListener(res -> {
            if (res.startsWith("SCORES ")) {
                logger.info("get SCORES from serv");
                res = res.replaceFirst("SCORES ", "");
                String finalRes = res;
                Platform.runLater(() -> {
                    scoreVbox.getChildren().clear();
                    String[] scores = finalRes.split("\\n");
                    for (String score : scores) {
                        Text text = new Text();
                        text.setFont(Font.font("Orbitron", FontWeight.BOLD, 20));
                        text.setFill(Color.WHITE);
                        text.setText(score);
                        scoreVbox.getChildren().add(text);
                    }
                });
                gameShowNext.show(currPiece);
            }
        });


        this.game.score.addListener((observable, oldValue, newValue) -> {
            this.gameWindow.getCommunicator().send("SCORE " + newValue);
        });

        Thread t = new Thread(() -> {
            // run方法具体重写
            this.gameWindow.getCommunicator().send("SCORES");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

    }

    // 鼠标悬浮
    private void blockHover(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();


    }

    // 点击存储
    private void storageClicked(MouseEvent mouseEvent) {
        logger.info("storage");
        if (storePiece == null) {
            game.levelUp();
            storePiece = currPiece;
            getPieceFromServ();
        } else {
            GamePiece temp = storePiece;
            storePiece = currPiece;
            currPiece = temp;
        }

        gameShowNext.show(currPiece);
        gameShowStore.show(storePiece);
    }

    // 点击旋转
    private void roteClicked(MouseEvent mouseEvent) {
        logger.info("rotate");
        currPiece.rotate();
        gameShowNext.show(currPiece);
//        gameShowStore.show(storePiece);
    }

    // 键盘点击事件
    private void keyPressed(KeyEvent keyEvent) {
        logger.info("adad");
        switch (keyEvent.getCode()) {
            case R:
                logger.info("rotate");
                currPiece.rotate();
                break;
        }
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        // 如果是第一次点击，开启计时器
        if (countdownTimer == null) {
            resetCountTimer();
        } else {

        }
        // 验证是否能放入当前的piece
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        if (game.canPlace(x, y, currPiece)) {
            // 重置计时器
            resetCountTimer();
            // 如果能放入，就放入
            game.place(x, y, currPiece);
            // 放入后进入下一环节
            game.levelUp();
            // 刷新候选
            getPieceFromServ();
//            currPiece = GamePiece.createPiece(game.getCurrLevel());
//            gameShowNext.show(currPiece);
            logger.info("can");
            // 查看有没有填满的消除
            game.checkLines();
        } else {
            // TODO 警告声音
            logger.info("can not");
        }


        // 如果不能放入，就不放入
        // game.blockClicked(gameBlock);
    }

    private void resetCountTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new CountDownTimer(countTime, new CountDownTimer.CountDownTimerCallback() {
            @Override
            public void onTick(long remainingMillis) {
                time.setText("TIME:" + remainingMillis);
//                System.out.println("Remaining millis: " + remainingMillis);
            }

            @Override
            public void onFinish() {
                // 跳转到结束页面
                System.out.println("Countdown finished!");

                game.reduceLife();
                gameWindow.getCommunicator().send("LIVES " + game.getLife());
                if (game.getLife() == 0) {
                    Platform.runLater(() -> {
                        gameWindow.getCommunicator().send("DIE");
                        gameWindow.loadScene(new GameOverScene(gameWindow, game.score.get()));
                    });
                } else {
                    countTime = 5000;
                    resetCountTimer();
                }

            }
        }, 0.1f);

        countdownTimer.start();
        countTime = (int) (countTime * (1 - upgradeReductionTimeRatio));
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);

    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

    public void getPieceFromServ() {
        this.gameWindow.getCommunicator().send("PIECE");
    }

}
