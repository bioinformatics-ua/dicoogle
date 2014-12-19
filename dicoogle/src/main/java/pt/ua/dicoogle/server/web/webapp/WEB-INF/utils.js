/**
 * Some helper functions, client side.
 */

/**
 * Returns a element with all the named params and their value on the current QueryString (URL).
 *
 * Based on http://stackoverflow.com/a/1099670
 */
function getQueryParams(qs)
{
	var params = {};

	qs = qs.split("+").join("%20"); // replace "+" with "%20", encoded space " ", a bit faster than using .replace()
	var re = /[?&]?([^=]+)=([^&]*)/g; // regex to break the query string into param and value pairs

	var tokens;
	while (tokens = re.exec(qs))
	{
		var name = decodeURIComponent(tokens[1]);
		var value = decodeURIComponent(tokens[2]);

		params[name] = value;
	}

	return params;
}

/**
 * DataTable operations.
 */

function resetDataTableRow(rowDiv)
{
	// enable the remove row button
	var buttons = rowDiv.getElementsByTagName("button");
	for (var i = 0; i < buttons.length; i++)
	{
		var button = buttons[i];
		if (button.getAttribute("class") == "removeButton")
		{
			button.removeAttribute("hidden");
			break;
		}
	}

	// clear all the text box input contents
	var inputs = rowDiv.getElementsByTagName("input");
	for (i = 0; i < inputs.length; i++)
	{
		var input = inputs[i];
		if (input.getAttribute("type") == "text")
			input.value = "";
	}
}

function addDataTableRow(formDiv)
{
	var rowDivs = formDiv.getElementsByTagName("div");
	var firstRow = null;
	for (var i = 0; i < rowDivs.length; i++)
	{
		if (rowDivs[i].getAttribute("class") == "data-table-row")
		{
			firstRow = rowDivs[i];
			break;
		}
	}

	var copy = firstRow.cloneNode(true);

	resetDataTableRow(copy);

	formDiv.appendChild(copy);
}

function removeDataTableRow(formDiv, rowDiv)
{
	formDiv.removeChild(rowDiv);
}

/**
 * ServerDirectoryPath operations.
 */

function parseDirectoryListXML(xmlDoc, dirList, currentDir)
{
	var contents = xmlDoc.getElementsByTagName("contents");
	if (! contents)
		return;

	var dir = contents[0].getAttribute("path");
	var parent = contents[0].getAttribute("parent");
	var children = xmlDoc.getElementsByTagName("directory");

	// clear dir list
	clearOptions(dirList);
	// add ".." element, that goes to parent
	if (dir)
	{
		currentDir.value = dir;
		appendOption(dirList, "..", parent);
	}

	// add each child dir to the list, if there is any
	if (children)
		//for (var child in children)
		for (var i = 0; i < children.length; i++)
		{
			var child = children[i];

			var name = child.getAttribute("name");
			var value = child.getAttribute("path");

			appendOption(dirList, name, value);
		}
}

function requestDirectoryList(xhrObject, forParentDir, dirList, currentDir)
{
	if (xhrObject)
	{
		xhrObject.open("POST", "indexer");
		xhrObject.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

		xhrObject.onreadystatechange = function ()
		{
			if((xhrObject.readyState == 4) && (xhrObject.status == 200))
				parseDirectoryListXML(xhrObject.responseXML, dirList, currentDir);
		}

		//var params = "<%= IndexerServlet.PARAM_ACTION %>=<%= IndexerServlet.ACTION_GET_PATH_CONTENTS %>&<%= IndexerServlet.ACTION_PARAM_PATH %>=" + encodeURIComponent(forParentDir);
		var params = "action=pathcontents&path=" + encodeURIComponent(forParentDir);
		params = params.replace(/%20/g, "+");

		xhrObject.send(params);
	}
}

// when a directory is double clicked
function onOptionDoubleClicked(xhrObject, dirList, currentDir)
{
	// get the selected directory
	var dir = unescape(dirList.options[dirList.selectedIndex].value);
	// ask the server for the selected directory contents, parse the contents, and add them to the directory list
	requestDirectoryList(xhrObject, dir, dirList, currentDir);
}

function showDirList(dirList, currentDir, callerButton)
{
	if (callerButton && (! currentDir.hasAttribute("originalpath")))
	{
		var xhrObject = getXHRO();

		requestDirectoryList(xhrObject, currentDir.value, dirList, currentDir);

		dirList.ondblclick = function ()
		{
			onOptionDoubleClicked(xhrObject, dirList, currentDir);
		};

		dirList.style.display = "inline";

		// save the previous/original path
		currentDir.setAttribute("originalpath", currentDir.value);

		//callerButton.disabled = true;
		callerButton.innerHTML = "<i class=\"icon-remove\"></i> Cancel";
	}
	else
	{
		dirList.style.display = "none";

		// recover the previous/original path
		currentDir.value = currentDir.getAttribute("originalpath");
		currentDir.removeAttribute("originalpath");

		//callerButton.disabled = true;
		callerButton.innerHTML = "<i class=\"icon-folder-open\"></i> Browse...";
	}
}

/**
 * Manages DOM elements classes.
 */

function hasClass(element/*ID*/, className)
{
	//var element = document.getElementById(elementID);

	return ((" " + element.className + " ").indexOf(" " + className + " ") != -1);
}

function addClass(element/*ID*/, className)
{
	//var element = document.getElementById(elementID);

	if (! hasClass(element, className)) // if the element does not have the desired class
	{
		element.className += " " + className;
	}
}

function removeClass(element/*ID*/, className)
{
	//var element = document.getElementById(elementID);

	element.className = element.className.replace(new RegExp("\\b" + className + "\\b"), "");
}