var service_request = function(method, uri, qs, callback) {
  var end_url = parseUrl(uri, qs);
  // This XDomainRequest thing is for IE support (lulz)
  var req = (typeof XDomainRequest !== 'undefined') ? new XDomainRequest() : new XMLHttpRequest();
  req.onreadystatechange = function() {
    if (req.readyState === 4) {
      if (req.status !== 200) {
        callback({code: "SERVER-"+req.status, message: req.statusText}, null);
        return;
      }
      var type = req.getResponseHeader('Content-Type');
      var mime = type;
      if (mime.indexOf(";") !== -1) {
        mime = mime.split(";")[0];
      }
      var result;
      if (mime === 'application/json') {
        result = JSON.parse(req.responseText);
        callback(null, result);
      } else {
        result = { type: type, text: req.responseText };
        callback(null, result);
      }
    }
  };
  req.open(method, end_url, true);
  req.send();
};
