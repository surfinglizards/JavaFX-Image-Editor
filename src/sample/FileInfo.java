package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileInfo {

    private int width;
    private int height;
    private String filename;

    public FileInfo(String filename) throws IOException {
        BufferedImage bimg = ImageIO.read(new File(filename));
        this.width          = bimg.getWidth();
        this.height         = bimg.getHeight();
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }






}
