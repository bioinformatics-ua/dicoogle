/**
 * Provides a simple way of obtaining the Dicoogle indexing status on a page.
 * It does so by alternating between two timers, different intervals on each
 * to reduce the server load of the queries, that "constantly" request the
 * indexing status from the server, and report it to a function defined on the
 * page:
 *	recievedCurrentindexingStatus(isIndexing, progressPercentage)
 */

var xhrObject = getXHRO();
var statusRequestInterval;

function manageTimers(isIndexing, isFastRequest)
{
	if ((! isIndexing) && isFastRequest)
	{
		startStatusRequestChain(false);
	}
	else if (isIndexing && (! isFastRequest))
	{
		startStatusRequestChain(true);
	}
}

function parseIndexingStatusXML(xmlDoc, isFastRequest)
{
	var running = xmlDoc.getElementsByTagName("status")[0].getAttribute("running");
	var completed = xmlDoc.getElementsByTagName("percent")[0].getAttribute("completed");

	var isIndexing = (running === "true");
	var progress = parseInt(completed);

	// manage the timers based on isSlowRequest and the current status
	manageTimers(isIndexing, isFastRequest);

	recievedCurrentindexingStatus(isIndexing, progress);
}

function requestIndexingStatus(isFastRequest)
{
	if (xhrObject)
	{
		//xhrObject.open("GET", "indexer?<%= IndexerServlet.PARAM_ACTION %>=<%= IndexerServlet.ACTION_GET_STATUS %>");
		xhrObject.open("GET", "indexer?action=status");

		xhrObject.onreadystatechange = function()
		{
			if((xhrObject.readyState == 4) && (xhrObject.status == 200))
				parseIndexingStatusXML(xhrObject.responseXML, isFastRequest);
		}

		xhrObject.send();
	}
}

function startStatusRequestChain(fast)
{
	if (statusRequestInterval)
		clearInterval(statusRequestInterval);

	//statusRequestTypeFast = fast;
	var interval = (fast ? 2000 : 10000); // number of milliseconds to wait till next request
	statusRequestInterval = setInterval(
		function()
		{
			// get current indexing progress from server, parse the
			// response xml and forward the information to the
			// requesting page
			requestIndexingStatus(fast);
		},
		interval // check server for indexing status every interval milliseconds
	);
}