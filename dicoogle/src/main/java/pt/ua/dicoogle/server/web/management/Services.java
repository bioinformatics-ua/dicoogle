/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server.web.management;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.core.settings.ServerSettings;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ua.dicoogle.server.ControlServices;
import pt.ua.dicoogle.plugins.PluginController;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import pt.ua.dicoogle.sdk.settings.Utils;

// FIXME do the procedures of this class need to be synchronized?!?
/**
 * A wrapper used to manage the services and plugins remotely through the web
 * environment/app.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Services {

    /**
     * The current instance of this class.
     */
    private static Services instance;
    /**
     * Pointers to the objects that maintain the services and plugins
     * information.
     */
    private static ControlServices svcs;
    private static PluginController plgs;
    /**
     * Types of actions to perform.
     */
    public static final int SVC_NO_ACTION = 0;
    public static final int SVC_START = 1;
    public static final int SVC_STOP = 2;
    public static final int SVC_INIT_START = 3;
    public static final int SVC_SET_PORT = 4;
    public static final int SVC_SET_SETTINGS = 5;
    /**
     * Textual representation of the actions available.
     */
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_INIT_START = "init-start";
    public static final String ACTION_SET_PORT = "set-port";
    public static final String ACTION_SET_ADVANCED_SETTINGS = "set-settings";
    /**
     * Textural representation of the params available.
     */
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_SERVICE = "service";
    public static final String PARAM_INIT_START = "init-start";
    public static final String PARAM_PORT = "port";
    /**
     * The embedded services name.
     */
    public static final String storageName = "Storage";
    public static final String queryRetrieveName = "Query Retrieve";
    public static final String webServicesName = "Web Services";
    public static final String webServerName = "Web Server";
    public static final String remoteGUIName = "Remote GUI";
    /**
     * The results indicating what was wrong with a performAction call.
     */
    public static final int RES_NO_ACTION = 0;
    public static final int RES_INVALID_ACTION = 1;
    public static final int RES_NO_SERVICE_NAME = 2;
    public static final int RES_INVALID_SERVICE_NAME = 3;
    public static final int RES_INVALID_ACTION_PARAMETER = 4;
    public static final int RES_WARNING = 5;
    public static final int RES_OK = 6;
    /**
     * The object responsible for saving the settings between server inits.
     */
    private XMLSupport xmlSupport;
    /**
     * Server settings container.
     */
    private ServerSettings cfgs;

    private Services() {
        svcs = ControlServices.getInstance();
        plgs = PluginController.getInstance();

        cfgs = ServerSettings.getInstance();
        xmlSupport = new XMLSupport();
    }

    /**
     * Returns the current instance of this class.
     *
     * @return the current instance of this class.
     */
    public synchronized static Services getInstance() {
        if (instance == null) {
            instance = new Services();
        }

        return instance;
    }

    /**
     * This converts and applies the new textual values into the
     * originalSettings HashMap, taking into account their original class/type.
     * Only the settings that exist on both Maps are updated, this avoid invalid
     * settings and Class type injections from happening.
     *
     * @param originalSettings the original advanced/internal settings of a
     * plugin or service.
     * @param newSettings the new settings to apply.
     * @return the originalSettings HashMap with the new values in place (you
     * can use the reference passed as originalSettings instead if you want,
     * they will allways be the same).
     */
    public HashMap<String, Object> processAdvancedSettings(HashMap<String, Object> originalSettings, HashMap<String, String[]> newSettings) {
        for (Map.Entry<String, Object> setting : originalSettings.entrySet()) {
            String name = Utils.getHTMLElementIDFromString(setting.getKey()); // NOTE remmember that the setting name is "kinda encoded" (and not in the URLEncode way, that's handled automagically)
            Object value = setting.getValue();

            if (newSettings.containsKey(name)) // if the setting is found on the request try to update its value (maintaining the same type ofc)
            {
                String[] newValue = newSettings.get(name);

                if (value != null) {
                    // parse the value depending on its class
                    if (value.getClass().equals(Integer.class)) {
                        value = Integer.valueOf(newValue[0]);
                    } else if (value.getClass().equals(Float.class)) {
                        value = Float.valueOf(newValue[0]);
                    } else if (value.getClass().equals(Boolean.class)) {
                        value = Boolean.valueOf(Utils.parseCheckBoxValue(newValue[0]));
                    } /*else if (value.getClass().equals(ServerDirectoryPath.class))
                     value = ServerDirectoryPath.fromHTTPParams((ServerDirectoryPath) value, newSettings, name);*/ 
                    //else if (value instanceof GenericSetting) {
                       // value = ((GenericSetting) value).fromHTTPParams(newSettings, name);
                //    } 
                    else // String or unrecognized class
                    {
                        value = newValue[0];
                    }

                    setting.setValue(value);
                } else // null is treated as String
                {
                    value = newValue[0];

                    setting.setValue(value);
                }

            } else // if the setting was not found check if it's a Boolean one, this is a FIX because browsers omit unchecked checkboxes on form action
            {
                if (value.getClass().equals(Boolean.class)) {
                    setting.setValue(false);
                }
            }
        }

        return originalSettings; // just for easier use
    }

    /**
     * Performs a certain action to a service or plugin.
     *
     * @param action the action to perform.
     * @param svcName the name of the service or plugin to perform the action
     * at.
     * @param initStart the init-start param value.
     * @param port the set-port param value.
     * @param advSettings a HashMap with all the advanced settings and their
     * values.
     * @return an int value indicating what was wrong with the call (see
     * Services.RES_* for values/name pairs)
     */
    @Deprecated
    public int performAction(int action, String svcName, boolean initStart, int port, HashMap<String, String[]> advSettings) {
        if ((svcName == null) || svcName.trim().isEmpty()) {
            return RES_NO_SERVICE_NAME;
        }

        try {
            switch (action) {
                case SVC_START:
                    if (svcName.equalsIgnoreCase(webServerName)) {
                        svcs.startWebServer();
                        return RES_OK;
                    } else {
                        if (svcName.equalsIgnoreCase(webServicesName)) {

                            svcs.startWebServices();
                            return RES_OK;
                        } else {
                            if (svcName.equalsIgnoreCase(storageName)) {
                                svcs.startStorage();
                                return RES_OK;
                            } else {
                                if (svcName.equalsIgnoreCase(queryRetrieveName)) {
                                    svcs.startQueryRetrieve();
                                    return RES_OK;
                                } else {
                                	//TODO: DELETED
                                    /*for (String plug : plgs.getPluginsNames()) {
                                        if (plug.equalsIgnoreCase(svcName)) {
                                            svcs.startPlugin(svcName);
                                            return RES_OK;
                                        }
                                    }*/

                                    return RES_INVALID_SERVICE_NAME;
                                }
                            }
                        }
                    }

                case SVC_STOP:
                    if (svcName.equalsIgnoreCase(webServerName)) {
                        svcs.stopWebServer();
                        return RES_OK;
                    } else {
                        if (svcName.equalsIgnoreCase(webServicesName)) {

                            svcs.stopWebServices();
                            return RES_OK;
                        } else {
                            if (svcName.equalsIgnoreCase(storageName)) {
                                svcs.stopStorage();
                                return RES_OK;
                            } else {
                                if (svcName.equalsIgnoreCase(queryRetrieveName)) {
                                    svcs.stopQueryRetrieve();
                                    return RES_OK;
                                } else {
                                	//TODO: DELETED
                                    /*for (String plug : plgs.getPluginsNames()) {
                                        if (svcName.equalsIgnoreCase(plug)) {
                                            svcs.stopPlugin(svcName);
                                            return RES_OK;
                                        }
                                    }*/

                                    return RES_INVALID_SERVICE_NAME;
                                }
                            }
                        }
                    }

                case SVC_INIT_START:
                    if (svcName.equalsIgnoreCase(webServerName)) {
                        setWebServerStart(initStart);
                        return RES_OK;
                    } else {
                        if (svcName.equalsIgnoreCase(webServicesName)) {

                            setWebServicesStart(initStart);
                            return RES_OK;
                        } else {
                            if (svcName.equalsIgnoreCase(storageName)) {
                                setStorageStart(initStart);
                                return RES_OK;
                            } else {
                                if (svcName.equalsIgnoreCase(queryRetrieveName)) {
                                    setQueryRetrieveStart(initStart);
                                    return RES_OK;
                                } else {
                                	//TODO: DELETED
                                    /*for (String plug : plgs.getPluginsNames()) {
                                        if (svcName.equalsIgnoreCase(plug)) {
                                            cfgs.setAutoStartPlugin(svcName, initStart);
                                            return RES_OK;
                                        }
                                    }*/

                                    return RES_INVALID_SERVICE_NAME;
                                }
                            }
                        }
                    }

                case SVC_SET_PORT: {
                    if ((port < 0) || (port > 65535)) {
                        return RES_INVALID_ACTION_PARAMETER;
                    }

                    if (svcName.equalsIgnoreCase(webServerName)) {
                        setWebServerPort(port);
                        return RES_OK;
                    } else {
                        if (svcName.equalsIgnoreCase(webServicesName)) {

                            setWebServicesPort(port);
                            return RES_OK;
                        } else {
                            if (svcName.equalsIgnoreCase(storageName)) {
                                setStoragePort(port);
                                return RES_OK;
                            } else {
                                if (svcName.equalsIgnoreCase(queryRetrieveName)) {
                                    setQueryRetrievePort(port);
                                    return RES_OK;
                                } else {
                                    if (svcName.equalsIgnoreCase(remoteGUIName)) {
                                        setRemoteGUIPort(port);
                                        return RES_OK;
                                    } else {
                                        // TODO for plugins

                                        return RES_INVALID_SERVICE_NAME;
                                    }
                                }
                            }
                        }
                    }
                }

                case SVC_SET_SETTINGS:
                    if (advSettings == null || advSettings.isEmpty()) {
                        return RES_INVALID_ACTION_PARAMETER;
                    } else {
                        if (svcName.equalsIgnoreCase(storageName)) {
                            HashMap<String, Object> settings = cfgs.getStorageSettings();

                            processAdvancedSettings(settings, advSettings);

                            // try to apply the settings
                            if (cfgs.tryStorageSettings(settings)) {
                                cfgs.setStorageSettings(settings);

                                saveSettings();

                                return RES_OK;
                            } else {
                                return RES_INVALID_ACTION_PARAMETER;
                            }
                        } else {
                            if (svcName.equalsIgnoreCase(queryRetrieveName)) {
                                HashMap<String, Object> settings = cfgs.getQueryRetrieveSettings();

                                processAdvancedSettings(settings, advSettings);

                                // try to apply the settings
                                if (cfgs.tryQueryRetrieveSettings(settings)) {
                                    cfgs.setQueryRetrieveSettings(settings);

                                    saveSettings();

                                    return RES_OK;
                                } else {
                                    return RES_INVALID_ACTION_PARAMETER;
                                }
                            } else {
                                if (svcName.equalsIgnoreCase(remoteGUIName)) {
                                    HashMap<String, Object> settings = cfgs.getRGUISettings();

                                    processAdvancedSettings(settings, advSettings);

                                    // try to apply the settings
                                    if (cfgs.tryRGUISettings(settings)) {
                                        cfgs.setRGUISettings(settings);

                                        saveSettings();

                                        return RES_OK;
                                    } else {
                                        return RES_INVALID_ACTION_PARAMETER;
                                    }
                                } else {
                                    if (plgs.hasAdvancedSettings(svcName)) {
                                        //TODO: DELETED
                                    	//HashMap<String, Object> settings = plgs.getAdvancedSettings(svcName);

                                        //processAdvancedSettings(settings, advSettings);


                                        // try to apply the settings
                                        if (/*plgs.trySettings(svcName, settings)*/true) {
                                            //plgs.setSettings(svcName, settings);

                                            //plgs.saveSettings();

                                            return RES_OK;
                                        } else {
                                            return RES_INVALID_ACTION_PARAMETER;
                                        }
                                    }

                                    return RES_INVALID_SERVICE_NAME;
                                }
                            }
                        }
                    }

                case SVC_NO_ACTION:
                default:
                    return RES_INVALID_ACTION;
            }
        } catch (IOException ex) {
            return RES_WARNING;
        }
    }

    /**
     * Performs a certain action to a service or plugin, and save the settings
     * if needed.
     *
     * @param request the request object.
     * @return an int value indicating what was wrong with the call (see
     * Services.RES_* for values/name pairs).
     */
    public int performAction(HttpServletRequest request) throws UnsupportedEncodingException {
        // set proper encoding on the request
        request.setCharacterEncoding("UTF-8");

        // get the action to take
        String action = request.getParameter(PARAM_ACTION);

        // check if it is a valid action
        if (action == null) {
            return RES_NO_ACTION;
        }

        // get the name of the service/plugin to execute the action at
        String svcName = request.getParameter(PARAM_SERVICE);

        int portNumber = 0;
        boolean initOnStart = false;
        HashMap<String, String[]> advSettings = new HashMap<>();

        int act = SVC_NO_ACTION;
        if (action.equalsIgnoreCase(ACTION_START)) {
            act = SVC_START;
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            act = SVC_STOP;
        } else if (action.equalsIgnoreCase(ACTION_INIT_START)) {
            initOnStart = Utils.parseCheckBoxValue(request.getParameter(PARAM_INIT_START));
            act = SVC_INIT_START;
        } else if (action.equalsIgnoreCase(ACTION_SET_PORT)) {
            String port = request.getParameter(PARAM_PORT);

            if (port == null || port.trim().isEmpty()) {
                return RES_INVALID_ACTION_PARAMETER;
            }

            portNumber = Integer.parseInt(port);
            act = SVC_SET_PORT;
        } else if (action.equalsIgnoreCase(ACTION_SET_ADVANCED_SETTINGS)) {
            // get all the settings and their values
            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String name = params.nextElement();

                // ignore the main params (the ones that go us here)
                if (name.equalsIgnoreCase(PARAM_ACTION) || name.equalsIgnoreCase(PARAM_SERVICE)) {
                    continue;
                }

                String[] value = request.getParameterValues(name);

                advSettings.put(name, value);
            }

            act = SVC_SET_SETTINGS;
        } else {
            return RES_INVALID_ACTION;
        }

        // execute the action requested
        int result = performAction(act, svcName, initOnStart, portNumber, advSettings);

        // save the settings if needed
        if ((result == RES_OK) && ((act == SVC_INIT_START) || (act == SVC_SET_PORT))) {
            saveSettings();
        }

        // return the performAction result
        return result;
    }

    /**
     * Processes a request from a servlet to perform an action on a service or
     * plugin.
     *
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @throws IOException if there was an error while attempting to perform the
     * requested action.
     */
    public void processServletRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // try to perform the action
        int result = performAction(request);

        // check the result and repond accordingly
        switch (result) {
            case RES_OK:
                response.setStatus(HttpServletResponse.SC_OK);
                break;

            case RES_NO_ACTION:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No action defined!");
                break;

            case RES_INVALID_ACTION:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action!");
                break;

            case RES_NO_SERVICE_NAME:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No service/plugin name defined!");
                break;

            case RES_INVALID_SERVICE_NAME:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid service/plugin name!");
                break;

            case RES_WARNING:
            default:
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected action result!");
                break;
        }
    }

    /**
     * Processes a request from a webapp to perform an action on a service or
     * plugin.
     *
     * @param request the webapp request object.
     * @param response the webapp response object.
     * @throws IOException if there was an error while attempting to perform the
     * requested action.
     * @return an int value that represents theresult of the operation performed
     * (see Services.RES_* for values/name pairs).
     */
    public int processWebappRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // try to perform the action
        int result = performAction(request);

        // check the result and repond accordingly
        switch (result) {
            case RES_OK:
                response.setStatus(HttpServletResponse.SC_OK);
                break;

            case RES_NO_ACTION:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;

            case RES_INVALID_ACTION:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;

            case RES_NO_SERVICE_NAME:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;

            case RES_INVALID_SERVICE_NAME:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;

            case RES_WARNING:
            default:
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
        }

        return result;
    }

    /**
     * Returns an HTML String containing the form element to perform some action
     * an a plugin name by the brokerURL.
     *
     * @param brokerURL the URL where the form will post its data.
     * @param action the action to take.
     * @param name the name of the service or plugin where the action will be
     * performed.
     * @return an HTML String containing the form element to perform some action
     * an a plugin name by the brokerURL.
     */
    private String getHTMLActionForm(String brokerURL, String action, String name) {
        String result = "<form action=\"" + brokerURL + "\" method=\"get\"><input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + action.toLowerCase() + "\" /><input type=\"hidden\" name=\"" + PARAM_SERVICE + "\" value=\"" + name + "\" />";

        String buttonStyle = "btn";
        if (action.equalsIgnoreCase("start")) {
            buttonStyle += " btn-success";
        }
        if (action.equalsIgnoreCase("stop")) {
            buttonStyle += " btn-danger";
        }

        result += "<input type=\"submit\" class=\"" + buttonStyle + "\" value=\"" + action + "\" />";

        // add a little warning for stopping the web server, making it unable to be used remotely again
		/*if (name.equalsIgnoreCase(webServerName))
         {
         result += "<div class=\"warning\"><span class=\"notice\">!</span></div>";
         }*/

        result += "</form>";

        return result;
    }

    /**
     * Returns an HTML String containing the form element to change the start on
     * init flag of a plugin name by the brokerURL.
     *
     * @param brokerURL the URL where the form will post its data.
     * @param name the name of the service or plugin where the action will be
     * performed.
     * @param startInitially if the plugin or service is currently set to start
     * initially or not.
     * @param canBeDisabled if this plugin or service can be disabled.
     * @return an HTML String containing the form element to change the start on
     * init flag of a plugin name by the brokerURL.
     */
    private String getHTMLStartOnInitForm(String brokerURL, String name, boolean startInitially, boolean canBeDisabled) {
        String result = "<form action=\"" + brokerURL + "\" method=\"get\"><input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_INIT_START + "\" /><input type=\"hidden\" name=\"" + PARAM_SERVICE + "\" value=\"" + name + "\" />";

        result += "<label for=\"" + PARAM_INIT_START + "-" + Utils.getHTMLElementIDFromString(name) + "\" class=\"checkbox inline\">";
        result += "<input type=\"checkbox\" id=\"" + PARAM_INIT_START + "-" + Utils.getHTMLElementIDFromString(name) + "\" name=\"" + PARAM_INIT_START + "\" " + (startInitially ? "checked=\"checked\"" : "") + " onclick=\"this.parent.submit();\" " + (canBeDisabled ? "" : "disabled=\"disabled\"") + " /> Auto Start";
        result += "</label>";
        result += "<input type=\"submit\" hidden=\"hidden\" />";

        // add a little warning for disabling the web server, making it unable to be used remotely again
		/*if (name.equalsIgnoreCase(webServerName))
         {
         result += "<div class=\"warning\"><span class=\"notice\">!</span></div>";
         }*/

        result += "</form>";

        return result;
    }

    /**
     * Returns an HTML String containing the form element to change the port
     * number of a plugin name by the brokerURL.
     *
     * @param brokerURL the URL where the form will post its data.
     * @param name the name of the service or plugin where the action will be
     * performed.
     * @param port the current plugin or service port number.
     * @return an HTML String containing the form element to change the port
     * number of a plugin name by the brokerURL.
     */
    private String getHTMLPortForm(String brokerURL, String name, int port) {
        String result = "<form action=\"" + brokerURL + "\" method=\"get\"><input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SET_PORT + "\" /><input type=\"hidden\" name=\"" + PARAM_SERVICE + "\" value=\"" + name + "\" />";

        result += "<label for=\"" + PARAM_PORT + "-" + Utils.getHTMLElementIDFromString(name) + "\">";
        result += "Port: " + "<input id=\"" + PARAM_PORT + "-" + Utils.getHTMLElementIDFromString(name) + "\" type=\"number\" name=\"" + PARAM_PORT + "\" min=\"0\" max=\"65535\" maxlength=\"5\" size=\"5\" value=\"" + port + "\" class=\"input-small\" style=\"margin-right: 10px;\"/>";
        result += "<input type=\"submit\" class=\"btn\" value=\"Set Port\" class=\"btn\" />";
        result += "</label>";

        // add a little warning for stopping the web server, making it unable to be used remotely again
		/*if (name.equalsIgnoreCase(webServerName))
         {
         result += "<div class=\"warning\"><span class=\"notice\">!</span></div>";
         }*/

        result += "</form>";

        return result;
    }

    /**
     * Returns an HTML String containing the form element to see and modify the
     * advanced/internal options of a plugin or service name by the brokerURL.
     *
     * @param brokerURL the URL where the form will post its data.
     * @param name the name of the service or plugin where the action will be
     * performed.
     * @return an HTML String containing the form element to see and modify the
     * advanced/internal options of a plugin or service name by the brokerURL.
     */
    @Deprecated
    private String getHTMLAdvancedOptionForm(String brokerURL, String name) {
        String result = "<form action=\"" + brokerURL + "\" method=\"get\"><input type=\"hidden\" name=\"" + PARAM_SERVICE + "\" value=\"" + name + "\" />";

        result += "<input type=\"submit\" class=\"btn\" value=\"Advanced Options\" class=\"btn btn-info\" />";

        result += "</form>";

        return result;
    }

    /**
     * Returns a String containing the HTML code for a row to the HTML service
     * management table, based o the parameters passed to it.
     *
     * @param isRunning if the target service is currently running.
     * @param brokerURL the url of the service broker.
     * @param name the name of the target service.
     * @param canBeDisabled if the plugins or service can be disabled from
     * starting/stoping.
     * @param startInitially if the plugin or service is currently set to be
     * start on server initialization.
     * @param hasPort if the plugin or service has a port number that can be
     * changed.
     * @param port the current port number of the plugin or service.
     * @param hasAdvancedOptions if the pugin or service has advanced (internal)
     * options.
     * @param advancedOptionsManagerURL the URL of the pugin or service advanced
     * options manager.
     * @return a String containing the HTML code for a row to the HTML service
     * management table, based o the parameters passed to it.
     */
    @Deprecated
    private String getHTMLServiceManagementTableRow(boolean isRunning, String brokerURL, String name, boolean canBeStopped, boolean canBeDisabled, boolean startInitially, boolean hasPort, int port, boolean hasAdvancedOptions, String advancedOptionsManagerURL) {
        String result = "<tr " + (name.equalsIgnoreCase(webServerName) ? "class=\"warning\"" : "") + ">"; // add a little warning for stopping the web server, making it unable to be used remotely again

        result += "<td>" + escapeHtml4(name) + "</td>";
        if (isRunning) {
            result += "<td class=\"running\">Running</td>";
            if (canBeStopped) {
                result += "<td>" + getHTMLActionForm(brokerURL, "Stop", name) + "</td>";
            } else {
                result += "<td></td>";
            }
        } else {
            result += "<td class=\"stopped\">Stopped</td>";
            if (canBeStopped) {
                result += "<td>" + getHTMLActionForm(brokerURL, "Start", name) + "</td>";
            } else {
                result += "<td>" + "<div class=\"error\"></div>" + getHTMLActionForm(brokerURL, "Start", name) + "</td>"; // add a little error notification, so that the admin can see that the plugin failed to start
            }
        }
        result += "<td>" + getHTMLStartOnInitForm(brokerURL, name, startInitially, canBeDisabled) + "</td>";
        if (hasPort) {
            result += "<td>" + getHTMLPortForm(brokerURL, name, port) + "</td>";
        } else {
            result += "<td></td>";
        }
        if (hasAdvancedOptions) {
            result += "<td>" + getHTMLAdvancedOptionForm(advancedOptionsManagerURL, name) + "</td>";
        } else {
            result += "<td></td>";
        }

        result += "</tr>";

        return result;
    }

    /**
     * Returns an HTML String containing the list of all plugins and services
     * available, their status and action that can be taken.
     *
     * @param brokerURL the URL of the service manager page (where the form will
     * post data to).
     * @param advancedOptionsManagerURL the URL of the plugin/service
     * advanced/internal options manager.
     * @param id the ID of this HTML element, can be null if not needed.
     * @return an HTML String containing the list of all plugins and services
     * available, their status and action that can be taken.
     */
    @Deprecated
    public String getHTMLServiceManagementTable(String brokerURL, String advancedOptionsManagerURL, String id) {
        String result = "";
        if ((id == null) || id.trim().isEmpty()) {
            result += "<table class=\"table table-striped\">";
        } else {
            result += "<table id=\"" + id + "\" class=\"table table-striped\">";
        }
        result += "<thead>";
        result += "<tr>";
        result += "<th>Name</th>";
        result += "<th>Status</th>";
        result += "<th>Actions</th>";
        result += "<th>Start on Init</th>";
        result += "<th>Options</th>";
        result += "<th>Advanced Options</th>";
        result += "</tr>";
        result += "</thead>";
        result += "<tbody>";

        // list all the plugins
        //TODO: DELETED
        /*for (String plug : plgs.getPluginsNames()) {
            result += getHTMLServiceManagementTableRow(plgs.isPluginRunning(plug), brokerURL, plug, true, true, cfgs.getAutoStartPlugin(plug), false, 0, plgs.hasAdvancedSettings(plug), advancedOptionsManagerURL);
        }*/
        // list the storage service
        result += getHTMLServiceManagementTableRow(svcs.storageIsRunning(), brokerURL, storageName, true, true, storageStart(), true, storagePort(), false, /*advancedOptionsManagerURL*/ null);
        // list the query retrieve service
        result += getHTMLServiceManagementTableRow(svcs.queryRetrieveIsRunning(), brokerURL, queryRetrieveName, true, true, queryRetrieveStart(), true, queryRetrievePort(), true, advancedOptionsManagerURL);
        // list the web services
        //result += getHTMLServiceManagementTableRow(false, brokerURL, webServicesName, false, false, false, true, webServicesPort(), false, null);
        // list the web server
        result += getHTMLServiceManagementTableRow(svcs.webServerIsRunning(), brokerURL, webServerName, true, true, webServerStart(), true, webServerPort(), false, null);
        // list the RMI service
        result += getHTMLServiceManagementTableRow(true, brokerURL, remoteGUIName, false, false, true, true, remoteGUIPort(), true, advancedOptionsManagerURL); // FIXME actually get if the RMI is running or not

        result += "</tbody>";
        result += "</table>";

        return result;
    }

    /**
     * Returns an HTML String containing the list of all plugins and services
     * available, their status and action that can be taken.
     *
     * @param brokerURL the URL of the service manager page (where the form will
     * post data to).
     * @param advancedOptionsManagerURL the URL of the plugin/service
     * advanced/internal options manager.
     * @return an HTML String containing the list of all plugins and services
     * available, their status and action that can be taken.
     */
    @Deprecated
    public String getHTMLServiceManagementTable(String brokerURL, String advancedOptionsManagerURL) {
        return getHTMLServiceManagementTable(brokerURL, advancedOptionsManagerURL, null);
    }

    /**
     * Return if the Storage service is started initially.
     *
     * @return if the Storage service is started initially.
     */
    public boolean storageStart() {
        return cfgs.isStorage();
    }

    /**
     * Return if the QueryRetrieve service is started initially.
     *
     * @return if the QueryRetrieve service is started initially.
     */
    public boolean queryRetrieveStart() {
        return cfgs.isQueryRetrive();
    }

    /**
     * Return if the WebServices service is started initially.
     *
     * @return if the WebServices service is started initially.
     */
    public boolean webServicesStart() {
        return cfgs.getWeb().isWebServices();
    }

    /**
     * Return if the WebServer service is started initially.
     *
     * @return if the WebServer service is started initially.
     */
    public boolean webServerStart() {
        return cfgs.getWeb().isWebServer();
    }

    /**
     * Sets if the Storage service is started initially.
     *
     * @param start if the Storage service is started initially.
     */
    public void setStorageStart(boolean start) {
        cfgs.setStorage(start);
    }

    /**
     * Sets if the QueryRetrieve service is started initially.
     *
     * @param start if the QueryRetrieve service is started initially.
     */
    public void setQueryRetrieveStart(boolean start) {
        cfgs.setQueryRetrive(start);
    }

    /**
     * Sets if the WebServices service is started initially.
     *
     * @param start if the WebServices service is started initially.
     */
    public void setWebServicesStart(boolean start) {
        cfgs.getWeb().setWebServices(start);
    }

    /**
     * Sets if the WebServer service is started initially.
     *
     * @param start if the WebServer service is started initially.
     */
    public void setWebServerStart(boolean start) {
        cfgs.getWeb().setWebServer(start);
    }

    /**
     * Returns the Storage service port.
     *
     * @return the Storage service port.
     */
    public int storagePort() {
        return cfgs.getStoragePort();
    }

    /**
     * Returns the QueryRetrieve service port.
     *
     * @return the QueryRetrieve service port.
     */
    public int queryRetrievePort() {
        return cfgs.getWlsPort();
    }

    /**
     * Returns the WebServices service port.
     *
     * @return the WebServices service port.
     */
    public int webServicesPort() {
        return cfgs.getWeb().getServicePort();
    }

    /**
     * Returns the WebServer service port.
     *
     * @return the WebServer service port.
     */
    public int webServerPort() {
        return cfgs.getWeb().getServerPort();
    }

    /**
     * Returns the RemoteGUI service port.
     *
     * @return the RemoteGUI service port.
     */
    public int remoteGUIPort() {
        return cfgs.getRemoteGUIPort();
    }

    /**
     * Returns the RemoteGUI service external IP.
     *
     * @return the RemoteGUI service external IP.
     */
    public String remoteGUIExtIP() {
        return cfgs.getRGUIExternalIP();
    }

    /**
     * Sets the Storage service port.
     *
     * @param port the Storage service port.
     */
    public void setStoragePort(int port) {
        cfgs.setStoragePort(port);
    }

    /**
     * Sets the QueryRetrieve service port.
     *
     * @param port the QueryRetrieve service port.
     */
    public void setQueryRetrievePort(int port) {
        cfgs.setWlsPort(port);
    }

    /**
     * Sets the WebServices service port.
     *
     * @param port the WebServices service port.
     */
    public void setWebServicesPort(int port) {
        cfgs.getWeb().setServicePort(port);
    }

    /**
     * Sets the WebServer service port.
     *
     * @param port the WebServer service port.
     */
    public void setWebServerPort(int port) {
        cfgs.getWeb().setServerPort(port);
    }

    /**
     * Sets the RemoteGUI service port.
     *
     * @param port the RemoteGUI service port.
     */
    public void setRemoteGUIPort(int port) {
        cfgs.setRemoteGUIPort(port);
    }

    /**
     * Sets the RemoteGUI service external IP.
     *
     * @param extIP the RemoteGUI service external IP.
     */
    public void setRemoteGUIExtIP(String extIP) {
        cfgs.setRGUIExternalIP(extIP);
    }

    /**
     * Saves the settings for use between server inits.
     */
    public void saveSettings() {
        
        xmlSupport.printXML();
    }

    /**
     * For a request, returns the name of the service/plugin required.
     *
     * @param request the servlet request object.
     * @return the name of the service/plugin required.
     * @throws IOException
     */
    public String getRequestServiceName(HttpServletRequest request) throws IOException {
        // set proper encoding on the request
        request.setCharacterEncoding("UTF-8");

        // get the name of the service/plugin
        return request.getParameter(PARAM_SERVICE);
    }

    public String getHTMLSettingHelp(String fieldTitle, String help) {
        String result = "";

        // make sure that there is a valid help notice
        if (help == null) {
            return result;
        }
        help = help.trim();
        if (help.isEmpty()) {
            return result;
        }

        // replace the "\n" (#13/#10) with "\\n" so that JS can interpret that correctly instead of ending up with a new line on the document
        //help = help.replaceAll("\n", "\\\\n");
        // also the "\t" ones
        //help = help.replaceAll("\t", "\\\\t");

        // add a button that will show the help (button because of touch interfaces, instead of popup for desktop)
        System.out.println("HELP: "+help);
        String msg = escapeHtml4(help.replaceAll("\n", "<br>"));
        result += buildInfoButton(fieldTitle, msg);
        		System.out.println("MSG: "+msg);
        return result;
    }
    
   private String buildInfoButton(String title, String msg){
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append("<a ");
    	//builder.append(id);
    	builder.append(" data-original-title=\"");
    	builder.append(title);
    	builder.append("\" href=\"#\" class=\"btn btn-mini btn-info\" data-toggle=\"popover\" data-html=\"true\" data-content=\"");
    	builder.append(msg);
    	builder.append("\" onclick=\"return false;\">Info</a>\n");
    	
    	return builder.toString();
    }
    

    /**
     * Returns a row for the advanced settings form.
     *
     * @param name the name of the setting.
     * @param value the type/value of the setting.
     * @param help s String containing a help notice for this setting, can be
     * null if not needed.
     * @return a String containing a HTML code to a row for the advanced
     * settings form.
     */
    public String getHTMLAdvancedSettingsFormRow(String name, Object value, String help) {
    	
        String result = "";

        String id = Utils.getHTMLElementIDFromString(name);

        result += "<tr>";

        result += "<td><label for=\"" + id + "\">" + escapeHtml4(name) + ":</label></td>";
        result += "<td>" + Utils.getHTMLInputFromType(id, value) + getHTMLSettingHelp(escapeHtml4(name) , help) + "</td>"; // TODO add a button with help, like in the desktop application [getHelpForParam(pluginName, paramName) != null]

        result += "</tr>";

        return result;
    }

    /**
     * Based on the request, returns a String containing a form with all the
     * settings inputs boxes. These boxes are rendered/specified in accordance
     * with the each setting value type reported by the plugin/service.
     *
     * @param request the servlet request object.
     * @param brokerURL the URL of the broker that will apply the settings after
     * receiving this forms post.
     * @param elementID the ID of this HTML form, can be null.
     * @return a String containing the HTML structure for the form with all the
     * plugin advanced setting.
     */
    public String getHTMLServiceAdvancedSettingsForm(HttpServletRequest request, String brokerURL, String elementID) throws IOException {
        String result = "";

        String name = getRequestServiceName(request);

        // test if this is a embeded service and has advanced settings
        boolean isStorage = storageName.equalsIgnoreCase(name);
        boolean isQueryRetrieve = queryRetrieveName.equalsIgnoreCase(name);
        boolean isRemoteGUI = remoteGUIName.equalsIgnoreCase(name);
        boolean isEmbededService = isStorage || isQueryRetrieve || isRemoteGUI;

        // if it's not a embeded service
        if (!isEmbededService) {
            // test if it's a plugin and has advanced settings
            if (!plgs.hasAdvancedSettings(name)) {
                result += "<h3>The required plugin/service is either invalid or has no advanced settings!</h3>";
                return result;
            }
        }

        if ((elementID == null) || elementID.trim().isEmpty()) {
            result += "<form ";
        } else {
            result += "<form id=\"" + elementID + "\" ";
        }
        result += "action=\"" + brokerURL + "\" method=\"get\">";

        result += "<input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SET_ADVANCED_SETTINGS + "\" />";
        result += "<input type=\"hidden\" name=\"" + PARAM_SERVICE + "\" value=\"" + escapeHtml4(name) + "\" />";

        result += "<table class=\"table table-hover\"><tbody>";

        HashMap<String, Object> settings = null;
        HashMap<String, String> settingsHelp = null;
        if (!isEmbededService) // if it's a plugin
        {
        	//TODO: DELETED
            //settings = plgs.getAdvancedSettings(name);
            settingsHelp = plgs.getAdvancedSettingsHelp(name);
        } else {
            if (isStorage) {
                settings = cfgs.getStorageSettings();
                settingsHelp = cfgs.getStorageSettingsHelp();
            } else if (isQueryRetrieve) {
                settings = cfgs.getQueryRetrieveSettings();
                settingsHelp = cfgs.getQueryRetrieveSettingsHelp();
            } else // Remote GUI
            {
                settings = cfgs.getRGUISettings();
                settingsHelp = cfgs.getRGUISettingsHelp();
            }
        }
        // just to avoid any unwanted exceptions, in case a plugin misbehaves
        if (settings == null) {
            settings = new HashMap<String, Object>();
        }
        if (settingsHelp == null) {
            settingsHelp = new HashMap<String, String>();
        }

        // create a table row for each setting (includes name, value/type and help, if available)
        for (Map.Entry<String, Object> setting : settings.entrySet()) {
            String key = setting.getKey();
            Object value = setting.getValue();
            String help = settingsHelp.get(key);

            result += getHTMLAdvancedSettingsFormRow(key, value, help);
        }

        result += "</tbody></table><br />";

        result += "<input type=\"submit\" value=\"Apply Settings\" class=\"btn btn-primary\"/>";
        result += "</form>";

        return result;
    }
}
