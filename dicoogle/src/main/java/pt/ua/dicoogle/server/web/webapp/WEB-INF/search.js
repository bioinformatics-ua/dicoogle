/**
 * Some helper functions relative to the GUI of the search page.
 */

var table = document.getElementById("modalities");
var modalities = table.getElementsByTagName("input");
var checkAll = document.getElementById("checkAll");
var checkNone = document.getElementById("checkNone");

/**
 * Returns 0 for no modalities selected, 1 for 1 or more, and 2 for all.
 */
function modalitiesSelected()
{
	var total = 0;
	var selected = 0;

	for (var i = 0; i < modalities.length; i++)
	{
		total++;

		if (modalities[i].checked)
			selected++;
	}

	if (selected < 1)
		return 0;
	else
		if (total == selected)
			return 2;
		else
			return 1;
}

function modalitiesSelectAll()
{
	for (var i = 0; i < modalities.length; i++)
	{
		modalities[i].checked = true;
	}
}

function modalitiesSelectNone()
{
	for (var i = 0; i < modalities.length; i++)
	{
		modalities[i].checked = false;
	}
}

/**
 * Checks if the select all and none should be checked.
 */
function checkSelectedModalities()
{
	var checked = modalitiesSelected();

	checkAll.checked = (checked == 2);
	checkNone.checked = (checked == 0);
}

function modalityCheckBoxToggle()
{
	checkSelectedModalities();
}

// checks if the select all and none should be checked after page load
checkSelectedModalities();