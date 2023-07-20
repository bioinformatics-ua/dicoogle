package pt.ua.dicoogle.server.web.servlets.mlprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.mlprovider.TrainRequest;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.mlprovider.MLProviderInterface;
import pt.ua.dicoogle.sdk.mlprovider.MLTrainTask;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This servlet implements the request training and cancel training endpoints.
 * To train a model, labels/annotations must first be uploaded to the provider.
 */
public class TrainServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TrainServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String dataString = IOUtils.toString(req.getReader());
        if (dataString == null) {
            ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Empty POST body");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        TrainRequest trainRequest;
        try {
            trainRequest = mapper.readValue(dataString, TrainRequest.class);
            MLProviderInterface mlplugin = PluginController.getInstance().getMachineLearningProviderByName(trainRequest.getProvider(), true);
            if(mlplugin == null){
                log.error("A provider with the provided name does not exist");
                ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Malformed request");
            } else {
                MLTrainTask trainTask = mlplugin.trainModel(trainRequest.getModelID());
                switch (trainTask.getStatus()){
                    case BUSY:
                        log.error("Could not create training task, service is busy");
                        ResponseUtil.sendError(resp, Status.CLIENT_ERROR_METHOD_NOT_ALLOWED.getCode(), "Could not create training task, service is busy");
                        break;
                    case REJECTED:
                        log.error("Could not create training task, request is malformed");
                        ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Could not create training task, service is busy");
                        break;
                    default:
                        resp.setContentType("application/json");
                        PrintWriter out = resp.getWriter();
                        mapper.writeValue(out, trainTask);
                        out.close();
                        out.flush();
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing json string", e);
            ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Malformed request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String dataString = IOUtils.toString(req.getReader());
        if (dataString == null) {
            ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Empty POST body");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        TrainRequest trainRequest;
        try {
            trainRequest = mapper.readValue(dataString, TrainRequest.class);
            MLProviderInterface mlplugin = PluginController.getInstance().getMachineLearningProviderByName(trainRequest.getProvider(), true);
            if(mlplugin == null){
                log.error("A provider with the provided name does not exist");
                ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Malformed request");
            } else {
                boolean stopped = mlplugin.stopTraining(trainRequest.getTrainingTaskID());

                if(!stopped){
                    log.error("Could not stop training task");
                    ResponseUtil.sendError(resp, Status.SERVER_ERROR_SERVICE_UNAVAILABLE.getCode(), "Could not stop training task");
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing json string", e);
            ResponseUtil.sendError(resp, Status.CLIENT_ERROR_BAD_REQUEST.getCode(), "Malformed request");
        }
    }

}
