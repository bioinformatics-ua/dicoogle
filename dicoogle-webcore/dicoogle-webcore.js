/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 * AMD module
 */
define('dicoogle-webcore', function (require) {
  'use strict';
  
  var m = {};
  
  // hidden properties
  
  var slots = {};
  var plugins = {};
  var packages = {};
  var base_url = '';

  var eventListeners = {
    load       : [],
    loadMenu   : [],
    loadQuery  : [],
    loadResult : [],
    result     : []
  };
    
  /** @param eventName the name of the event (must be one of 'load','loadMenu','loadQuery','loadResult')
   * @param fn function(...)
   */
  m.addEventListener = function(eventName, fn) {
    let arrL = eventListeners[eventName];
    if (!arrL) {
      console.error('Illegal DicoogleWeb event ', eventName);
      return;
    }
    arrL.push(fn);
  };
  
  /** @param fn function(result, requestTime, options) */
  m.addResultListener = function (fn) {
    eventListeners.result.push(fn);
  };
  /** @param fn function(name, slotId) */
  m.addPluginLoadListener = function (fn) {
    eventListeners.load.push(fn);
  };
  /** @param fn function(name) */
  m.addMenuPluginLoadListener = function (fn) {
    eventListeners.loadMenu.push(fn);
  };
  /** @param fn function(name) */
  m.addQueryPluginLoadListener = function (fn) {
    eventListeners.loadQuery.push(fn);
  };
  /** @param fn function(name) */
  m.addResultPluginLoadListener = function (fn) {
    eventListeners.loadResult.push(fn);
  };
  
  m.init = function(baseURL) {
    base_url = '';
    if (typeof baseURL === 'string') {
      base_url = baseURL;
      if (base_url[base_url.length-1] !== '/') {
        base_url += '/';
      }
    }
    slots = {};
    plugins = {};
    packages = {};
    m.updateSlots();
  };
  
  m.updateSlots = function() {    
    if (typeof document !== 'object') {
      throw "no DOM environment!";
    }
    console.log('Initializing Dicoogle web core ...');
    
    // take all <dicoogle-slot> elements in page
    var slotsDOM = document.getElementsByTagName('dicoogle-slot');
    for (let i = 0 ; i < slotsDOM.length ; i++) {
      m.loadSlot(slotsDOM[i]);
    }
    
    // finally, fetch the needed plugins and load each one of them
    m.fetchPlugins(Object.keys(slots));
  };
  
  /** Load a new Dicoogle slot into the core.
   * @param a DOM element in the document with the correct tag name
   * @return the id of the slot
   */
  m.loadSlot = function (slotDOM) {
    var elemAttributes = slotDOM.attributes;
    if (!elemAttributes['data-slot-id']) {
      console.error('Dicoogle web UI slot lacking id attribute!');
      return;
    }
    var id = elemAttributes['data-slot-id'].value;
    slots[id] = new this.WebUISlot(id, slotDOM);
    console.log('Loaded Dicoogle slot', id);
    return id;
  };
  
  /**
   * @param slotIds an array of slot id's
   */
  m.fetchPlugins = function (slotIds) {
    console.log('Fetching Dicoogle web UI plugin descriptors ...');
    var uri = 'webui';
    service_get(uri, {'slot-id':slotIds}, function(error, data) {
      if (error) {
        console.error('Failed to fetch plugin descriptors:' , error);
        return;
      }
      var packageArray = data.plugins;
      for (let i = 0 ; i < packageArray.length ; i++) {
        packages[packageArray[i].name] = packageArray[i];
        load_plugin(packageArray[i]);
      }
    });
  };
      
  // --------------------- Plugin-accessible methods --------------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal. The query service requested will be "search" unless modified
   * with the overrideService option.
   * function(query, options, callback)
   * @param query an object containing the query
   * @param options an object containing additional options (such as query plugins to use, result limit, etc.)
   *      - overrideService [string] the name of the service to use instead of "search" 
   * @param callback an optional callback function(error, result)
   */
  m.issueQuery = function(query, options, callback) {
    options = options || {};
    options.query = query;
    let requestTime = new Date();
    let queryService = options.overrideService || 'search';
    service_get(queryService, options, function (error, data) {
      if (error) {
        callback(error, null);
        return;
      }
      dispatch_result(data, requestTime, options);
      callback(null, data);
    });
  };
  
  /** Make a request to Dicoogle.
   * function(service, [data,] callback)
   * @param service the relative URI of the service
   * @param data the data to pass
   * @param callback function(error, result)
   */
  m.request = function(service, arg1, arg2) {
    var data = (typeof arg1 === 'object') ? arg1 : {};
    var callback = (typeof arg1 === 'function') ? arg1 : arg2;
    if (typeof callback !== 'function')  {
      console.error('invalid call to DicoogleWeb.request : a callback function is required');
      return;
    }
    service_get(service, data, callback);
  };
  
  // ----------------------------------------------------------------------------
  m.WebUISlot = function(id, dom) {
    this.id = id;
    this.dom = dom;
    this.attachments = [];
    
    this.attachPlugin = function(plugin) {
      if (plugin.SlotId !== this.id) {
        console.error('Attempt to attach plugin ', plugin.Name, ' to the wrong slot');
        return;
      }
      if (this.attachments.length === 0) {
        this.dom.innerHTML = '';
      }
      if (this.attachments.length > 0) {
        this.dom.appendChild(document.createElement('hr'));
      }
      let pluginDOM = document.createElement('div');
      this.dom.appendChild(pluginDOM);
      plugin.render(pluginDOM);
      this.attachments.push(plugin);
      plugin.TabIndex = this.attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      let slotDOM = this.dom;
      slotDOM.innerHTML = '';
      for (let i = 0 ; i < this.attachments.length ; i++) {
        if (i > 0) {
          slotDOM.appendChild(document.createElement('hr'));
        }
        let pluginDOM = document.createElement('div');
        slotDOM.appendChild(pluginDOM);
        this.attachments[i].render(pluginDOM);
      }
    };
  };

  // ---------------- private methods ----------------
  function isArray(it) {
    const ostring = Object.prototype.toString;
    return ostring.call(it) === '[object Array]';
  }
  
  function isFunction(it) {
    const ostring = Object.prototype.toString;
    return ostring.call(it) === '[object Function]';
  }

  function load_plugin(packageJSON) {
    let slotId = slots[packageJSON.dicoogle['slot-id']];
    if (!slotId) {
      console.error('Unexistent slot ID ', packageJSON.dicoogle['slot-id'], '!');
      return;
    }
    getScript(packageJSON.name, function() {
      require([packageJSON.name], function(PluginModule) {
        if (!isFunction(PluginModule)) {
          console.error('Plugin module is not a function! ', PluginModule);
        } else {
          onRegister(new PluginModule(), packageJSON.name);
        }
      });
    });
  }

  function onRegister(pluginInstance, name) {
    if (typeof pluginInstance !== 'object' || typeof pluginInstance.render !== 'function') {
      console.error('Dicoogle web UI plugin ', name, ' is corrupted or invalid: ', pluginInstance);
      return;
    }
    let thisPackage = packages[name];
    let slotId = thisPackage.dicoogle['slot-id'];
    if (slotId === 'result' && typeof pluginInstance.onResult !== 'function') {
      console.error('Dicoogle web UI plugin ', name, ' does not provide onResult');
      return;
    }
    console.log('Executed plugin: ', name);
    pluginInstance.Name = name;
    pluginInstance.SlotId = slotId;
    pluginInstance.Caption = thisPackage.dicoogle.caption || name;
    plugins[name] = pluginInstance;
    slots[slotId].attachPlugin(pluginInstance);
    for (let i = 0 ; i < eventListeners.load.length ; i++) {
      eventListeners.load[i](name, slotId);
    }
    if (slotId === 'query') {
      for (let i = 0 ; i < eventListeners.loadQuery.length ; i++) {
        eventListeners.loadQuery[i](name);
      }
    } else if (slotId === 'result') {
      for (let i = 0 ; i < eventListeners.loadResult.length ; i++) {
        eventListeners.loadResult[i](name);
      }
    } else if (slotId === 'menu') {
      for (let i = 0 ; i < eventListeners.loadMenu.length ; i++) {
        eventListeners.loadMenu[i](name);
      }
    }
  }
  
  /// @deprecated
  function rename_element(node,name) {
    var renamed = document.createElement(name); 
    for (var i = 0 ; i < node.attributes.length ; i++) {
      let a = node.attributes[i];
      renamed.setAttribute(a.nodeName, a.nodeValue);
    }
    while (node.firstChild) {
      renamed.appendChild(node.firstChild);
    }
    node.parentNode.replaceChild(renamed, node);
    return renamed;
  }
  
  function dispatch_result(result, requestTime, options) {
    var resultSlot = slots.result;
    if (!resultSlot) {
      console.error('Cannot show results without a result slot.');
      return;
    }
    for (let i = 0 ; i < resultSlot.attachments.length ; i++) {
      resultSlot.attachments[i].onResult(result, requestTime, options);
    }
    for (let i = 0 ; i < eventListeners.result.length ; i++) {
      eventListeners.result[i](result, requestTime, options);
    }
  }
  
  /**
   * send a GET request to a Dicoogle service
   *
   * @param {string} uri the request URI in string or array form
   * @param {string} qs an object containing query string parameters (or a QS without '?')
   * @param {Function(error,outcome)} callback
   */
  function service_get(uri, qs, callback) {
    // create full query string
    let end_url = base_url;
    if (isArray(qs[uri])) {
      end_url += uri.join('/');
    } else {
      end_url += uri;
    }
    
    let qstring = '?';
    if (typeof qs === 'string') {
      qstring += qs;
    } else {
      let qparams = [];
      for (let pname in qs) {
        if (isArray(qs[pname])) {
          for (let j = 0 ; j < qs[pname].length ; j++) {
            qparams.push(pname + '=' + encodeURIComponent(qs[pname][j]));
          }
        } else {
          qparams.push(pname + '=' + encodeURIComponent(qs[pname]));
        }
      }
      qstring += qparams.join('&');
    }
    end_url += qstring;
    
    // This XDomainRequest thing is for IE support (lulz)
    let req = (typeof XDomainRequest !== 'undefined') ?
      new XDomainRequest() : new XMLHttpRequest();
    req.onreadystatechange = function() {
      if (req.readyState === 4) {
        if (req.status !== 200) {
          callback({code: "SERVER-"+req.status, message: req.statusText}, null);
          return;
        }
        let type = req.getResponseHeader('Content-Type');
        let mime = type;
        if (mime.indexOf(";") !== -1) {
          mime = mime.split(";")[0];
        }
        if (mime === 'application/json') {
          let result = JSON.parse(req.responseText);
          callback(null, result);
        } else {
          let result = { type: type, text: req.responseText };
          callback(null, result);
        }
      }
    };
    req.open('GET', end_url, true);
    req.send();
  }
  
  function getScript(moduleName, callback) {
    let script = document.createElement('script');
    let prior = document.getElementsByTagName('script')[0];
    script.async = 1;
    prior.parentNode.insertBefore(script, prior);
    let onLoadHandler = function( _, isAbort ) {
        if(isAbort || !script.readyState || /loaded|complete/.test(script.readyState) ) {
            script.onload = script.onreadystatechange = null;
            script = undefined;
            if(!isAbort) {
              if(callback) callback();
            }
        }
    };

    script.onload = script.onreadystatechange = onLoadHandler;

    script.src = base_url+'webui?module='+moduleName+'&process=false';
  }

  // custom element definitions
  var HTMLDicoogleSlotElement = (function() {

    var elem = document.registerElement('dicoogle-slot', {
      prototype: Object.create(HTMLDivElement.prototype, {
        slotId: {
          get () {
            return this.attributes['data-slot-id'] ? this.attributes['data-slot-id'].value : null;
          }
        },
        createdCallback: { value () {
          //console.log('[CALLBACK] Dicoogle slot ', this.slotId,' created: ', this);
        }},
        attachedCallback: { value () {
          var attSlotId = this.attributes['data-slot-id'];
          if (!attSlotId || !attSlotId.value || attSlotId === '') {
            console.error('Dicoogle slot contains illegal data-slot-id!');
            return;
          }
          //console.log('[CALLBACK] Dicoogle slot attached: ', this);
        }},
        detachedCallback: { value () {
          //console.log('[CALLBACK] Dicoogle slot detached: ', this);
        }}
      })
    });
    console.log('Registered HTMLDicoogleSlotElement');
    return elem;
  })();

  m.HTMLDicoogleSlotElement = HTMLDicoogleSlotElement;
  console.log('Associated HTMLDicoogleSlotElement to DicoogleWeb');
  
  return m;
});
