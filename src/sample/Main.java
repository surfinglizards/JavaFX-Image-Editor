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
    public void start(Stage primaryStage) throws Exception{
        Image image = new Image("file:E:\\Downloads\\f46aef5f98d1d7ba8003cc7b75bdc79e136eaf7e.jpg");
        Image image1 = new Image("file:E:\\Downloads\\fd05e0cf5bef5b0c18e907a7bf53cd88.jpeg");
        Image image2 = new Image("file:E:\\Downloads\\2d392974003a38b99101eb54a826cba46be22392.jpg");
        Image image3 = new Image("file:C:\\Users\\surfi\\IdeaProjects\\Writeable Image Experiment\\images.jpg");
        Image image4 = new Image("file:./fox.jpg");
        File file = new File("E:\\Iphone 8 photos\\New folder\\New folder");
        File file1 = new File("E:\\Downloads");
        File file2 = new File("C:\\Users\\surfi\\Desktop\\Image merge test images");

        Image test1 = new Image("file:E:\\Iphone 8 photos\\New folder\\New folder\\IMG_0004.JPG");
        Image test2 = new Image("file:E:\\Iphone 8 photos\\New folder\\New folder\\IMG_0008.JPG");

        System.out.println(new String("Test").hashCode());
//
//        int counter = 0;
//        int localCounter = 0;
//        ArrayList<Image> list = new ArrayList<Image>();
////        note: this is for random image
//        for (File f: file1.listFiles()) {
//            if (counter == 1) {
//                if (localCounter < 4) {
//                    list.add(new Image("file:" + f.getPath()));
//                    localCounter++;
//                }
//                counter = 0;
//            } else counter++;
//        }
//
//        list.add(image);
//        list.add(image1);
//        list.add(image2);
//
////        for (File f: file.listFiles()) {
////            list.add(new Image("file:" + f.getPath()));
////        }
//
////        Image filtered = reduceImage(image1, 10);
        Image imageMod = enhanceImage(image4, 5);// enhanceImage(image3, 10);
//        for (int i = 0; i < 9; i++) {
//            imageMod = combineImages(imageMod, imageMod);
//        }
//
        ImageView imageView = new ImageView(imageMod);
//        saveToFile(imageMod);
//
////        imageView.setSmooth(true);
imageView.setPreserveRatio(true);
//
//
        primaryStage.setScene(new Scene(new VBox(imageView), 1920, 1080));
        primaryStage.show();
//
//        imageView.setFitWidth(imageMod.getWidth());
//        imageView.setFitHeight(imageMod.getHeight());
//
//        primaryStage.getScene().setOnScroll(event -> {
//            if (event.getDeltaY() == 40){
//                imageView.setFitWidth(imageView.getFitWidth() + 10);
//                imageView.setFitHeight(imageView.getFitWidth() + 10);
//            } else if (event.getDeltaY() == -40){
//                imageView.setFitWidth(imageView.getFitWidth() - 10);
//                imageView.setFitHeight(imageView.getFitWidth() - 10);
//            }
//        });
//
//        primaryStage.getScene().setOnMouseClicked(this::stageController);

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
