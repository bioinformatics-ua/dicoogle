package pt.ua.dicoogle.sdk.imageworker;

import pt.ua.dicoogle.sdk.DicooglePlugin;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;

import java.awt.image.BufferedImage;

public interface ImageWorkerInterface extends DicooglePlugin {

    /**
     * Given a search result and one annotation, extract its ROI as a BufferedImage.
     * This method does not write the ROI to disk.
     * @param sr
     * @param annotation
     * @return
     */
    public abstract BufferedImage extractROI(SearchResult sr, BulkAnnotation annotation);

    /**
     * Given a search result and a list of annotations, extract the ROIs the annotations define on the image.
     * This method will automatically write all ROIs to disk.
     * @param sr
     * @param annotations
     * @return a list of image ROIs defined by the list of annotations
     */
    public abstract Iterable<ImageROI> extractROIs(SearchResult sr, Iterable<BulkAnnotation> annotations);
}
