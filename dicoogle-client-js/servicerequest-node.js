var service_request = function(method, uri, qs, callback) {
  var end_url = parseUrl(uri, qs);
  var options = require('url').parse(end_url);
  options.method = method;
  var req = require('http').request(options, function(res) {
    if (res.statusCode !== 200) {
      callback({code: "SERVER-"+res.statusCode,
                message: res.statusMessage}, null);
      req.abort();
      return;
    }
    // accumulate chunks and convert to JSON in the end.
    // raw usage of http module, no external libraries.
    res.setEncoding('utf8');
    var acc_data = '';
    res.on('data', function(chunk) {
      acc_data += chunk;
    });
    res.on('end', function() {
      var type = res.headers['content-type'];
      var mime = type;
      if (mime.indexOf(";") !== -1) {
        mime = mime.split(";")[0];
      }
      var result;
      if (mime === 'application/json') {
        result = JSON.parse(acc_data);
        callback(null, result);
      } else {
        result = {type: type, text: acc_data};
        callback(null, result);
      }
    });
  });
  req.on('error', function (exception) {
    callback({code: 'EXCEPT', exception: exception});
  });
  req.end();
};