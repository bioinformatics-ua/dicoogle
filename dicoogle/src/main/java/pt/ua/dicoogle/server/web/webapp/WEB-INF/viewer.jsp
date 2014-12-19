<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>Dicoogle Web - Viewer</title>
		<%@include file="jspf/header.jspf" %>
	</head>
	<body>
		<%@include file="jspf/mainbar.jspf" %>
		<%@include file="jspf/needsLoginBegin.jspf" %>
		<div class="container-fluid">
			<!--canvas id="canvas1" width="500" height="500"></canvas-->
			<!--script type="text/javascript">
				var can = document.getElementById("canvas1");
				var ctx = can.getContext("2d");

				// for touch start

				function onTouchStart(event)
				{
					ctx.fillRect(0, 0, 300, 300);
				}

				can.addEventListener("touchstart", onTouchStart, false);

				// for mouse wheel scroll
	/*
				function wheelHandle(delta)
				{
					// erase previous message
					ctx.fillStyle = "white";
					ctx.fillRect(0, 0, 500, 500);

					// print new message
					ctx.fillStyle = "blue";
					ctx.font = "bold 16px Arial";
					ctx.textBaseline = "top";
					ctx.fillText("Scroll " + delta, 5, 5);
				}

				function onMouseWheel(event)
				{
					var delta = 0;

					if (! event) // IE fix
						event = can.event;

					if (event.wheelDelta) // for IE
					{
						delta = event.wheelDelta; // / 120;
					}
					else
					{
						if (event.detail) // for remaining browsers
						{
							delta = - event.detail;// / 3;
						}
					}

					// if the delta is not zero call the event handler (positive value for scroll up and negative for down)
					if (delta != 0)
						wheelHandle(delta);

					// prevent mouse wheel default action
					if (! event.preventDefault)
						event.returnValue = false; // for IE
					else
						event.preventDefault(); // for remaining browsers
				}

				if (! can.addEventListener)
					can.onmousewheel = onMouseWheel; // for IE
				else
					can.addEventListener("DOMMouseScroll", onMouseWheel, false); // for remaining browsers*/
			</script>
			<img src="" alt="coiso" />
			<script type="text/javascript">
				function preloader()
				{
					// counter
					var i = 0;

					// create object
					imageObj = new Image();

					// set image list
					images = new Array();
					images[0] = "image1.jpg"
					images[1] = "image2.jpg"
					images[2] = "image3.jpg"
					images[3] = "image4.jpg"

					// start preloading
					for(i = 0; i < 4; i++)
					{
						imageObj.src = images[i];
					}
				}
			</script-->
			<!--img id="viewer" src="" alt="coiso" /-->
			<canvas id="viewer" width="100" height="100"></canvas>
			<script type="text/javascript">
				/* async */
				/*function coiso(xmlDoc)
				{
					alert(xmlDoc);
				}

				var oReq = new XMLHttpRequest();
				oReq.open("GET", "dictags?SOPInstanceUID=1.2.840.1136190195280574824680000700.3.0.1.19970424140438", true);
				oReq.onreadystatechange = function (oEvent)
				{
					if (oReq.readyState === 4)
					{
						if (oReq.status === 200)
						{
							coiso(oReq.responseText);
						}
						else
						{
							alert("Error " + oReq.statusText);
						}
					}
				};
				oReq.send(null);*/

				var currentSOPInstanceUID;

				var tagsWindow;
				var tagsXML;

				var numberOfFrames = 1;
				var currentFrame = 0;

				var timer = null;
				var currentFrameRate = 15;

				var frames = new Array();

				var viewer = document.getElementById("viewer");
				var canvas = viewer.getContext("2d");

				function createTagsWindow()
				{
					tagsWindow = window.open(
						"",
						"DICOM MetaData",
						"width=800,height=480,menubar=0,toolbar=1,status=0,scrollbars=1,resizable=1"
					);
				}

				function showTags()
				{
					if (tagsXML == null)
					{
						alert("Unable to obtain the MetaData of this DICOM file!");
						return;
					}

					if ((tagsWindow == null) || (tagsWindow.closed))
						createTagsWindow();

					var newContent = "<!DOCTYPE html><html><head><meta http-equiv='content-type' content='text/html; charset=UTF-8'><title>DICOM MetaData</title></head><body>";

					var xmlDoc = tagsXML;

					// the the tags and insert each key,value pair into the window
					/*if (window.DOMParser) // for remaining browsers
					{
						var parser = new DOMParser();
						xmlDoc = parser.parseFromString(tagsXML, "text/xml");
					}
					else // for IE
					{
						xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
						xmlDoc.async = false;
						xmlDoc.loadXML(tagsXML);
					}*/

					var tags = xmlDoc.getElementsByTagName("tag");

					//var table = document.getElementById("tags");

					newContent += "<table style='width: 95%;'><thead><tr><th>Name</th><th>Value</th></tr></thead><tbody id='tags'>";

					for (var i = 0; i < tags.length; i++)
					{
						// obtain the values
						var name = tags[i].getAttribute("name");
						var value = "";
						if (tags[i].childNodes[0] != null)
							value = tags[i].childNodes[0].textContent;
						/*
						// create the new table row and its data cells
						var row = document.createElement("tr");
						var d1 = document.createElement("td");
						var d2 = document.createElement("td");

						// add the proper text to each cell/table-data
						d1.textContent = name;
						d2.textContent = value;

						// append the cells to the row
						row.appendChild(d1);
						row.appendChild(d2);

						// append the row to the table
						table.appendChild(row);*/
						newContent += "<tr><td>" + name + "</td><td>" + value + "</td></tr>";
					}

					newContent += "</tbody></table>";

					newContent += "</body></html>"

					tagsWindow.document.write(newContent);
					tagsWindow.document.close();

					tagsWindow.focus();
				}

				function processTags(tags)
				{
					if (tags == null)
					{
						alert("Unable to obtain the MetaData of this DICOM file!");
					}
					else
					{
						tagsXML = tags;
					}
				}

				function setCurrentSOPInstanceUID(sopInstanceUID)
				{
					currentSOPInstanceUID = sopInstanceUID;
				}

				function getTags(sopInstanceUID)
				{
					var request = new XMLHttpRequest();

					request.open("GET", "dictags?SOPInstanceUID=" + sopInstanceUID, false);
					request.send();

					if (request.status == 200)
						processTags(request.responseXML);

					setCurrentSOPInstanceUID(sopInstanceUID);
				}

				function getNumberOfFrames()
				{
					if (tagsXML == null)
					{
						alert("Unable to obtain the MetaData of this DICOM file!");
						return;
					}

					var tags = tagsXML.getElementsByTagName("tag");

					for (var i = 0; i < tags.length; i++)
					{
						// obtain the values
						var name = tags[i].getAttribute("name");

						if (name.toLowerCase() == "numberofframes")
						{
							var value = tags[i].childNodes[0].textContent;

							numberOfFrames = parseInt(value);

							return;
						}

						// could not get it, so default to 1
						numberOfFrames = 1;
					}
				}

				function setViewerSize(width, height)
				{
					// limit the viewer size to 100% image size
					viewer.style.maxWidth = width + "px";
					viewer.style.maxHeight = height + "px";

					// set the 100% size/scale
					viewer.setAttribute("width", width);
					viewer.setAttribute("height", height);
				}

				/**
				 * FIXME this is currently disabled because it doesn't work quite right
				 * extra scaling stuff must be implemented, and page scale lock must be removed for mobile devices, or they will get low res upscaled canvas
				 */
				//window.addEventListener("resize", resizeCanvas, false);

				function resizeCanvas()
				{
					// try to limit the size
					viewer.setAttribute("width", document.body.offsetWidth);
					viewer.setAttribute("height", document.body.offsetHeight);

					// show the first frame
					showFrame(currentFrame);
				}

				function showFrame(index)
				{
					//viewer.src = frames[index];
					var imageObj = new Image();
					imageObj.src = frames[index];
					canvas.drawImage(imageObj, 0, 0, viewer.getAttribute("width"), viewer.getAttribute("height"));
				}

				function showLoading()
				{
					canvas.font = "bold 20px sans-serif";
					canvas.fillText("Loading...", 0, 30);
				}

				function buildImageList()
				{
					currentFrame = 0;

					// set image list
					for (var i = 0; i < numberOfFrames; i++)
					{
						frames[i] = "d;
					}

					// start preloading
					for (var i = 0; i < numberOfFrames; i++)
					{
						// create object
						var loader = new Image();
						loader.src = frames[i];
						if (i == 0)
						{
							loader.onload = function()
							{
								// adjust the canvas size to the image dimensions
								setViewerSize(this.width, this.height);
								// show the first frame
								showFrame(currentFrame);
							}
						}
						// TODO do something with the loader, I can't store them in an array, dunno why :S
					}
				}

				function stopPlayingImageList()
				{
					if (timer == null)
						return;

					clearInterval(timer);
					timer = null;
				}

				function wheelHandle(delta)
				{
					stopPlayingImageList();

					currentFrame = (currentFrame + delta) % numberOfFrames;
					if (delta < 0)
					{
						if (currentFrame < 0)
							currentFrame = numberOfFrames + currentFrame;
					}

					showFrame(currentFrame);
				}

				function onMouseWheel(event)
				{
					var delta = 0;

					if (! event) // IE fix
						event = viewer.event;

					if (event.wheelDelta) // for IE
					{
						delta = event.wheelDelta; // / 120;
					}
					else
					{
						if (event.detail) // for remaining browsers
						{
							delta = - event.detail;// / 3;
						}
					}

					// prevent mouse wheel default action
					if (! event.preventDefault)
						event.returnValue = false; // for IE
					else
						event.preventDefault(); // for remaining browsers

					// if the delta is not zero call the event handler (positive value for scroll up and negative for down)
					if (delta != 0)
						wheelHandle(delta);
				}

				// register the mouse wheel action handler
				if (! viewer.addEventListener)
					viewer.onmousewheel = onMouseWheel; // for IE
				else
					viewer.addEventListener("DOMMouseScroll", onMouseWheel, false); // for remaining browsers

				function getRecomendedFrameRate()
				{
					if (tagsXML == null)
					{
						alert("Unable to obtain the MetaData of this DICOM file!");
						return;
					}

					var tags = tagsXML.getElementsByTagName("tag");

					for (var i = 0; i < tags.length; i++)
					{
						// obtain the values
						var name = tags[i].getAttribute("name");

						if (name.toLowerCase() == "recommendeddisplayframerate")
						{
							var value = tags[i].childNodes[0].textContent;

							currentFrameRate = parseInt(value);

							return;
						}

						// could not get it, so default to 15
						currentFrameRate = 15;
					}
				}

				function playNextFrame()
				{
					currentFrame = (currentFrame + 1) % numberOfFrames;
					showFrame(currentFrame);
				}

				function playImageList()
				{
					stopPlayingImageList();

					getRecomendedFrameRate();
					timer = setInterval(playNextFrame, 1000 / currentFrameRate);
				}
			</script>
			<br />
			<label for="sopinstanceuid" style="display: inline;">SOP Instance UID:</label>
			<input type="text" id="sopinstanceuid" placeholder="Insert an indexed SOP Instance UID" value="<%= (request.getParameter("SOPInstanceUID") != null) ? request.getParameter("SOPInstanceUID") : "" %>" class="display: inline;" />
			<br />
			<a href="#" onclick="if (document.getElementById('sopinstanceuid').value == null){alert('Insert a SOP INstance UID!'); return;} getTags(document.getElementById('sopinstanceuid').value); return false;">1st - Load MetaData of one file</a>
			<br />
			<a href="#" onclick="getNumberOfFrames(); return false;">2nd - Calculate the number of frames available on the file chosen</a> - <a href="#" onclick="showTags(); return false;">(Optional) Show the loaded MetaData</a>
			<br />
			<a href="#" onclick="buildImageList(); return false;">3rd - Pre-load the frames available and show the first one when ready</a>
			<br />
			<a href="#" onclick="playImageList(); return false;">4rd - Play the frames at the recommended frame rate</a> Or you can use your mouse wheel to change the displayed frame
			<br />
			<a href="#" onclick="stopPlayingImageList(); return false;">5rd - Stop playing the frames</a> Or you can use your mouse wheel to change the displayed frame
			<br />
		</div>
		<%
			// if this page was called with a pre-defined SOP Instance UID then auto-start the vieweing process
			if (request.getParameter("SOPInstanceUID") != null)
			{
				// do all the loading and displaying, except playing, let the user do that if (s)he wants to
				// NOTE the script is defer'ed so it will only be executed after page load
		%>
		<script type="text/javascript" defer>
			showLoading();
			getTags(document.getElementById('sopinstanceuid').value);
			getNumberOfFrames()
			buildImageList();
		</script>
		<%
			}
		%>
		<%@include file="jspf/needsLoginEnd.jspf" %>
		<%@include file="jspf/footer.jspf" %>
	</body>
</html>