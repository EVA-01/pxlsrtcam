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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        "-m hue -s",
        "-m saturation",
        "-m brightness",
        "-m random",
        "-m luma",
        "-m sum-hsb",
        "-m sum-hsb -s",
        "-m uniqueness",
        "-m alpha"
    };
    private final static boolean[] tORf = {true, false};
    public static void randomPxlsrt(Runtime rt, String input, String output, Dimension d) throws IOException, InterruptedException {
        String direction = directions[(int)Math.floor(Math.random() * directions.length)];
        String method = methods[(int)Math.floor(Math.random() * methods.length)];
        boolean reverse = tORf[(int)Math.floor(Math.random() * tORf.length)];
        boolean full = tORf[(int)Math.floor(Math.random() * tORf.length)];
        int min = 0;
        int max = 0;
        if(!full) {
            int length;
            switch(direction) {
                case "":
                    // horizontal
                    length = (int)d.getWidth();
                break;
                case "-v":
                    // vertical
                    length = (int)d.getHeight();
                default:
                    // diagonal
                    length = Math.min((int)d.getWidth(), (int)d.getHeight());
                break;
            }
            min = (int)Math.floor(Math.random() * length) + 1;
            max = (int)Math.floor(Math.random() * length) + 1;
        }
        String command = "pxlsrt brute " + input + " " + output + (full ? "" : (" --min " + min + " --max " + max)) + ("".equals(direction) ? "" : " ") + direction + ("".equals(method) ? "" : " ") + method + (reverse ? " -r" : "");
        Process pr = rt.exec(command);
        pr.waitFor();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    Files.deleteIfExists(Paths.get("input.png"));
                } catch (IOException ex) {
                    Logger.getLogger(Pxlsrt.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Files.deleteIfExists(Paths.get("output.png"));
                } catch (IOException ex) {
                    Logger.getLogger(Pxlsrt.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
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
        Runtime rt = Runtime.getRuntime();
        webcam.open();
        JLabel jLabel = new JLabel();
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        jLabel.setVerticalAlignment(JLabel.CENTER);
        boolean first = true;
        // Interval in seconds
        double seconds = 0;
        // Max amount of seconds until program quits
        double maxSeconds = 60;
        System.out.println(System.nanoTime()/ 1000000000.0);
        long start = System.nanoTime();
        while((System.nanoTime()  - start) / 1000000000.0 < maxSeconds) {
        // Or run indefinitely
        //while(true) {
            BufferedImage image = webcam.getImage();
            try {
                ImageIO.write(image, "PNG", new File("input.png"));
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            randomPxlsrt(rt, "input.png", "output.png", max);
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File("output.png"));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
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
