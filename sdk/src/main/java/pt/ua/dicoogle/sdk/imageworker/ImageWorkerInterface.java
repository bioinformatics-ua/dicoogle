package pt.ua.dicoogle.sdk.imageworker;

import pt.ua.dicoogle.sdk.DicooglePlugin;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;

public interface ImageWorkerInterface extends DicooglePlugin {
    public abstract ImageROI extractROI(SearchResult sr, BulkAnnotation annotation);

    public abstract Iterable<ImageROI> extractROIs(SearchResult sr, Iterable<BulkAnnotation> annotations);
}
