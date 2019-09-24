package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class ImagePanel extends JPanel {

    private BufferedImage bufferedImage = null;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public void paintComponent(Graphics g){
        if(bufferedImage!=null){
            g.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(),bufferedImage.getHeight(),this);
        }
    }
}

