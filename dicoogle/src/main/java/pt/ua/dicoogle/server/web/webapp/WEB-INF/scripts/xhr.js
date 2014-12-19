/**
 * Some helper function, client side, for XML HTTP Request objects.
 */

/**
 * Return a new XML HTTP Request object.
 */
function getXHRO()
{
	if (window.XMLHttpRequest) // for normal browsers
		return new XMLHttpRequest();
	else
		if (window.ActiveXObject) // for IE 6
			return new ActiveXObject("Microsoft.XMLHTTP");

	return null;
}