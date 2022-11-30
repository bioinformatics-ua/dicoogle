package pt.ua.dicoogle.server.web.servlets.mlprovider;

import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.dim.BulkAnnotation;
import pt.ua.dicoogle.sdk.datastructs.dim.Point2D;
import pt.ua.dicoogle.sdk.mlprovider.MLPrediction;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.dicom.ROIExtractor;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MakePredictionServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MakePredictionServlet.class);

    private final ROIExtractor roiExtractor;

    /**
     * Creates ROI servlet servlet.
     *
     */
    public MakePredictionServlet() {
        this.roiExtractor = new ROIExtractor();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String sopInstanceUID = request.getParameter("uid");
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String width = request.getParameter("width");
        String height = request.getParameter("height");
        String provider = request.getParameter("provider");

        if(sopInstanceUID == null || sopInstanceUID.isEmpty()){
            response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "SOPInstanceUID provided was invalid");
            return;
        }

        if(provider == null || provider.isEmpty()){
            response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Provider provided was invalid");
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

        BufferedImage bi = roiExtractor.extractROI(sopInstanceUID, annotation);

        // mount the resulting memory stream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ImageIO.write(bi, "jpg", bos);

        Task<MLPrediction> task = PluginController.getInstance().makePredictionOverImage(bos, provider);
        task.onCompletion(() -> {
            try {
                MLPrediction prediction = task.get();
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.append("This is a test");
                out.close();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Could not make prediction", e);
                try {
                    response.sendError(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Could not make prediction");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
