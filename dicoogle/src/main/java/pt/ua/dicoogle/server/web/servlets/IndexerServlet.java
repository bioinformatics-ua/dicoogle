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
package pt.ua.dicoogle.server.web.servlets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.settings.Utils;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.auth.Session;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import pt.ua.dicoogle.plugins.PluginController;

/**
 * Provides indexing start and stop requests (scan path). Also handles requests
 * for both indexing progress/status and server path contents.
 * 
 * @author António Novo <antonio.novo@ua.pt>
 */
@Deprecated
public class IndexerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PARAM_ACTION = "action";

	public static final String ACTION_START_INDEXING = "start";
	public static final String ACTION_STOP_INDEXING = "stop";
	public static final String ACTION_GET_STATUS = "status";
	public static final String ACTION_SET_INDEXING_PATH = "setpath";
	public static final String ACTION_SET_ADVANCED_SETTINGS = "setadvancedsettings";
	public static final String ACTION_GET_PATH_CONTENTS = "pathcontents";
	public static final String ACTION_PARAM_PATH = "path";

	/*
	 * Action codes for internal use.
	 */
	private static final int ACTION_CODE_INVALID = 0;
	private static final int ACTION_CODE_START_INDEXING = 1;
	private static final int ACTION_CODE_STOP_INDEXING = 2;
	private static final int ACTION_CODE_GET_STATUS = 3;
	private static final int ACTION_CODE_SET_INDEXING_PATH = 4;
	private static final int ACTION_CODE_SET_ADVANCED_SETTINGS = 5;
	private static final int ACTION_CODE_GET_PATH_CONTENTS = 6;

	/**
	 * A file filter to allow the listing of directories only.
	 */
	private static FileFilter onlyDirectories = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	private List<Task<Report>> ongoingTasks;

	public IndexerServlet() {
		this.ongoingTasks = null;
	}

	/**
	 * Returns a XML document in String form containing the list of child
	 * directories of the specified path.
	 * 
	 * @param path
	 *            the path of the directory to retrieve the child directories
	 *            of.
	 * @return a XML document in String form containing the list of child
	 *         directories of the specified path.
	 */
	public static String getPathContents(String path) {
		File dir = null;
		if (path != null)
			dir = new File(path);

		// check if the specified path is a valid one, if not revert to "roots"
		if ((path == null) || path.trim().isEmpty() || (dir == null)
				|| (!dir.exists()) || (!dir.isDirectory()))
			path = "";
		else
			path = dir.getAbsolutePath();

		// guarantee that the parent path is always a valid one (never null)
		String parentPath = "";
		File[] childs = null;
		if (path.isEmpty()) {
			// return "roots"
			childs = File.listRoots();
		} else {
			if (dir.getParent() != null) {
				File parent = new File(dir.getParent());
				parentPath = parent.getAbsolutePath();
			}
			// list the dir children
			childs = dir.listFiles(onlyDirectories);
		}

		// create the XML string builder and open the xml document
		StringBuilder xml = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xml.append("<contents path=\"");
		xml.append(escapeHtml4(path));
		xml.append("\" parent=\"");
		xml.append(escapeHtml4(parentPath));
		xml.append("\">");

		// loop through all the cildren and addMoveDestination their path name to the XML tree
		if (childs != null) {
			for (File child : childs) {
				String cName = child.getName();
				String cPath = child.getAbsolutePath();
				if ((cName == null) || cName.isEmpty())
					cName = cPath;

				xml.append("<directory name=\"");
				xml.append(escapeHtml4(cName));
				xml.append("\" path=\"");
				xml.append(escapeHtml4(cPath));
				xml.append("\" />");
			}
		}

		// close the document
		xml.append("</contents>");

		// return the formed XML string
		return xml.toString();
	}

	/**
	 * Returns a XML document in String form containing the status of the
	 * indexing.
	 * 
	 * @return a XML document in String form containing the status of the
	 *         indexing. TODO: fix
	 */
	public String getIndexingStatus() {
		boolean isIndexing = false;
		int percentCompleted = 0;

		if (this.ongoingTasks != null) {
			System.out.println("### Status ###");
			System.out.println("### Number of Tasks : "
					+ this.ongoingTasks.size());
			float tempProgess = 0;
			for (Task<Report> task : this.ongoingTasks) {
				if (!task.isDone())
					isIndexing = true;
				System.out.println("##### $ Progress: " + task.getProgress());
				System.out.println("##### $ Completed: " + task.isDone());
				tempProgess += task.getProgress();
			}
			percentCompleted = (int) (tempProgess / this.ongoingTasks.size() * 100);
			if (isIndexing == false)
				this.ongoingTasks = null;
		}

		// create the XML string builder and open the xml document
		StringBuilder xml = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xml.append("<status running=\"");
		xml.append(Boolean.toString(isIndexing));
		xml.append("\">");

		// addMoveDestination percentage information
		xml.append("<percent completed=\"");
		xml.append(percentCompleted);
		xml.append("\" />");

		// close the document
		xml.append("</status>");

		// return the formed XML string
		return xml.toString();
	}

	/**
	 * Writes the specified XML document in String form to a HttpServletResponse
	 * object.
	 * 
	 * @param xml
	 *            a XML document in String form.
	 * @param response
	 *            a HttpServletResponse.
	 * @throws IOException
	 *             if a error has occurred while writing the response.
	 */
	private static void writeXMLToResponse(String xml,
			HttpServletResponse response, boolean allowCache)
			throws IOException {
		// get the returned xml as a UTF-8 byte array
		byte[] data = xml.getBytes("UTF-8");
		if (data == null) {
			response.sendError(500,
					"Could generate the resulting XML document!");
			return;
		}

		if (!allowCache) {
			response.addHeader("Cache-Control", "no-cache, must-revalidate");
			response.addHeader("Pragma", "no-cache");
		}

		response.setContentType("application/xml"); // set the appropriate type
													// for the XML file
		response.setContentLength(data.length); // set the document size
		// response.setCharacterEncoding("UTF-8"); // set the apropriate
		// encoding type

		// write the XML data to the response output
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO validate user session credentials

		// get which action to take
		String actionStr = request.getParameter(PARAM_ACTION);

		// translate each action string to a action identification
		int action = ACTION_CODE_INVALID;
		if (actionStr != null) {
			if (actionStr.equalsIgnoreCase(ACTION_START_INDEXING))
				action = ACTION_CODE_START_INDEXING;
			else if (actionStr.equalsIgnoreCase(ACTION_STOP_INDEXING))
				action = ACTION_CODE_STOP_INDEXING;
			else if (actionStr.equalsIgnoreCase(ACTION_GET_STATUS))
				action = ACTION_CODE_GET_STATUS;
			else if (actionStr.equalsIgnoreCase(ACTION_SET_INDEXING_PATH))
				action = ACTION_CODE_SET_INDEXING_PATH;
			else if (actionStr.equalsIgnoreCase(ACTION_SET_ADVANCED_SETTINGS))
				action = ACTION_CODE_SET_ADVANCED_SETTINGS;
			else if (actionStr.equalsIgnoreCase(ACTION_GET_PATH_CONTENTS))
				action = ACTION_CODE_GET_PATH_CONTENTS;
		}

		// response to each action accordingly
		// fix this!
		switch (action) {
		case ACTION_CODE_START_INDEXING:
			System.err.println("Started Indexing!!");

			String thepath = ServerSettingsManager.getSettings().getArchiveSettings().getMainDirectory();
			File f = new File(thepath);
			URI uri = f.toURI();

			if (uri != null) {
				System.out.println("URI: " + uri.toString());
				List<Task<Report>> report = PluginController.getInstance().index(uri);
				System.out.println("Report Length: " + report.size());
				if (this.ongoingTasks == null)
					this.ongoingTasks = report;
				else
					System.out.println("More than one task in queue");
			} else
				System.out.println("Faulty");
			// send the client back the to previous page
			response.sendRedirect(Session.getLastVisitedURL(request));
			break;

		case ACTION_CODE_STOP_INDEXING:
			// idx.stopIndexing();
			// send the client back the to previous page

			// Cancelling all Tasks
			if (this.ongoingTasks != null) {
				for (Task<Report> t : this.ongoingTasks)
					t.cancel(true);
			}

			response.sendRedirect(Session.getLastVisitedURL(request));
			break;

		case ACTION_CODE_GET_STATUS:
			// get the XML document containing contents of the requested
			// directory path
			writeXMLToResponse(getIndexingStatus(), response, false);
			break;

		case ACTION_CODE_SET_INDEXING_PATH:
			String path = request.getParameter(ACTION_PARAM_PATH);
			if ((path == null) || (path.isEmpty())) {
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
						"Invalid path parameter specified!");
				return;
			}

			// send the client back the to previous page
			response.sendRedirect(Session.getLastVisitedURL(request));
			break;

		case ACTION_CODE_SET_ADVANCED_SETTINGS:
			HashMap<String, String[]> advSettings = new HashMap<String, String[]>();

			// get all the settings and their values
			Enumeration<String> params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = params.nextElement();
				// ignore the main params (the ones that go us here)
				if (name.equalsIgnoreCase(PARAM_ACTION))
					continue;
				String[] value = request.getParameterValues(name);
				advSettings.put(name, value);
			}

			// HashMap<String, Object> settings = idx.getSettings();
			// Services svcs = Services.getSettings();
			// svcs.processAdvancedSettings(settings, advSettings);

			// try to apply the settings
			/*
			 * if (idx.trySettings(settings)){ idx.setSettings(settings);
			 * svcs.saveSettings(); // send the client back the to previous page
			 * response.sendRedirect(Session.getLastVisitedURL(request)); } else
			 */
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid parameters!");
			break;

		case ACTION_CODE_GET_PATH_CONTENTS:
			path = request.getParameter(ACTION_PARAM_PATH);

			// get the XML document containing contents of the requested
			// directory path
			writeXMLToResponse(getPathContents(path), response, false);
			break;

		default:
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid action!");
			return;
		}
	}

	/**
	 * Based on the request, returns a String containing a form with all the
	 * settings inputs boxes. These boxes are rendered/specified in accordance
	 * with the each setting value type reported by the plugin/service.
	 * 
	 * @param request
	 *            the servlet request object.
	 * @param brokerURL
	 *            the URL of the broker that will apply the settings after
	 *            receiving this forms post.
	 * @param elementID
	 *            the ID of this HTML form, can be null.
	 * @return a String containing the HTML structure for the form with all the
	 *         plugin advanced setting.
	 */
	public static String getHTMLServiceAdvancedSettingsForm(
			HttpServletRequest request, String brokerURL, String elementID)
			throws IOException {
		String result = "";

		if ((elementID == null) || elementID.trim().isEmpty())
			result += "<form ";
		else
			result += "<form id=\"" + elementID + "\" ";
		result += "action=\"" + brokerURL + "\" method=\"get\">";

		result += "<input type=\"hidden\" name=\"" + PARAM_ACTION
				+ "\" value=\"" + ACTION_SET_ADVANCED_SETTINGS + "\" />";

		result += "<table class=\"table table-hover\"><tbody>";

		// HashMap<String, Object> settings = idx.getSettings();
		// HashMap<String, String> settingsHelp = idx.getSettingsHelp();

		HashMap<String, Object> settings = new HashMap<>();
		HashMap<String, String> settingsHelp = new HashMap<>();

		// create a table row for each setting (includes name, value/type and
		// help, if available)
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

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doGet(request, response);
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
	private static String getHTMLAdvancedSettingsFormRow(String name, Object value, String help) {

		String result = "";

		String id = Utils.getHTMLElementIDFromString(name);

		result += "<tr>";

		result += "<td><label for=\"" + id + "\">" + escapeHtml4(name) + ":</label></td>";
		result += "<td>" + Utils.getHTMLInputFromType(id, value) + getHTMLSettingHelp(escapeHtml4(name) , help) + "</td>"; // TODO addMoveDestination a button with help, like in the desktop application [getHelpForParam(pluginName, paramName) != null]

		result += "</tr>";

		return result;
	}

	private static String getHTMLSettingHelp(String fieldTitle, String help) {
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

		// addMoveDestination a button that will show the help (button because of touch interfaces, instead of popup for desktop)
		System.out.println("HELP: "+help);
		String msg = escapeHtml4(help.replaceAll("\n", "<br>"));
		result += buildInfoButton(fieldTitle, msg);
		System.out.println("MSG: "+msg);
		return result;
	}

	private static String buildInfoButton(String title, String msg){
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

}
