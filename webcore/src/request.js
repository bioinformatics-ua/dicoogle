
function isArray(it) {
	const ostring = Object.prototype.toString;
	return ostring.call(it) === '[object Array]';
}

function isFunction(it) {
	const ostring = Object.prototype.toString;
	return ostring.call(it) === '[object Function]';
}

// This XDomainRequest thing is for IE support (lulz)
const XHR = (typeof XMLHttpRequest !== 'undefined') ?
     XMLHttpRequest : (typeof XDomainRequest !== 'undefined' && XDomainRequest);

/** Send an HTTP request.
 *
 * @param {string} method the HTTP method to produce ('GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', ...)
 * @param {(string|string[])} uri the request URI in string or array form
 * @param {(string|object)} qs an object containing query string parameters (or a string without '?')
 * @param {any} [content] the data to be contained in the request body
 * @param {function(error,outcome)} callback
 */
function request(method, uri, qs, content, callback) {
	method = method.toUpperCase();
	// create full query string
	let endUrl = '';
	if (isArray(uri)) {
		endUrl += uri.join('/');
	} else {
		endUrl += uri;
	}
	
	// check for content
	if (isFunction(content)) {
		callback = content;
		content = null;
	}

	let qstring = '?';
	if (typeof qs === 'string') {
		qstring += qs;
	} else if (typeof qs === 'object') {
		let qparams = [];
		for (let pname in qs) {
			if (isArray(qs[pname])) {
				for (let j = 0; j < qs[pname].length; j++) {
					qparams.push(pname + '=' + encodeURIComponent(qs[pname][j]));
				}
			} else {
				qparams.push(pname + '=' + encodeURIComponent(qs[pname]));
			}
		}
		qstring += qparams.join('&');
		if (qstring === '?') {
			qstring = '';
		}
	} else {
		qstring = '';
	}
	endUrl += qstring;

	let req = new XHR();
	req.onreadystatechange = function () {
		if (req.readyState === 4) {
			let err = null;
			if (req.status !== 200) {
				err = { code: "SERVER-" + req.status, message: req.statusText };
			}
			const type = req.getResponseHeader('Content-Type');
			if (!type) {
				callback(err, null);
				return;
			}
			let mime = type;
			if (mime.indexOf(";") !== -1) {
				mime = mime.split(";")[0];
			}
			if (mime === 'application/json') {
				const result = JSON.parse(req.responseText);
				callback(err, result);
			} else {
				const result = { type: type, text: req.responseText };
				callback(err, result);
			}
		}
	};
	req.open(method, endUrl, true);
	if (content instanceof FormData) {
		//req.setRequestHeader('Content-Type', 'multipart/form-data');
		req.send(content);
	} else if (content) {
		req.setRequestHeader('Content-Type', 'application/json');
		req.send(JSON.stringify(content));
	} else req.send();
}

export default request;
