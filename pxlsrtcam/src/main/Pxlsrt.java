package main;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class Pxlsrt {
    private BufferedImage original;
    private BufferedImage modified;
    public Pxlsrt(String path) throws IOException {
        original = ImageIO.read(new File(path));
        modified = deepCopy(original);
    }
    public Pxlsrt(BufferedImage img) {
        original = deepCopy(img);
        modified = deepCopy(img);
    }
    public BufferedImage original() {
        return original;
    }
    public BufferedImage modified() {
        return modified;
    }
    public Pxl pxl(int x, int y) {
        return new Pxl(modified.getRGB(x, y));
    }
    public void pxl(int x, int y, int c) {
        modified.setRGB(x, y, c);
    }
    public void pxl(int x, int y, Pxl c) {
        
        modified.setRGB(x, y, c.color().getRGB());
    }
    public int width() {
        return modified.getWidth();
    }
    public int height() {
        return modified.getHeight();
    }
    public int area() {
        return width() * height();
    }
    public Pxl[][] horizontalLines(String method, boolean reverse) {
        Pxl[][] rows = new Pxl[height()][width()];
        for(int y = 0; y < height(); y++) {
            for(int x = 0; x < width(); x++) {
                rows[y][x] = pxl(x, y);
                rows[y][x].mode(method);
                rows[y][x].reverse(reverse);
            }
        }
        return rows;
    }
    public Pxl[][] verticalLines(String method, boolean reverse) {
        Pxl[][] columns = new Pxl[width()][height()];
        for(int x = 0; x < width(); x++) {
            for(int y = 0; y < height(); y++) {
                columns[x][y] = pxl(x, y);
                columns[x][y].mode(method);
                columns[x][y].reverse(reverse);
            }
        }
        return columns;
    }
    public Pxl[] allPixels(String method, boolean reverse) {
        Pxl[] all = new Pxl[width() * height()];
        for(int x = 0; x < width(); x++) {
            for(int y = 0; y < height(); y++) {
                all[x + y * width()] = pxl(x, y);
                all[x + y * width()].mode(method);
                all[x + y * width()].reverse(reverse);
            }
        }
        return all;
    }
    public Pxl[][] rDiagonalLines(String method, boolean reverse) {
        Pxl[][] dias = new Pxl[height() + width() - 1][];
        int kp = width() - height();
        int min = Math.min(width(), height());
        int j = 0;
        for(int k = (1 - height()); k <= (width() - 1); k++) {
            int length;
            if(k == 0 || (kp < 0 && k >= kp && k <= 0) || (kp > 0 && k <= kp && k >= 0)) {
                length = min;
            } else {
                if(k < 0) {
                    length = height() + k;
                } else {
                    length = width() - k;
                }
            }
            Pxl[] line = new Pxl[length];
            int x;
            int y;
            if(k < 0) {
                x = width() - 1;
                y = Math.abs(k);
            } else {
                y = 0;
                x = width() - 1 - k;
            }
            int i = 0;
            while(i < length) {
                line[i] = pxl(x, y);
                line[i].mode(method);
                line[i].reverse(reverse);
                x--;
                y++;
                i++;
            }
            dias[j] = line;
            j++;
        }
        return dias;
    }
    public Pxl[][] diagonalLines(String method, boolean reverse) {
        Pxl[][] dias = new Pxl[height() + width() - 1][];
        int kp = width() - height();
        int min = Math.min(width(), height());
        int j = 0;
        for(int k = (1 - height()); k <= (width() - 1); k++) {
            int length;
            if(k == 0 || (kp < 0 && k >= kp && k <= 0) || (kp > 0 && k <= kp && k >= 0)) {
                length = min;
            } else {
                if(k < 0) {
                    length = height() + k;
                } else {
                    length = width() - k;
                }
            }
            Pxl[] line = new Pxl[length];
            int x;
            int y;
            if(k < 0) {
                x = 0;
                y = Math.abs(k);
            } else {
                y = 0;
                x = k;
            }
            int i = 0;
            while(i < length) {
                line[i] = pxl(x, y);
                line[i].mode(method);
                line[i].reverse(reverse);
                x++;
                y++;
                i++;
            }
            dias[j] = line;
            j++;
        }
        return dias;
    }
    public static int randomInRange(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min + 1)) + min;
    }
    public Pxl[][] bands(Pxl[] a, int min, int max) {
        if (min > a.length) {
            min = a.length;
        } else if (min < 1) {
            min = 1;
        }
        if (max > a.length) {
            max = a.length;
        } else if (max < 1) {
            max = 1;
        }
        if (min > max) {
            int t = max;
            max = min;
            min = t;
        }
        if(a.length <= min) {
            Pxl[][] m = new Pxl[1][];
            m[0] = a;
            return m;
        }
        ArrayList<Pxl[]> master = new ArrayList<Pxl[]>();
        int fi = 0;
        int li = randomInRange(min, max) - 1;
        boolean end = false;
        while(true) {
            Pxl[] band = new Pxl[li + 1 - fi];
            for(int i = fi; i <= li; i++) {
                band[i - fi] = a[i];
            }
            master.add(band);
            fi = li + 1;
            if(end) {
                break;
            }
            li += randomInRange(min, max) - 1;
            if(li >= a.length) {
                li = a.length - 1;
                end = true;
            }
        }
        Pxl[][] r = new Pxl[master.size()][];
        return master.toArray(r);
    }
    public Pxl[] middlate(Pxl[] a) {
        Pxl[] nu = new Pxl[a.length];
        for(int e = 0; e < a.length; e++) {
            if((a.length + e) % 2 == 1) {
                nu[(int)(0.5 * (a.length + e - 1))] = a[e];
            } else {
                nu[(int)(0.5 * (a.length - e) - 1)] = a[e];
            }
        }
        return nu;
    }
    public Pxl[] reverseMiddlate(Pxl[] a) {
        Pxl[] nu = new Pxl[a.length];
        double k = (Math.ceil(a.length/2.0) - 1);
        for(int e = 0; e < a.length; e++) {
            if(e < k) {
                nu[a.length - 2 * e - 2] = a[e];
            } else if(e > k) {
                nu[2 * e - a.length + 1] = a[e];
            } else {
                nu[0] = a[e];
            }
        }
        return nu;
    }
    public Pxl[] middlate(Pxl[] a, int m) {
        Pxl[] nu = a;
        if(m > 0) {
            for(int i = 0; i < m; i++) {
                nu = middlate(nu);
            }
        } else if(m < 0) {
            for(int i = 0; i < Math.abs(m); i++) {
                nu = reverseMiddlate(nu);
            }
        }
        return nu;
    }
    public Pxl[] srt(Pxl[] band, int middlate) {
        Arrays.sort(band);
        return middlate(band, middlate);
    }
    public void replaceHorizontal(Pxl[] line, int i) {
        for(int x = 0; x < line.length; x++) {
            pxl(x, i, line[x]);
        }
    }
    public void replaceVertical(Pxl[] line, int i) {
        for(int y = 0; y < line.length; y++) {
            pxl(i, y % height(), line[y]);
        }
    }
    public void replaceDiagonal(Pxl[] line, int i) {
        for(int k = 0; k < line.length; k++) {
            pxl(
                    ((i < 0) ? k : i + k), 
                    ((i < 0) ? Math.abs(i) + k : k), 
                    line[k]
            ); 
        }
    }
    public void replaceRDiagonal(Pxl[] line, int i) {
        for(int k = 0; k < line.length; k++) {
            pxl(
                    width() - 1 - ((i < 0) ? k : k + i),
                    ((i < 0) ? Math.abs(i) + k : k),
                    line[k]
            );
        }
    }
    public void brute(int min, int max, String direction, String method, boolean reverse, int middlate) {
        Pxl[][] lines;
        switch(direction) {
            case "horizontal":
                lines = horizontalLines(method, reverse);
            break;
            case "vertical":
                lines = verticalLines(method, reverse);
            break;
            case "diagonal":
                lines = diagonalLines(method, reverse);
            break;
            default:
                lines = rDiagonalLines(method, reverse);
            break;
        }
        for(int l = 0; l < lines.length; l++) {
            Pxl[] line = lines[l];
            Pxl[][] bands = bands(line, min, max);
            Pxl[] newLine = {};
            for(int b = 0; b < bands.length; b++) {
                Pxl[] band = bands[b];
                bands[b] = srt(band, middlate);
                newLine = concatenate(newLine, bands[b]);
            }
            switch(direction) {
                case "horizontal":
                    replaceHorizontal(newLine, l);
                break;
                case "vertical":
                    replaceVertical(newLine, l);
                break;
                case "diagonal":
                    replaceDiagonal(newLine, l - height() + 1);
                break;
                default:
                    replaceRDiagonal(newLine, l - height() + 1);
                break;
            }
        }
    }
    public static <T> T[] concatenate(T[] a, T[] b) {
        if(a.length == 0) {
            return b;
        }
        if(b.length == 0) {
            return a;
        }
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
        int k = 0;
        for(int i = 0; i < a.length; i++, k++) {
            c[k] = a[i];
        }
        for(int i = 0; i < b.length; i++, k++) {
            c[k] = b[i];
        }
        return c;
    }
    /* http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage */
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}