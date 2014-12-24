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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Pxlsrt {
    private final static String[] directions = {
        "",
        "-d",
        "-d -v",
        "-v",
    };
    private final static String[] methods = {
        "",
        "-m red",
        "-m green",
        "-m blue",
        "-m yellow",
        "-m cyan",
        "-m magenta",
        "-m hue",
        "-m saturation",
        "-m brightness",
        "-m random",
        "-m luma",
        "-m sum-hsb"
    };
    private final static boolean[] reverse = {true, false};
    public static void randomPxlsrt(Runtime rt, String input, String output) throws IOException, InterruptedException {
        String direction = directions[(int)Math.floor(Math.random() * directions.length)];
        String method = methods[(int)Math.floor(Math.random() * methods.length)];
        boolean doReverse = reverse[(int)Math.floor(Math.random() * reverse.length)];
        String command = "pxlsrt brute " + input + " " + output + ("".equals(direction) ? "" : " ") + direction + ("".equals(method) ? "" : " ") + method + (doReverse ? " -r" : "");
        Process pr = rt.exec(command);
        pr.waitFor();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting");
        Webcam webcam = Webcam.getDefault();
        Dimension[] sizes = webcam.getViewSizes();
        Dimension max = sizes[0];
        for(int i = 1; i < sizes.length; i++) {
            if(sizes[i].getHeight() * sizes[i].getWidth() >= max.getHeight() * max.getWidth()) {
                max = sizes[i];
            }
        }
        System.out.println("Dimensions: " + (int)max.getWidth() + "x" + (int)max.getHeight());
        webcam.setViewSize(max);
        Runtime rt = Runtime.getRuntime();
        webcam.open();
        JLabel jLabel = new JLabel();
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        jLabel.setVerticalAlignment(JLabel.CENTER);
        boolean first = true;
        int runs = 0;
        // Interval in seconds
        double seconds = 2;
        // Max amount of seconds until program quits
        double maxSeconds = 60;
        while(runs < maxSeconds/seconds) {
        // Or run indefinitely
        // while(true) {
            BufferedImage image = webcam.getImage();
            try {
                ImageIO.write(image, "PNG", new File("input.png"));
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            randomPxlsrt(rt, "input.png", "output.png");
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File("output.png"));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
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
            } else {
                ImageIcon imageIcon = new ImageIcon(img);
                jLabel.setIcon(imageIcon);
            }
            runs++;
            Thread.sleep((long) (seconds * 1000));
        }
    }
}
