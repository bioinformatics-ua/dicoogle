<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Dicoogle Web</title>
		<link rel="stylesheet" href="style.css" />
		<script src="transform.js" defer="defer"></script>
		<script src="gesture.js" defer="defer"></script>
	</head>
	<body>
		<figure>
			<img id="orig" src="test.png" width="600" height="337">
			<figcaption>The original test image</figcaption>
		</figure>
		<figure>
			<canvas id="grayscale" width="600" height="337"></canvas>
			<button onclick="grayscale()">Grayscale the image</button>
		</figure>
		<figure>
			<canvas id="brightness" width="600" height="337"></canvas>
			<button onclick="brightness()">Brighten the image</button>
		</figure>
		<figure>
			<canvas id="contrast" width="600" height="337"></canvas>
			<button onclick="contrast()">Darken the image</button>
		</figure>
		<figure>
			<canvas id="threshold" width="600" height="337"></canvas>
			<button onclick="threshold()">Threshold the image</button>
		</figure>
		<figure>
			<canvas id="sharpen" width="600" height="337"></canvas>
			<button onclick="sharpen()">Sharpen the image</button>
		</figure>
		<figure>
			<canvas id="sobel" width="600" height="337"></canvas>
			<button onclick="sobel()">Run a Sobel filter on the image</button>
		</figure>
		<figure>
			<canvas id="custom" width="600" height="337"></canvas>
			<div id="customMatrix">
				<input type="text" value="1" size="3">
				<input type="text" value="1" size="3">
				<input type="text" value="1" size="3">
				<br>
				<input type="text" value="1" size="3">
				<input type="text" value="0.7" size="3">
				<input type="text" value="-1" size="3">
				<br>
				<input type="text" value="-1" size="3">
				<input type="text" value="-1" size="3">
				<input type="text" value="-1" size="3">
				<br>
			</div>
			<button onclick="custom()">Run the above filter on the image</button>
		</figure>
		<div id="picture-frame" ontouchstart="touchStart(event, 'picture-frame');" ontouchend="touchEnd(event);" ontouchmove="touchMove(event);" ontouchcancel="touchCancel(event);" style="width: 300px; height: 300px; background-color: blueviolet;">
			&nbsp;
		</div>
	</body>
</html>