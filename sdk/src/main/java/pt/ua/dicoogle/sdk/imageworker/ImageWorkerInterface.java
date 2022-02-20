package pt.ua.dicoogle.sdk.imageworker;

import org.dcm4che2.io.DicomInputStream;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;

public abstract class ImageWorkerInterface {

    public abstract ImageROI extractROI(DicomInputStream is, BulkAnnotation annotation);

    public abstract Iterable<ImageROI> extractROIs(DicomInputStream is, Iterable<BulkAnnotation> annotations);
}
