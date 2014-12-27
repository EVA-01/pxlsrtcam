/*
 pxlsrtcam - Use Ruby's pxlsrt gem to pixel sort a webcam
 Copyright (C) 2014  James Anthony Bruno

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package main;

import com.github.sarxos.webcam.Webcam;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Pxlsrtcam {
    private final static String[] directions = {
        "horizontal",
        "vertical",
        "diagonal",
        "r-diagonal",
    };
    private final static String[] methods = {
        "sum-rgb",
        "red",
        "green",
        "blue",
        "yellow",
        "cyan",
        "magenta",
        "hue",
        "saturation",
        "brightness",
        "luma",
        "sum-hsb",
        "none"
    };
    public static boolean tORf() {
        boolean[] tf = {true, false};
        return tf[(int)Math.floor(Math.random() * 2)];
    }
    public static BufferedImage randomPxlsrt(BufferedImage img) {
        String method = methods[(int) Math.floor(Math.random() * methods.length)];
        if(method.equals("none")){
            return img;
        }
        String direction = directions[(int) Math.floor(Math.random() * directions.length)];
        boolean reverse = tORf();
        boolean middlate = tORf();
        boolean bruteMode = tORf();
        if(bruteMode) {
            boolean full = tORf();
            int min;
            int max;
            int length;
            Pxlsrt brute = new Pxlsrt(img);
            switch (direction) {
                case "horizontal":
                    // horizontal
                    length = brute.width();
                    break;
                case "vertical":
                    // vertical
                    length = brute.height();
                default:
                    // diagonal
                    length = Math.min(brute.width(), brute.height());
                    break;
            }
            if(!full) {
                min = (int) Math.floor(Math.random() * length) + 1;
                max = (int) Math.floor(Math.random() * length) + 1;
            } else {
                min = length;
                max = length;
            }
            brute.brute(min, max, direction, method, reverse, (middlate ? Pxlsrt.randomInRange(-10, 10) : 0));
            return brute.modified();
        } else {
            boolean absolute = tORf();
            double threshold = Pxlsrt.randomInRange(20, 100);
            Pxlsrt smart = new Pxlsrt(img);
            smart.smart(threshold, absolute, direction, method, reverse, (middlate ? Pxlsrt.randomInRange(-10, 10) : 0));
            return smart.modified();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting");
        Webcam webcam = Webcam.getDefault();
        Dimension[] sizes = webcam.getViewSizes();
        Dimension max = null;
        double mArea = 0;
        int maxArea = 1000 * 900;
        for(int i = 0; i < sizes.length; i++) {
            double cArea = sizes[i].getHeight() * sizes[i].getWidth();
            if(cArea <= maxArea) {
                if(max == null) {
                    max = sizes[i];
                    mArea = cArea;
                } else {
                    if(cArea >= mArea) {
                        max = sizes[i];
                        mArea = cArea;
                    }
                }
            }
        }
        System.out.println("Dimensions: " + (int)max.getWidth() + "x" + (int)max.getHeight());
        webcam.setViewSize(max);
        webcam.open();
        JLabel jLabel = new JLabel();
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        jLabel.setVerticalAlignment(JLabel.CENTER);
        boolean first = true;
        // Interval in seconds
        double seconds = 0;
        // Max amount of seconds until program quits
        double maxSeconds = 60;
        long start = System.nanoTime();
        while((System.nanoTime()  - start) / 1000000000.0 < maxSeconds) {
        // Or run indefinitely
        //while(true) {
            BufferedImage image = webcam.getImage();
            BufferedImage img = randomPxlsrt(image);
            if(first) {
                JFrame editorFrame = new JFrame("pxlsrt");
                editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                editorFrame.setUndecorated(true);
                ImageIcon imageIcon = new ImageIcon(img);
                jLabel.setIcon(imageIcon);
                editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
                editorFrame.pack();
                editorFrame.setLocationRelativeTo(null);
                editorFrame.setVisible(true);
                first = false;
                start = System.nanoTime();
            } else {
                ImageIcon imageIcon = new ImageIcon(img);
                jLabel.setIcon(imageIcon);
            }
            Thread.sleep((long) (seconds * 1000));
        }
        System.exit(0);
    }
}