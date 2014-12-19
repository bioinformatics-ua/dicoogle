/**
 * Some helper functions, client side, for HTML DOM Select Element.
 */

/**
 * Appends a new option to a Select element.
 */
function appendOption(select, text, value)
{
	// create a new option element
	var opt = document.createElement("option");

	// sets its properties
	opt.text = text;
	opt.value = value;

	// add it to the select element
	try
	{
		select.add(opt, null); // for regular browsers
	}
	catch (ex)
	{
		select.add(opt); // for IE
	}
}

/**
 * Removes all option elements from a select element.
 */
function clearOptions(select)
{
	select.options.length = 0;
}