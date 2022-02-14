package pt.ua.dicoogle.core.mlprovider;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;
import org.dcm4che3.imageio.plugins.dcm.DicomMetaData;
import org.dcm4che3.io.DicomInputStream;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;
import pt.ua.dicoogle.sdk.mlprovider.MLDataset;
import pt.ua.dicoogle.sdk.mlprovider.MLImageDataset;
import pt.ua.dicoogle.sdk.mlprovider.MLlabel;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class PrepareDatasetTask implements Callable<MLDataset> {

    private final CreateDatasetRequest request;
    private final PluginController controller;
    private String dataset;

    public PrepareDatasetTask(PluginController controller, CreateDatasetRequest request) {
        this.controller = controller;
        this.request = request;
        this.dataset = UUID.randomUUID().toString();
    }

    @Override
    public MLDataset call() throws Exception {

        MLImageDataset mlDataset = new MLImageDataset();
        ImageReader imageReader = getImageReader();


        this.request.getDataset().entrySet().forEach((entry -> {
            try {

                Attributes dataset;
                DicomMetaData dicomMetaData;
                DicomImageReadParam param;

                // Query this image using the first available query provider
                Iterable<SearchResult> results = controller
                        .query(controller.getQueryProvidersName(true).get(0), "SOPInstanceUID:" + entry.getKey()).get();

                for (SearchResult image : results) {
                    DicomInputStream dis = new DicomInputStream(new File(image.getURI()));

                    Attributes fmi = dis.getFileMetaInformation();
                    dataset = dis.readDataset(-1, -1);
                    dicomMetaData = new DicomMetaData(fmi, dataset);

                    int tile_height = fmi.getInt(Tag.Rows, -1);
                    int tile_width = fmi.getInt(Tag.Columns, -1);

                    imageReader.setInput(dicomMetaData);
                    param = (DicomImageReadParam) imageReader.getDefaultReadParam();

                    List<MLlabel> labels;

                    for (BulkAnnotation annotation : entry.getValue()) {
                        labels = new ArrayList<>();
                        List<List<Integer>> frameMatrix = getFrameMatrixFromAnnotation(fmi, annotation);
                        BufferedImage combined = new BufferedImage(frameMatrix.size() * tile_width,
                                frameMatrix.get(0).size() * tile_height, BufferedImage.TYPE_INT_RGB);
                        Graphics g = combined.getGraphics();
                        int c = -1;
                        int d = 0;
                        for (List<Integer> row : frameMatrix) {
                            c++;
                            for (Integer frame : row) {
                                BufferedImage bb = imageReader.read(frame, param);
                                g.drawImage(bb, c * tile_width, d++ * tile_height, null);
                            }
                        }
                        g.dispose();
                        // Save as new image
                        File f = new File(dataset + File.separator + entry.getKey() + ".jpg");
                        ImageIO.write(combined, "jpg", f);

                        MLlabel label = new MLlabel(annotation.getLabel(), 1);
                        labels.add(label);
                        mlDataset.getDataset().put(f.toURI(), labels);
                    }

                }
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace(); // Ignore
            }
        }));

        return mlDataset;
    }

    private static ImageReader getImageReader() {
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
        while (iter.hasNext()) {
            ImageReader reader = iter.next();
            if (reader instanceof DicomImageReader)
                return reader;
        }
        return null;
    }

    private List<List<Integer>> getFrameMatrixFromAnnotation(Attributes attrs, BulkAnnotation annotation) {
        int image_width = attrs.getInt(Tag.TotalPixelMatrixColumns, -1);

        int tile_height = attrs.getInt(Tag.Rows, -1);
        int tile_width = attrs.getInt(Tag.Columns, -1);

        int nx_tiles = (int) Math.ceil(image_width / tile_width);

        List<List<Integer>> matrix = new ArrayList<>();

        switch (annotation.getAnnotationType()) {
            case RECTANGLE:
                // Get dimmensions of the annotation
                double width = annotation.getPoints().get(0).distance(annotation.getPoints().get(1));
                double height = annotation.getPoints().get(0).distance(annotation.getPoints().get(3));

                // Estimate the number of frames that the annotation covers in height and width
                int xframes = (int) Math.ceil(width / tile_width);
                int yframes = (int) Math.ceil(height / tile_height);

                // Calculate the starting position of the annotation in frame coordinates
                int x_c = (int) Math.floor(annotation.getPoints().get(0).getX() / tile_width);
                int y_c = (int) Math.floor(annotation.getPoints().get(0).getX() / tile_width);

                for (int i = y_c; i < (yframes + y_c); i++) {
                    matrix.add(new ArrayList<>());
                    for (int j = x_c; j < (xframes + x_c); j++) {
                        matrix.get(i - y_c).add(i * nx_tiles + j);
                    }
                }
                break;
        }

        return matrix;
    }

}
