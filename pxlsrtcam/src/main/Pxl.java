package main;

import java.awt.Color;

public class Pxl implements Comparable<Pxl>{
    private Color color;
    private String method;
    private double[] avg;
    private boolean reverse;
    private double sobel;
    public Pxl(int o) {
        color = new Color(o);
    }
    public Pxl(int o, double s) {
        color = new Color(o);
        sobel = s;
    }
    public void method(String mode) {
        this.method = mode;
    }
    public String method() {
        return method;
    }
    public void reverse(boolean reverse) {
        this.reverse = reverse;
    }
    public void avg(double[] avg) {
        this.avg = avg;
    }
    public double[] avg() {
        return avg;
    }
    public Color color() {
        return color;
    }
    public double grey() {
        return (red() * 0.2126 + green() * 0.7152 + blue() * 0.0722) / 3.0;
    }
    public void sobel(double s) {
        sobel = s;
    }
    public double sobel() {
        return sobel;
    }
    public float hue() {
        return Color.RGBtoHSB(red(), green(), blue(), null)[0];
    }
    public float saturation() {
        return Color.RGBtoHSB(red(), green(), blue(), null)[1];
    }
    public float brightness() {
        return Color.RGBtoHSB(red(), green(), blue(), null)[2];
    }
    public int red() {
        return color.getRed();
    }
    public int green() {
        return color.getGreen();
    }
    public int blue() {
        return color.getBlue();
    }
    public double uniqueness() {
        return Math.sqrt(Math.pow(red() - avg[0], 2) + Math.pow(green() - avg[1], 2) + Math.pow(blue() - avg[2], 2));
    }
    @Override
    public int compareTo(Pxl o) {
        double v1;
        double v2;
        switch(method) {
            case "red":
                v1 = red();
                v2 = o.red();
            break;
            case "green":
                v1 = green();
                v2 = o.green();
            break;
            case "blue":
                v1 = blue();
                v2 = o.blue();
            break;
            case "hue":
                v1 = hue();
                v2 = o.hue();
            break;
            case "saturation":
                v1 = saturation();
                v2 = o.saturation();
            break;
            case "brightness":
                v1 = brightness();
                v2 = o.brightness();
            break;
            case "sum-hsb":
                v1 = hue() / 360.0 + saturation() + brightness();
                v2 = o.hue() / 360.0 + o.saturation() + o.brightness();
            break;
            case "luma":
                v1 = red() * 0.2126 + green() * 0.7152 + blue() * 0.0722;
                v2 = o.red() * 0.2126 + o.green() * 0.7152 + o.blue() * 0.0722;
            break;
            case "magenta":
                v1 = red() + blue();
                v2 = o.red() + o.blue();
            break;
            case "cyan":
                v1 = green() + blue();
                v2 = o.green() + o.blue();
            break;
            case "yellow":
                v1 = red() + green();
                v2 = o.red() + o.green();
            break;
            case "uniqueness":
                v1 = uniqueness();
                v2 = o.uniqueness();
            break;
            default:
                v1 = red() + green() + blue();
                v2 = o.red() + o.green() + o.blue();
            break;
        }
        if(reverse) {
            return -Double.compare(v1, v2);
        } else {
            return Double.compare(v1, v2);
        }
    }
}