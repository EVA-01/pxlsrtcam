package main;

import java.awt.Color;

public class Pxl implements Comparable<Pxl>{
    private Color color;
    private String mode;
    private double[] avg;
    private boolean reverse;
    public Pxl(int o, String mode, boolean reverse, double[] avg) {
        color = new Color(o);
        this.mode = mode;
        this.reverse = reverse;
        this.avg = avg;
    }
    public Pxl(Color o, String mode, boolean reverse, double[] avg) {
        color = o;
        this.mode = mode;
        this.reverse = reverse;
        this.avg = avg;
    }
    public Pxl(int o, String mode, boolean reverse) {
        color = new Color(o);
        this.mode = mode;
        this.reverse = reverse;
    }
    public Pxl(Color o, String mode, boolean reverse) {
        color = o;
        this.mode = mode;
        this.reverse = reverse;
    }
    public Pxl(Color o) {
        color = o;
    }
    public Pxl(int o) {
        color = new Color(o);
    }
    public void mode(String mode) {
        this.mode = mode;
    }
    public void reverse(boolean reverse) {
        this.reverse = reverse;
    }
    public void avg(double[] avg) {
        this.avg = avg;
    }
    public Color color() {
        return color;
    }
    public float hue() {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
    }
    public float saturation() {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[1];
    }
    public float brightness() {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[2];
    }
    public double uniqueness() {
        return Math.sqrt(Math.pow(color.getRed() - avg[0], 2) + Math.pow(color.getGreen() - avg[1], 2) + Math.pow(color.getBlue() - avg[2], 2));
    }
    @Override
    public int compareTo(Pxl o) {
        double v1;
        double v2;
        switch(mode) {
            case "red":
                v1 = color.getRed();
                v2 = o.color().getRed();
            break;
            case "green":
                v1 = color.getGreen();
                v2 = o.color().getGreen();
            break;
            case "blue":
                v1 = color.getBlue();
                v2 = o.color().getBlue();
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
                v1 = color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722;
                v2 = o.color().getRed() * 0.2126 + o.color().getGreen() * 0.7152 + o.color().getBlue() * 0.0722;
            break;
            case "magenta":
                v1 = color.getRed() + color.getBlue();
                v2 = o.color().getRed() + o.color().getBlue();
            break;
            case "cyan":
                v1 = color.getGreen() + color.getBlue();
                v2 = o.color().getGreen() + o.color().getBlue();
            break;
            case "yellow":
                v1 = color.getRed() + color.getGreen();
                v2 = o.color().getRed() + o.color().getGreen();
            break;
            case "uniqueness":
                v1 = uniqueness();
                v2 = o.uniqueness();
            break;
            default:
                v1 = color.getRed() + color.getGreen() + color.getBlue();
                v2 = o.color().getRed() + o.color().getGreen() + o.color().getBlue();
            break;
        }
        if(reverse) {
            return -Double.compare(v1, v2);
        } else {
            return Double.compare(v1, v2);
        }
    }
}