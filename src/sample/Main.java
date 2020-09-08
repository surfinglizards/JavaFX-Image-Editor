package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Controller(primaryStage);
    }

    private void stageController(MouseEvent event){

    }


    private void saveToFile(Image image) {
        File outputFile = new File("./sample.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image enhanceImage(Image image, int by){

        int xVar = (int) image.getWidth() * by;
        int yVar = (int) image.getHeight() * by;

        WritableImage writableImage = new WritableImage(xVar, yVar);

        PixelReader pixelReader = image.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = pixelReader.getColor(x, y);
                for (int x1 = 0; x1 < by; x1++) {
                    for (int y1 = 0; y1 < by; y1++) {
                        writer.setColor(x * by + x1,y * by + y1,color);

                    }
                }
            }

        }

        return writableImage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
