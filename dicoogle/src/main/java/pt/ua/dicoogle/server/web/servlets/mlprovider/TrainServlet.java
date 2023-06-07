package pt.ua.dicoogle.server.web.servlets.mlprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.mlprovider.TrainRequest;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.mlprovider.MLProviderInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TrainServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TrainServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String dataString = IOUtils.toString(req.getReader());
        if (dataString == null) {
            resp.sendError(404, "Empty POST body");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        TrainRequest trainRequest;
        try {
            trainRequest = mapper.readValue(dataString, TrainRequest.class);
            MLProviderInterface mlplugin = PluginController.getInstance().getMachineLearningProviderByName(trainRequest.getProvider(), true);
            if(mlplugin == null){
                log.error("A provider with the provided name does not exist");
                resp.sendError(404, "Malformed request");
            } else {
                mlplugin.trainModel(trainRequest.getModelID());
            }
        } catch (Exception e) {
            log.error("Error parsing json string", e);
            resp.sendError(404, "Malformed request");
        }
    }

}
