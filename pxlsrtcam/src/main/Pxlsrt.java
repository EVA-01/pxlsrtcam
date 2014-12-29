package main;

import java.awt.Color;
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
    private double[] sobels = null;
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
        if(sobels == null) {
            return new Pxl(modified.getRGB(x, y));
        } else {
            return new Pxl(modified.getRGB(x, y), sobel(x, y));
        }
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
                rows[y][x].method(method);
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
                columns[x][y].method(method);
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
                all[x + y * width()].method(method);
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
                line[i].method(method);
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
                line[i].method(method);
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
    public Pxl[][] bands(Pxl[] a, double threshold, boolean absolute) {
        /* Smart */
        if(a.length <= 1) {
            Pxl[][] m = new Pxl[1][];
            m[0] = a;
            return m;
        }
        ArrayList<Pxl[]> divisions = new ArrayList<Pxl[]>();
        ArrayList<Pxl> division = new ArrayList<Pxl>();
        for(int p = 0; p < a.length; p++) {
            if(division.isEmpty() || ((absolute ? a[p].sobel() : a[p].sobel() - division.get(division.size() - 1).sobel()) <= threshold)) {
                division.add(a[p]);
            } else {
                Pxl[] k = new Pxl[division.size()];
                divisions.add(division.toArray(k));
                division = new ArrayList<Pxl>();
                division.add(a[p]);
            }
            if(p == a.length - 1) {
                Pxl[] k = new Pxl[division.size()];
                divisions.add(division.toArray(k));
                division = new ArrayList<Pxl>();
            }
        }
        Pxl[][] r = new Pxl[divisions.size()][];
        return divisions.toArray(r);
    }
    public Pxl[][] bands(Pxl[] a, int min, int max) {
        /* Brute */
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
    public static void average(Pxl[] band) {
        double[] avg = {0, 0, 0};
        for(int p = 0; p < band.length; p++) {
            avg[0] += band[p].red();
            avg[1] += band[p].green();
            avg[2] += band[p].blue();
        }
        avg[0] /= (double)band.length;
        avg[1] /= (double)band.length;
        avg[2] /= (double)band.length;
        for(int p = 0; p < band.length; p++) {
            band[p].avg(avg);
        }
    }
    public Pxl[] srt(Pxl[] band, int middlate) {
        if(band.length < 1) {
            return band;
        }
        if("uniqueness".equals(band[0].method())) {
            average(band);
        }
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
    public double[] sobels() {
        if(this.sobels == null) {
            double[] sobels = new double[area()];
            for(int x = 0; x < width(); x++) {
                for(int y = 0; y < height(); y++) {
                    sobels[x + y * width()] = sobel(x, y);
                }
            }
            this.sobels = sobels;
        }
        return sobels;
    }
    public double sobel(int x, int y) {
        if(sobels != null) {
            return sobels[x + y * width()];
        } else {
            int[][] sobel_x = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
            };
            int[][] sobel_y = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
            };
            if(x != 0 && x != width() - 1 && y != 0 && y != height() - 1) {
                double t1 = pxl(x - 1, y - 1).grey();
                double t2 = pxl(x, y - 1).grey();
                double t3 = pxl(x + 1, y - 1).grey();
                double t4 = pxl(x - 1, y).grey();
                double t5 = pxl(x, y).grey();
                double t6 = pxl(x + 1, y).grey();
                double t7 = pxl(x - 1, y + 1).grey();
                double t8 = pxl(x, y + 1).grey();
                double t9 = pxl(x + 1, y + 1).grey();
                double pixel_x = sobel_x[0][0] * t1 + sobel_x[0][1] * t2 + sobel_x[0][2] * t3 + sobel_x[1][0] * t4 + sobel_x[1][1] * t5 + sobel_x[1][2] * t6 + sobel_x[2][0] * t7 + sobel_x[2][1] * t8 + sobel_x[2][2] * t9;
		double pixel_y = sobel_y[0][0] * t1 + sobel_y[0][1] * t2 + sobel_y[0][2] * t3 + sobel_y[1][0] * t4 + sobel_y[1][1] * t5 + sobel_y[1][2] * t6 + sobel_y[2][0] * t7 + sobel_y[2][1] * t8 + sobel_y[2][2] * t9;
                return Math.sqrt(pixel_x * pixel_x + pixel_y * pixel_y);
            } else {
                return 0;
            }
        }
    }
    public void smart(double threshold, boolean absolute, String direction, String method, boolean reverse, int middlate) {
        sobels();
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
            Pxl[][] bands = bands(line, threshold, absolute);
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
    public int getFirstNotBlackY(int x, int y, int value) {
        if (y < height()) {
            while (get(x, y) < value) {
                y++;
                if (y >= height()) {
                    return -1;
                }
            }
        }
        return y;
    }
    public int getFirstNotBlackX(int x, int y, int value) {
        if(x < width()) {
            while (get(x, y) < value) {
                x++;
                if (x >= width()) {
                    return -1;
                }
            }
        }
        return x;
    }
    public int getNextBlackY(int x, int y, int value) {
        y += 1;
        if (y < height()) {
            while (get(x, y) > value) {
                y++;
                if (y >= height()) {
                    return height() - 1;
                }
            }
        }
        return y - 1;
    }
    public int getNextBlackX(int x, int y, int value) {
        x += 1;
        if(x < width()) {
            while (get(x, y) > value) {
                x++;
                if (x >= width()) {
                    return width() - 1;
                }
            }
        }
        return x - 1;
    }
    public int getFirstBrightY(int x, int y, int value) {
        if (y < height()) {
            while (pxl(x, y).brightness() * 255 < value) {
                y++;
                if (y >= height()) {
                    return -1;
                }
            }
        }
        return y;
    }
    public int getFirstBrightX(int x, int y, int value) {
        if(x < width()) {
            while (pxl(x, y).brightness() * 255 < value) {
                x++;
                if (x >= width()) {
                    return -1;
                }
            }
        }
        return x;
    }
    public int getNextDarkY(int x, int y, int value) {
        y += 1;
        if (y < height()) {
            while (pxl(x, y).brightness() * 255 > value) {
                y++;
                if (y >= height()) {
                    return height() - 1;
                }
            }
        }
        return y - 1;
    }
    public int getNextDarkX(int x, int y, int value) {
        x += 1;
        if(x < width()) {
            while (pxl(x, y).brightness() * 255 > value) {
                x++;
                if (x >= width()) {
                    return width() - 1;
                }
            }
        }
        return x - 1;
    }
    public int getFirstNotWhiteY(int x, int y, int value) {
        if (y < height()) {
            while (get(x, y) > value) {
                y++;
                if (y >= height()) {
                    return -1;
                }
            }
        }
        return y;
    }
    public int getNextWhiteY(int x, int y, int value) {
        y += 1;
        if (y < height()) {
            while (get(x, y) < value) {
                y++;
                if (y >= height()) {
                    return height() - 1;
                }
            }
        }
        return y - 1;
    }
    public int getFirstNotWhiteX(int x, int y, int value) {
        if(x < width()) {
            while (get(x, y) > value) {
                x++;
                if (x >= width()) {
                    return -1;
                }
            }
        }
        return x;
    }
    public int getNextWhiteX(int x, int y, int value) {
        x += 1;
        if(x < width()) {
            while(get(x, y) < value) {
                x++;
                if (x >= width()) {
                    return width() - 1;
                }
            }
        }
        return x - 1;
    }
    public int get(int x, int y) {
        return modified.getRGB(x, y);
    }
    public void set(int x, int y, int c) {
        modified.setRGB(x, y, c);
    }
    public void kim(String method) {
        switch(method) {
            case "black":
                kim(method, (new Color(11, 220, 0)).getRGB());
            break;
            case "brightness":
                kim(method, 60);
            break;
            case "white":
                kim(method, (new Color(57, 167, 192)).getRGB());
            break;
        }
    }
    public void kim(String method, int value) {
        int column = 0;
        int row = 0;
        while(column < width()) {
            int x = column;
            int y = 0;
            int yend = 0;
            while(yend < height()) {
                switch(method) {
                    case "black":
                        y = getFirstNotBlackY(x, y, value);
                        yend = getNextBlackY(x, y, value);
                    break;
                    case "brightness":
                        y = getFirstBrightY(x, y, value);
                        yend = getNextDarkY(x, y, value);
                    break;
                    case "white":
                        y = getFirstNotWhiteY(x, y, value);
                        yend = getNextWhiteY(x, y, value);
                    break;
                }
                if(y < 0) {
                    break;
                }
                int sortLength = yend - y;
                int[] unsorted = new int[sortLength];
                for(int i = 0; i < sortLength; i++) {
                    unsorted[i] = get(x, y + i);
                }
                Arrays.sort(unsorted);
                for(int i = 0; i < sortLength; i++) {
                    set(x, y + i, unsorted[i]);
                }
                y = yend + 1;
            }
            column++;
        }
        while(row < height()) {
            int x = 0;
            int y = row;
            int xend = 0;
            while(xend < width()) {
                switch(method) {
                    case "black":
                        x = getFirstNotBlackX(x, y, value);
                        xend = getNextBlackX(x, y, value);
                    break;
                    case "brightness":
                        x = getFirstBrightX(x, y, value);
                        xend = getNextDarkX(x, y, value);
                    break;
                    case "white":
                        x = getFirstNotWhiteX(x, y, value);
                        xend = getNextWhiteX(x, y, value);
                    break;
                }
                if(x < 0) {
                    break;
                }
                int sortLength = xend - x;
                int[] unsorted = new int[sortLength];
                for(int i = 0; i < sortLength; i++) {
                    unsorted[i] = get(x + i, y);
                }
                Arrays.sort(unsorted);
                for(int i = 0; i < sortLength; i++) {
                    set(x + i, y, unsorted[i]);
                }
                x = xend + 1;
            }
            row++;
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
    public static <T> T randomInArray(T[] a) {
        return a[randomInRange(0, a.length - 1)];
    }
    /* http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage */
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}