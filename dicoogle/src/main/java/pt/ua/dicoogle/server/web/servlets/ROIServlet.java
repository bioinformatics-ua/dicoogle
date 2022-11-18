package pt.ua.dicoogle.server.web.servlets;

import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;
import pt.ua.dicoogle.sdk.datastructs.dim.Point2D;
import pt.ua.dicoogle.sdk.utils.QueryException;
import pt.ua.dicoogle.server.web.dicom.ROIExtractor;
import pt.ua.dicoogle.server.web.utils.LocalImageCache;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class ROIServlet extends HttpServlet {


    private static final Logger logger = LoggerFactory.getLogger(ROIServlet.class);
    private static final long serialVersionUID = 1L;

    private final LocalImageCache cache;
    private final String queryProvider;
    private final ROIExtractor roiExtractor;
    private HashMap<String, String> extraFields;

    /**
     * Creates an image servlet.
     *
     * @param cache the local image caching system, can be null and if so no caching mechanism will be used.
     */
    public ROIServlet(LocalImageCache cache) {
        this.cache = cache;
        this.roiExtractor = new ROIExtractor();
        List<String> dicomProviders = ServerSettingsManager.getSettings().getArchiveSettings().getDIMProviders();
        queryProvider = dicomProviders.iterator().next();

        extraFields = new HashMap<>();
        extraFields.put("SOPInstanceUID", "SOPInstanceUID");
        extraFields.put("SharedFunctionalGroupsSequence_PixelMeasuresSequence_PixelSpacing", "SharedFunctionalGroupsSequence_PixelMeasuresSequence_PixelSpacing");
        extraFields.put("Rows", "Rows");
        extraFields.put("Columns", "Columns");
        extraFields.put("NumberOfFrames", "NumberOfFrames");
        extraFields.put("TotalPixelMatrixColumns", "TotalPixelMatrixColumns");
        extraFields.put("TotalPixelMatrixRows", "TotalPixelMatrixRows");
        extraFields.put("ImageType", "ImageType");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sopInstanceUID = request.getParameter("uid");
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String width = request.getParameter("width");
        String height = request.getParameter("height");

        if(sopInstanceUID == null || sopInstanceUID.isEmpty()){
            response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "SOPInstanceUID provided was invalid");
            return;
        }

        if(x == null || x.isEmpty() || y == null || y.isEmpty() || width == null || width.isEmpty() || height == null || height.isEmpty()){
            response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "ROI provided was invalid");
            return;
        }

        BulkAnnotation annotation;
        try{
            int nX = Integer.parseInt(x);
            int nY = Integer.parseInt(y);
            int nWidth = Integer.parseInt(width);
            int nHeight = Integer.parseInt(height);
            annotation = new BulkAnnotation();
            Point2D tl = new Point2D(nX, nY);
            Point2D tr = new Point2D(nX + nWidth, nY);
            Point2D bl = new Point2D(nX, nY + nHeight);
            Point2D br = new Point2D(nX + nWidth, nY + nHeight);
            List<Point2D> points = new ArrayList<>();
            points.add(tl); points.add(tr); points.add(bl); points.add(br);
            annotation.setPoints(points);
            annotation.setAnnotationType(BulkAnnotation.AnnotationType.RECTANGLE);
        } catch (NumberFormatException e){
            response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "ROI provided was invalid");
            return;
        }

        QueryInterface queryInterface = PluginController.getInstance().getQueryProviderByName(queryProvider, false);

        String query = String.format("SOPInstanceUID:%s", sopInstanceUID);
        Iterable<SearchResult> results;
        try {
            results = queryInterface.query(query, extraFields);
        } catch (QueryException e) {
            logger.error("Error requesting ROI for image {}", sopInstanceUID, e);
            response.sendError(Status.SERVER_ERROR_INTERNAL.getCode(), "Error building response");
            return;
        }

        if(!results.iterator().hasNext()){
            response.sendError(Status.CLIENT_ERROR_NOT_FOUND.getCode(), String.format("No instances exist with SOPInstanceUID: %s", sopInstanceUID));
            return;
        }

        //We're only expecting one result
        SearchResult result = results.iterator().next();

        StorageInputStream sis = PluginController.getInstance().resolveURI(result.getURI()).iterator().next();

        BufferedImage bi = roiExtractor.extractROI(sis, annotation);

        if(bi != null){
            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.close();
            return;
        }

        response.sendError(Status.CLIENT_ERROR_NOT_FOUND.getCode(), "Could not build ROI with the provided UID");
    }

}