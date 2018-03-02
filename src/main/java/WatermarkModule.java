package com.company;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/*  Testing arguments are:
    C:/Users/Orestis/Desktop/Screenshot_11.jpg C:/Users/Orestis/Desktop/Result.png 200 180
 */

public class WatermarkModule {
    // arguments: ***
    // source path, destination path, desiredWidth, desiredHeight
    public static void main(String[] args) {

//        /*      // input through arguments
        String origPath = args[0];
        String resPath = args[1];
        int desWidth = Integer.parseInt(args[2], 10);
        int desHeight = Integer.parseInt(args[3], 10);
//        */

        /*      // hard-coded input
        String origPath = "C:/Users/Orestis/Desktop/Screenshot_11.jpg";
        String resPath = "C:/Users/Orestis/Desktop/Result.png";
        int desWidth = 200;
        int desHeight = 180;
        */

        File origFile = new File(origPath);
        ImageIcon icon = new ImageIcon(origFile.getPath());

        System.out.println("original with is: " + icon.getIconWidth());
        System.out.println("original height is: " + icon.getIconHeight() + "\n");
        System.out.println("desired width is: " + desWidth);
        System.out.println("desired height is: " + desHeight + "\n");

        // create BufferedImage object of original image
        BufferedImage originalImage = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        // create graphics object and add original image to it
        Graphics graphics = originalImage.getGraphics();
        graphics.drawImage(icon.getImage(), 0, 0, null);


        // calculate new width/height to fit resizing requirements
        Double factor = ((double) desWidth / icon.getIconWidth());
        boolean fitToWidth = true;
        if (((double) desHeight/icon.getIconHeight()) > factor) {factor = ((double) desHeight / icon.getIconHeight()); fitToWidth = false;}
        System.out.println("fitToWidth is: " + fitToWidth + "\n");
        System.out.println("factor is: " + factor);
        int newWidth = new Double(icon.getIconWidth() * factor).intValue();
        int newHeight = new Double(icon.getIconHeight() * factor).intValue();
        System.out.println("new width is: " + newWidth);
        System.out.println("new height is: " + newHeight + "\n");

        // resize image
        BufferedImage bufferedImage;
        bufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, icon.getIconWidth(),
                icon.getIconHeight(), null);
        g.dispose();


        // calculate cropping coordinates
        int startX, startY;
        if (fitToWidth) {
            startX = 0;
            startY = (newHeight-desHeight)/2;
        }else{
            startY = 0;
            startX = (newWidth-desWidth)/2;
        }
        System.out.println("startX: " + startX);
        System.out.println("startY: " + startY + "\n");

        // crop unwanted parts
        BufferedImage img = bufferedImage.getSubimage(startX, startY, desWidth, desHeight); //fill in the corners of the desired crop location here
        BufferedImage croppedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics gr = croppedImage.createGraphics();
        gr.drawImage(img, 0, 0, null);




        // set the watermark
        String watermark = "kidspiration-ath \u00a9";             // watermark text ***
        Graphics2D gWatermark = (Graphics2D) croppedImage.getGraphics();
        gWatermark.drawImage(croppedImage, 0, 0, null);     // unnecessary?? ***

        // configurations
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);   // alpha: transparency percentage ***
        gWatermark.setComposite(alphaChannel);
        gWatermark.setColor(Color.WHITE);               // choice of colour ***
        gWatermark.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gWatermark.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));       // choice of font ***
        FontMetrics fontMetrics = gWatermark.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(watermark, gWatermark);      // rectangle of text's boundaries


        // Move our origin to the center
        gWatermark.translate(desWidth / 2f, desHeight / 2f);

        // rotation based on the diagonal angle of the image
        AffineTransform at = new AffineTransform();
        double opad = (double) desHeight / (double) desWidth;
        double angle = Math.toDegrees(Math.atan(opad));     // angle of choice, x(-1) for inversion ***
        double theta = (2 * Math.PI * angle) / 360;
        at.rotate(theta);
        gWatermark.transform(at);

        // center diagonal logo in image
        float x1 = (int)rect.getWidth() / 2f *(-1);
        float y1 = (int)rect.getHeight() / 2f;
        gWatermark.translate(x1, y1);

        //draw the string
        gWatermark.drawString(watermark, 0.0f, 0.0f);


        // create new image file
        File newFile = new File(resPath);
        try {
            ImageIO.write(croppedImage, "png", newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gWatermark.dispose();

        System.out.println(resPath + " created successfully!");
    }
}