package pt.ua.dicoogle.sdk.mlprovider;

public class MLAnnotation {

    public enum PixelOrigin {
        FRAME, //Coordinates of this annotation are related to the frame (image section)
        VOLUME //Coordinates of this annotation are related to the Frame Matrix (whole image)
    }

    public enum AnnotationType {
        RECTANGLE,
        ELLIPSE,
        POLYGON,
        POLYLINE,
        POINT
    }

    private PixelOrigin pixelOrigin;

    private AnnotationType annotationType;

    private String label;
    private double confidence;

}
