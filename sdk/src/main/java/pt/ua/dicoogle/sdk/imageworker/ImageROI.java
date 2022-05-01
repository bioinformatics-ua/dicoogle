package pt.ua.dicoogle.sdk.imageworker;

import java.net.URI;

public class ImageROI {

    private int width;

    private int height;

    private URI roi;

    public ImageROI(int width, int height, URI roi) {
        this.width = width;
        this.height = height;
        this.roi = roi;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public URI getRoi() {
        return roi;
    }

    public void setRoi(URI roi) {
        this.roi = roi;
    }
}
