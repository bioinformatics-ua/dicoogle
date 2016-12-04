/**
 * Dicoogle Service Wrapper
 */

/** @namespace */
var dicoogle = (function dicoogle_module() {

  // private variables of the module
  var url_ = "http://localhost:8080";
  
  // module
  var m =  {};

  var EndPoints = {
    SEARCH: "search",
    PROVIDERS: "providers"
//    IMAGESEARCH: "imageSearch",
//    DIC2PNG: "dic2png",
//    DICTAGS: "dictags",
//    PLUGINS: "plugin",
//    INDEX: "indexer",
//    DIM: "dim",
//    FILE: "file",
//    DUMP: "dump",
//    TAGS: "tags",
//    IMAGE: "image",
//    ENUMFIELD: "enumField",
//    WADO: "wado",
//    EXAMTIME: "examTime"
  };
  
  m.Endpoints = EndPoints;

  /** Perform a raw request.
   * @param service the URI endpoint of the service
   * @param qs the query string of the request
   * @param callback (error, result)
   */
  m.request = function dicoogle_queryFreeText(service, qs, callback) {
      service_request('GET', service, qs, function(err, data) {
        callback(err, data ? data : null);
      });
  };
  
  /** Perform a free text query.
   * @param query text query
   * @param callback (error, result)
   */
  m.queryFreeText = function dicoogle_queryFreeText(query, callback) {
      service_request('GET', EndPoints.SEARCH, {
        keyword: false, query: query
        }, function(err, data) {
          callback(err, data ? data.results : null);
      });
  };
  
  /** Perform an advanced query.
   * @param query text query
   * @param callback (error, result)
   */
  m.queryAdvanced = function dicoogle_queryAdvanced(query, callback) {
    service_request('GET', EndPoints.SEARCH, {
      keyword: true,
      query: query}, function(err, data) {
        callback(err, data ? data.results : null);
    });
  };

//---------------------private methods--------------------------

  function isArray(it) {
    var ostring = Object.prototype.toString;
    return ostring.call(it) === '[object Array]';
  }
  
  function parseUrl(uri, qs) {
    // create full query string
    var end_url = url_;
    if (isArray(qs[uri])) {
      end_url += uri.join('/');
    } else {
      end_url += uri;
    }
    
    var qstring;
    if (!qs) {
      qstring = '';
    } if (typeof qs === 'string') {
      qstring = '?' + qs;
    } else {
      var qparams = [];
      for (var pname in qs) {
        if (isArray(qs[pname])) {
          for (var j = 0 ; j < qs[pname].length ; j++) {
            qparams.push(pname + '=' + encodeURIComponent(qs[pname][j]));
          }
        } else if (qs[pname]) {
          qparams.push(pname + '=' + encodeURIComponent(qs[pname]));
        } else {
          qparams.push(pname);
        }
      }
      qstring = '?' + qparams.join('&');
    }
    return end_url + qstring;
  }

  /**
   * send a REST request to the service
   *
   * @param {string} method the http method ('GET','POST','PUT' or 'DELETE')
   * @param {string} uri the request URI
   * @param {string|hash} qs the query string parameters
   * @param {Function(error,outcome)} callback
   */
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
  
  /**
   * Initialize a new Dicoogle access object, which can be used multiple times.
   *
   * @param {String} url the controller service's base url
   * @return a dicoogle service access object
   */
  return function(url) {

    url_ = url || "http://localhost:8080/";
    if (url_[url_.length-1] !== '/')
      url_ += '/';
    if (url_.indexOf('://') === -1) {
      url_ = 'http://' + url_;
    }
    
    return m;
  };
})();

module.exports = dicoogle;
