package uk.ac.soton.comp1206.util;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CreateUtil {

    public static Button createRoundButton(double radius, Color color) {
        // 创建一个圆形按钮
        Button btn = new Button();
        Circle circle = new Circle(radius);
        btn.setGraphic(circle);
        circle.getStyleClass().add("testBtn");
        btn.getStyleClass().add("testBtn");
        return btn;
    }
}