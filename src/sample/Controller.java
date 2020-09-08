package sample;

import com.sun.management.OperatingSystemMXBean;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class Controller {

    private Stage stage;
    protected Controller(Stage stage){
        this.stage = stage;
        start();
    }

    private Button applyButton = new Button("Apply");
    private Slider noiseSlider = new Slider();
    private Slider randomSlider = new Slider();
    private Slider reduceSlider = new Slider();
    private Slider enhanceSlider = new Slider();
    private CheckBox noiseCheckBox = new CheckBox("Noise:");
    private CheckBox randomCheckBox = new CheckBox("Random Pixel:");
    private CheckBox reduceCheckBox = new CheckBox("Down Scale:");
    private CheckBox enhanceCheckBox = new CheckBox("Up Scale");

    private Image image = test(new Image("file:E:\\Downloads\\2d392974003a38b99101eb54a826cba46be22392.jpg"));//= new Image("file:./fox.jpg");

    private ImageView imageView = new ImageView(image);

    private Label memoryLabel = new Label();
    private Label processorLabel = new Label();

    private BorderPane root = new BorderPane();
    private Pane centerPane = new Pane();
    private GridPane bottomPane = new GridPane();
    private HBox topPane = new HBox();

    private Scene scene = new Scene(root);

    private MenuBar menuBar = new MenuBar();
    private Menu file = new Menu("File");
    private MenuItem exitMenuItem = new MenuItem("Exit");
    private MenuItem saveMenuItem = new MenuItem("Save");
    private MenuItem combineMenuItem = new MenuItem("Combine");
    private MenuItem importImageButton = new MenuItem("Import...");


    private void start(){
        menuBar.getMenus().add(file);
        menuBar.autosize();

        file.getItems().addAll(importImageButton, combineMenuItem, saveMenuItem, new SeparatorMenuItem(), exitMenuItem);

        saveMenuItem.setOnAction(this::saveButtonControl);

        importImageButton.setOnAction(this::imageButtonControl);

        exitMenuItem.setOnAction(event -> System.exit(0));

        root.setPadding(new Insets(10));
        root.setCenter(centerPane);
        root.setBottom(bottomPane);
        root.setTop(menuBar);
        root.setOnKeyPressed(this::buttonControl);
        root.setOnScroll(this::ScrollWheelControl);

        stage.setScene(scene);
        stage.show();
        stage.setTitle("Image Editor");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        applyButton.setOnAction(this::apply);
        applyButton.toFront();

        centerPane.getChildren().add(imageView);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth( scene.getWidth() / 2 );
        imageView.autosize();
        centerPane.setOnMouseClicked(event -> {
            double initX = event.getX();
            double initY = event.getY();
            centerPane.setOnMouseDragged(event1 -> {
                double x = event1.getX();
                double y = event1.getY();
                if (initX > x){
                    imageView.setX(imageView.getX() - 2);
                } else {
                    imageView.setX(imageView.getX() + 2);
                }
            });
        });

        bottomPane.add(noiseCheckBox, 0, 0);
        bottomPane.add(noiseSlider, 1,0);
        bottomPane.add(randomCheckBox, 2, 0);
        bottomPane.add(randomSlider, 3, 0);
        bottomPane.add(reduceCheckBox, 4, 0);
        bottomPane.add(reduceSlider,5, 0);
        bottomPane.add(applyButton,6,0);
        bottomPane.add(enhanceCheckBox, 0 , 1);
        bottomPane.add(enhanceSlider, 1, 1);
        bottomPane.add(memoryLabel,0, 2);
        bottomPane.add(processorLabel, 1, 2);
        bottomPane.setPadding(new Insets(10));
        bottomPane.setHgap(10);
        bottomPane.setVgap(10);

        noiseSlider.setMax(20);
        noiseSlider.setMin(0.05);
        noiseSlider.setValue(5);
        noiseSlider.setMajorTickUnit(5);
        noiseSlider.setMinorTickCount(5);
        noiseSlider.setBlockIncrement(0.5);
        noiseSlider.setShowTickLabels(true);
        noiseSlider.setShowTickLabels(true);

        randomSlider.setValue(20);
        randomSlider.setMax(100);
        randomSlider.setMin(0);
        randomSlider.setShowTickLabels(true);
        randomSlider.setShowTickMarks(true);

        reduceSlider.setMin(1);
        reduceSlider.setMax(16);
        reduceSlider.setBlockIncrement(1);
        reduceSlider.setShowTickLabels(true);
        reduceSlider.setShowTickMarks(true);
        reduceSlider.setMajorTickUnit(8);

        enhanceSlider.setMax(10);
        enhanceSlider.setMin(1);
        enhanceSlider.setShowTickMarks(true);
        enhanceSlider.setShowTickLabels(true);
//        enhanceSlider.setBlockIncrement(1);
        enhanceSlider.setMajorTickUnit(1);
        enhanceSlider.setOnMousePressed(event -> System.out.println(enhanceSlider.getValue()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

    }

    private final Runtime runtime = Runtime.getRuntime();
    private final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private void update(){
        long totalMemory = operatingSystemMXBean.getTotalPhysicalMemorySize();
        long freeMemory = operatingSystemMXBean.getFreePhysicalMemorySize();
        long usedMemory = totalMemory - freeMemory;
        long percentUsed = totalMemory / freeMemory;
        memoryLabel.setText("Memory:\nTotal: " + bytesToMegabytes(totalMemory)  + "KB Free: " + bytesToMegabytes(freeMemory) +
                "KB Used: " + bytesToMegabytes(usedMemory) + "KB Usage: " + percentUsed + "%");

        String vmCPUsage = String.format ("%.2f", operatingSystemMXBean.getProcessCpuLoad() * 100);
        String systemCPUsage = String.format ("%.2f", operatingSystemMXBean.getSystemCpuLoad() * 100);

        processorLabel.setText("CPU Usage: VM: " + vmCPUsage + "% System: " + systemCPUsage + "%");
    }

    public long bytesToMegabytes(long bytes) {
        final long MEGABYTE = 1024L;
        return bytes / MEGABYTE;
    }


    private void apply(ActionEvent event){
        Image image = this.image;

        if (noiseCheckBox.isSelected())
            image = applyNoise(image, noiseSlider.getValue());
        if (randomCheckBox.isSelected())
            image = randomizeImage(image,(int) randomSlider.getValue());
        if (reduceCheckBox.isSelected())
            image = reduceImage(image,(int) reduceSlider.getValue());
        if (enhanceCheckBox.isSelected()) {
            Image image1 = enhanceImage(image, (int) enhanceSlider.getValue());
            image = image1;
        }
        imageView.setImage(image);
    }


    private ArrayList<Image> images = new ArrayList<>();
    private void combineImageButtonControl(ActionEvent event){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg");
        chooser.setSelectedExtensionFilter(fileExtensions);
        chooser.setTitle("Enter an image, Cancel to stop");
         File file = chooser.showOpenDialog(stage);
         if (file != null) {
             images.add(new Image("file:" + file.getPath()));
             combineImageButtonControl(event);
         } else {
             image = combineImages(images);
             imageView.setImage(image);
             images.removeIf(image1 -> true);
             System.out.println(images.size());
         }
    }

    private void imageButtonControl(ActionEvent event){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg");

        chooser.getExtensionFilters().add(fileExtensions);
        File file = chooser.showOpenDialog(stage);
        this.image = new Image("file:" + file.getPath());
        imageView.setImage(image);
    }

    private void saveButtonControl(ActionEvent event){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg");
        chooser.setSelectedExtensionFilter(fileExtensions);
        File file = chooser.showSaveDialog(stage);
        saveToFile(image, file);
    }

    private void saveToFile(Image image, File outputFile) {
        if (image != null && outputFile != null) {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(bImage, "png", outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private double scrollSpeed = 50;

    private void ScrollWheelControl(ScrollEvent event){
        if (event.getDeltaY() == 40){
                imageView.setFitWidth(imageView.getFitWidth() + scrollSpeed);
                imageView.setFitHeight(imageView.getFitWidth() + scrollSpeed);
            } else if (event.getDeltaY() == -40){
                if (!(imageView.getFitWidth() < 100) || !(imageView.getFitHeight() < 100)) {
                    imageView.setFitWidth(imageView.getFitWidth() - scrollSpeed);
                    imageView.setFitHeight(imageView.getFitWidth() - scrollSpeed);
                }
                
            }
    }


    private void buttonControl(KeyEvent event){
        switch (event.getCode()){
            case W:
                imageView.setY(imageView.getY() + 10);
                break;
            case A:
                imageView.setX(imageView.getX() + 10);
                break;
            case S:
                imageView.setY(imageView.getY() - 10);
                break;
            case D:
                imageView.setX(imageView.getX() - 10);
                break;
            case F:
                if (event.isControlDown())
                stage.setFullScreen(!stage.isFullScreen());
                break;
            case Q:
                if (event.isControlDown()){
                    System.exit(0);
                }
                break;
            default:
                event.consume();
                break;
        }

    }




    private Image applyNoise(Image source, double noiseValue){
        PixelReader pixelReader = source.getPixelReader();

        if (noiseValue > 20)
            noiseValue = 20;
        else if (noiseValue < 0.05)
            noiseValue = 0.05;

        int w = (int) source.getWidth();
        int h = (int) source.getHeight();

        WritableImage image = new WritableImage(w, h);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color color = pixelReader.getColor(x, y);

                double noise = Math.random() / noiseValue;

                Color newColor = new Color(Math.min(color.getRed() + noise, 1), Math.min(color.getGreen() + noise, 1),
                        Math.min(color.getGreen() + noise, 1) , 1);

                pixelWriter.setColor(x,y, newColor);
            }

        }
        return image;
    }

    private int counter = 0;


    private Image combineImages(Image...images){
        int maxX = 0;
        int maxY = 0;

        ArrayList<PixelReader> readers = new ArrayList<>();

        for (Image value : images) {
            if (value.getWidth() > maxX) {
                maxX = (int) value.getWidth();
            }
            if (value.getHeight() > maxY) {
                maxY = (int) value.getHeight();
            }
            readers.add(value.getPixelReader());
        }

        WritableImage image = new WritableImage(maxX, maxY);
        PixelWriter pixelWriter = image.getPixelWriter();


        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                if (counter == images.length - 1){
                    if (!(x >= images[counter].getWidth()) && !(y >= images[counter].getHeight())){
                        Color color = readers.get(counter).getColor(x, y);
                        pixelWriter.setColor(x, y, color);
                    }
                    counter = 0;
                } else {
                    if (!(x >= images[counter].getWidth()) && !(y >= images[counter].getHeight())){
                        Color color = readers.get(counter).getColor(x, y);
                        pixelWriter.setColor(x, y, color);
                    }
                    counter++;
                }

            }

        }

        counter = 0;
        return image;
    }

    private Image combineImages(ArrayList<Image> images){
        int maxX = 0;
        int maxY = 0;

        ArrayList<PixelReader> readers = new ArrayList<>();

        for (Image value : images.subList(0, images.size())) {
            if (value.getWidth() > maxX) {
                maxX = (int) value.getWidth();
            }
            if (value.getHeight() > maxY) {
                maxY = (int) value.getHeight();
            }
            readers.add(value.getPixelReader());
        }

        WritableImage image = new WritableImage(maxX, maxY);
        PixelWriter pixelWriter = image.getPixelWriter();


        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                if (counter == images.size() - 1){
                    if (!(x >= images.get(counter).getWidth()) && !(y >= images.get(counter).getHeight())){
                        Color color = readers.get(counter).getColor(x, y);
                        pixelWriter.setColor(x, y, color);
                    }
                    counter = 0;
                } else {
                    if (!(x >= images.get(counter).getWidth()) && !(y >= images.get(counter).getHeight())){
                        Color color = readers.get(counter).getColor(x, y);
                        pixelWriter.setColor(x, y, color);
                    }
                    counter++;
                }

            }

        }

        counter = 0;
        return image;
    }

    private Image reduceImage(Image image, int by){

        int xVar = (int) image.getWidth();
        int yVar = (int) image.getHeight();

        WritableImage writableImage = new WritableImage(xVar, yVar);

        PixelReader pixelReader = image.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < xVar; x += by) {
            for (int y = 0; y < yVar ; y += by) {
                Color color = pixelReader.getColor(x,y);
                writer.setColor(x / by,y / by, color);
            }

        }

        return writableImage;
    }

    private Image enhanceImage(Image image, int by){

        int xVar = (int) image.getWidth() * by;
        int yVar = (int) image.getHeight() * by;

        WritableImage writableImage = null;
        try {
            writableImage = new WritableImage(xVar, yVar);
        } catch (OutOfMemoryError e){
            e.printStackTrace();
            Dialog dialog = new Alert(Alert.AlertType.ERROR, "Out of memory! This image could not be up-scaled by: " + (int) enhanceSlider.getValue());
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Dialog dialog = new Alert(Alert.AlertType.ERROR, e.getMessage());
            dialog.showAndWait();
        }
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

    public Image randomizeImage(Image source, int randomPercent){

        if (randomPercent > 100)
            randomPercent = 100;

        int w = (int) source.getWidth();
        int h = (int) source.getHeight();

        WritableImage writableImage = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (randomPercent >= Math.random() * 100) {
                    Color c = reader.getColor((int) (Math.random() * w), (int) (Math.random() * h));
                    writer.setColor(x, y, c);
                } else {
                    Color c = reader.getColor(x,y);
                    writer.setColor(x, y, c);
                }
            }

        }

        return writableImage;
    }

    private Image rescale(Image image, int newW, int newH){

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();

        WritableImage writableImage = new WritableImage(newW - 1, newH - 1 );
        PixelWriter writer = writableImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        if ( w > newW && h > newH ){
            double tempVar = (double) newW / w;
            for (int x = 0; x < h; x++) {
                for (int y = 0; y < w; y++) {
                    Color color = null;
                    if (x != 0 && y != 0)
                        color = reader.getColor((int) (x / tempVar), (int) (y / tempVar));
                    else
                        color = reader.getColor(x, y);
                    for (int x1 = 0; x1 < newW; x1++) {
                        for (int y1 = 0; y1 < newH; y1++) {
                            writer.setColor(x1, y1, color);
                        }
                    }
                    writer.setColor(x,y,color);

                }
            }
        }

        return writableImage;
    }

    private Image test(Image image2) {

        Image image = new Image("file:E:\\Downloads\\fd05e0cf5bef5b0c18e907a7bf53cd88.jpeg");

        Image result = image2;

        System.out.println("Width: " + result.getWidth() + " Height: " + result.getHeight());
//        while (image.getHeight() / 4 < result.getHeight() && image.getWidth() / 4 < result.getWidth()) {
//            result = reduceImage(result, 2);
//            System.out.println("Width: " + result.getWidth() + " Height: " + result.getHeight());
//        }
        System.out.println("Width: " + result.getWidth() + " Height: " + result.getHeight());

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();

        WritableImage writableImage = new WritableImage(w, h);


        PixelReader reader = writableImage.getPixelReader();
        PixelReader readerAlt = image2.getPixelReader();

        PixelWriter writer = writableImage.getPixelWriter();


//        for (int x = 0; x < result.getWidth(); x++) {
//            for (int y = 0; y < result.getHeight(); y++) {
//                int x1 = x + 1;
//                int y1 = y + 1;
//
//                Color color = reader.getColor(x, y);
//
//                writer.setColor(x1, y1, color);
//
//            }
//        }


        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color color;
                if (x > w / 4 && y > h / 4) {
                    color = readerAlt.getColor(x, y);
                } else {
                    color = reader.getColor(x, y);
                }

                writer.setColor(x,y, color);
            }
        }
        return writableImage;
    }

}
