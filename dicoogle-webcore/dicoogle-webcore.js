import request from './request';

/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 */
const DicoogleWebcore = (function () {
  var m = { constructors: {} };
  
  // hidden properties
  
  var slots = {};
  var plugins = {};
  var packages = {};
  var base_url = null;

  var eventListeners = {
    load: [],
    menu: [],
    loadMenu: [],
    loadQuery: [],
    loadResult: [],
    result: []
  };
  
  /** 
   * @param {string} eventName the name of the event (must be one of 'load','loadMenu','loadQuery','loadResult')
   * @param {function} fn
   */
  m.addEventListener = function(eventName, fn) {
    let arrL = eventListeners[eventName];
    if (!arrL) {
      console.error('Illegal DicoogleWeb event ', eventName);
      return;
    }
    arrL.push(fn);
  };
  
  /**@typedef {object} PluginDesc
   * @property {string} name the unique name of the plugin
   * @property {string} slotId the slot ID that must be attached to
   * @property {?string} caption a caption for the plugin
   */
  
  /** @param {function(result, requestTime, options)} fn */
  m.addResultListener = function (fn) {
    eventListeners.result.push(fn);
  };
  /** @param {function(PluginDesc)} fn */
  m.addPluginLoadListener = function (fn) {
    eventListeners.load.push(fn);
  };
  /** @param {function(PluginDesc)} fn */
  m.addMenuPluginListener = function (fn) {
    eventListeners.menu.push(fn);
  };
  
  /** Initialize Dicoogle Webcore. This should be called once and at the beginning
   * of the web page's life time.
   * 
   */
  m.init = function(baseURL) {
    if (typeof document !== 'object') {
      throw "no DOM environment!";
    }
    console.log('Initializing Dicoogle web core ...');
    base_url = '';
    if (typeof baseURL === 'string') {
      base_url = baseURL;
      if (base_url[base_url.length-1] === '/') {
        base_url = base_url.slice(0, -1);
      }
    }
    slots = {};
    plugins = {};
    packages = {};
    //m.updateSlots();
  };
  
  m.updateSlots = function() {    
    if (typeof document !== 'object') {
      throw "no DOM environment!";
    }
    
    if (base_url === null) {
      return;
    }
    
    // take all <dicoogle-slot> elements in page
    let slotsDOM = document.getElementsByTagName('dicoogle-slot');
    for (let i = 0; i < slotsDOM.length; i++) {
      m.loadSlot(slotsDOM[i]);
    }
    
    // finally, fetch the needed plugins and load each one of them
    let slotIds = Object.keys(slots);
    if (Object.keys(plugins).length !== slotIds.length) {
      m.fetchPlugins(slotIds, function(packages) {
        for (let i = 0 ; i < packages.length ; i++) {
          load_plugin(packages[i]);
        }
      });
    }
  };

  /** Update a given slot.
   * @param {HTMLDicoogleSlotElement} elem
   * @param {function()} callback called once per plugin
   */
  m.updateSlot = function(elem, callback) {
    if (typeof document !== 'object') {
      throw "no DOM environment!";
    }
    
    loadSlot(elem);
    
    // finally, fetch the needed plugin and load it
    if (!plugins[elem.slotId]) {
      m.fetchPlugins(elem.slotId, function(packages) {
        for (let i = 0 ; i < packages.length ; i++) {
          load_plugin(packages[i], callback);
        }
      });
    }
  };
    
  /** Fetch the plugin information from the server.
   * @param {string|string[]} slotIds a slot id name or an array of slot id's
   * @param {function(object[])} [callback]
   */
  m.fetchPlugins = function (slotIds, callback) {
    console.log('Fetching Dicoogle web UI plugin descriptors ...');
    if (typeof slotIds === 'string') {
      slotIds = [slotIds];
    }
    let uri = 'webui';
    service_get(uri, {'slot-id': slotIds}, function(error, data) {
      if (error) {
        console.error('Failed to fetch plugin descriptors:' , error);
        return;
      }
      var packageArray = data.plugins;
      for (let i = 0; i < packageArray.length; i++) {
        if (!packages[packageArray[i].name]) {
          packages[packageArray[i].name] = packageArray[i];
          if (packageArray[i].dicoogle.slotId === 'menu') {
            for (let k = 0 ; k < eventListeners.menu.length ; k++) {
              eventListeners.menu[k]({name: packageArray[i].name, slotId: 'menu', caption: packageArray[i].dicoogle.caption});
            }
          }
        }
      }
      if (callback) {
        callback(packageArray);
      }
    });
  };
      
  // --------------------- Plugin-accessible methods --------------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal. The query service requested will be "search" unless modified
   * with the overrideService option.
   * function(query, options, callback)
   * @param query an object containing the query
   * @param {object} options an object containing additional options (such as query plugins to use, result limit, etc.)
   *      - overrideService [string] the name of the service to use instead of "search" 
   * @param {function(error, result)} callback an optional callback function
   */
  m.issueQuery = function(query, options, callback) {
    options = options || {};
    options.query = query;
    let requestTime = new Date();
    let queryService = options.overrideService || 'search';
    service_get(queryService, options, function (error, data) {
      if (error) {
        if (callback) callback(error, null);
        return;
      }
      dispatch_result(data, requestTime, options);
      if (callback) callback(null, data);
    });
  };
  
  /** Make a GET request to Dicoogle.
   * function(service, [data,] callback)
   * @param service the relative URI of the service
   * @param data the data to pass
   * @param callback function(error, result)
   */
  m.request = function(service, arg1, arg2) {
    let data = (typeof arg1 === 'object') ? arg1 : {};
    let callback = (typeof arg1 === 'function') ? arg1 : arg2;
    if (typeof callback !== 'function') {
      console.error('invalid call to DicoogleWeb.request : a callback function is required');
      return;
    }
    service_get(service, data, callback);
  };
  
  /** Invoked by the webUI service for registering a new plugin implementation.
   * @param {{render:function}} pluginInstance a module describing a plugin
   * @param {string} name the name of the plugin
   */
  m.onRegister = function(pluginInstance, name) {
    console.log('onRegister', pluginInstance);
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
    for (let i = 0; i < eventListeners.load.length; i++) {
      eventListeners.load[i]({name, slotId, caption: pluginInstance.Caption});
    }
    if (slotId === 'query') {
      for (let i = 0; i < eventListeners.loadQuery.length; i++) {
        eventListeners.loadQuery[i]({name, slotId, caption: pluginInstance.Caption});
      }
    } else if (slotId === 'result') {
      for (let i = 0; i < eventListeners.loadResult.length; i++) {
        eventListeners.loadResult[i]({name, slotId, caption: pluginInstance.Caption});
      }
    } else if (slotId === 'menu') {
      for (let i = 0; i < eventListeners.loadMenu.length; i++) {
        eventListeners.loadMenu[i]({name, slotId, caption: pluginInstance.Caption});
      }
    }
  };

  // ----------------------------------------------------------------------------
  m.WebUISlot = function(id, dom) {
    this.id = id;
    this.dom = dom;
    this.pluginName = dom.pluginName;
    this.attachments = [];
    this.dom.className = 'dicoogle-webcore-' + this.id;
    this.dom.webUi = this;
    
    this.attachPlugin = function(plugin) {
      if (plugin.SlotId !== this.id) {
        console.error('Attempt to attach plugin ', plugin.Name, ' to the wrong slot');
        return;
      }
      if ((typeof this.pluginName === 'string') && this.pluginName !== plugin.Name) {
        //console.log('Ignoring plugin', plugin.Name);
        return;
      }
      if (this.attachments.length === 0) {
        this.dom.innerHTML = '';
      }
      let pluginDOM = document.createElement('div');
      pluginDOM.className = this.dom.className + '_' + this.attachments.length;
      this.dom.appendChild(pluginDOM);
      plugin.render(pluginDOM);
      this.attachments.push(plugin);
      plugin.TabIndex = this.attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      let slotDOM = this.dom;
      slotDOM.innerHTML = '';
      for (let i = 0; i < this.attachments.length; i++) {
        let pluginDOM = document.createElement('div');
        pluginDOM.className = slotDOM.className + '_' + i;
        slotDOM.appendChild(pluginDOM);
        this.attachments[i].render(pluginDOM);
      }
    };
  };

  // ---------------- private methods ----------------
  const ostring = Object.prototype.toString;
  function isArray(it) {
    return ostring.call(it) === '[object Array]';
  }
  
  function isFunction(it) {
    return ostring.call(it) === '[object Function]';
  }

  /** Load a new Dicoogle slot into the core.
   * @param slotDOM a DOM element in the document with the correct tag name
   * @return the id of the slot
   */
  function loadSlot (slotDOM) {
    if (base_url === null) {
      return;
    }
    if (!slotDOM.slotId) {
      console.error('Dicoogle web UI slot has no data-slot-id attribute!');
      return null;
    }
    let id = slotDOM.slotId;
    slots[id] = new m.WebUISlot(id, slotDOM);
    console.log('Loaded Dicoogle slot', id);
    return id;
  }

  function load_plugin(packageJSON, callback) {
    console.log('Loading plugin', packageJSON.name);
    let slotId = slots[packageJSON.dicoogle['slot-id']];
    if (!slotId) {
      console.error('Unexistent slot ID ', packageJSON.dicoogle['slot-id'], '!');
      return;
    }
    getScript(packageJSON.name, function() {
      const {name} = packageJSON;
      console.log('Loaded module ', name);
      if (!isFunction(m.constructors[name])) {
        console.error('The loaded module', name, 'is not a function!');
      }
      if (callback) callback(plugins[name]);
    });
  }
  
  function camelize(s) {
    let words = s.split('-');
    if (words.length === 0) return '';
    let t = words[0];
    for (let i = 1; i < words.length; i++) {
        if (words[i].length !== 0) {
            t += words[i][0].toUpperCase() + words[i].substring(1);
        }
    }
    return t;
  }
  
  /// @deprecated
  function rename_element(node,name) {
    var renamed = document.createElement(name); 
    for (var i = 0; i < node.attributes.length; i++) {
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
    for (let i = 0; i < resultSlot.attachments.length; i++) {
      resultSlot.attachments[i].onResult(result, requestTime, options);
    }
    for (let i = 0; i < eventListeners.result.length; i++) {
      eventListeners.result[i](result, requestTime, options);
    }
  }
  
  /**
   * send a GET request to a Dicoogle service
   *
   * @param {string} uri the request URI in string or array form
   * @param {string} qs an object containing query string parameters (or a QS without '?')
   * @param {function(error, outcome)} callback
   */
  function service_get(uri, qs, callback) {
    // issue request
    let full_uri;
    if (isArray(uri)) {
      full_uri = [base_url].concat(uri);
    } else {
      full_uri = [base_url, uri];
    }
    request('GET', full_uri, qs, callback);
  }
  
  function getScript(moduleName, callback) {
    let script = document.createElement('script');
    let prior = document.getElementsByTagName('script')[0];
    
    script.async = true;
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
    script.src = base_url+'/webui?module='+moduleName+'&process=true';
    prior.parentNode.insertBefore(script, prior);
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
        pluginName: {
          get () {
            return this.attributes['data-plugin-name'] ? this.attributes['data-plugin-name'].value : null;
          }
        },
        webUi: {
          get () {
            return this._webUi;
          },
          set (webUi) {
            this._webUi = webUi;
          }
        },
        createdCallback: { value () {
          //console.log('[CALLBACK] Dicoogle slot ', this.slotId,' created: ', this);
        }},
        attachedCallback: { value () {
          let attSlotId = this.attributes['data-slot-id'];
          if (!attSlotId || !attSlotId.value || attSlotId === '') {
            console.error('Dicoogle slot contains illegal data-slot-id!');
            return;
          }
          const sId = attSlotId.value;
          const self = this;
          // add content if the webcore plugin is already available
          if (base_url !== null) {
            m.updateSlot(this, function(pluginInstance){
              //if (self.webUi && (!self.pluginName || pluginInstance.Name === self.pluginName)) {
              //  self.webUi.attachPlugin.call(self.webUi, pluginInstance);
              //}
            });
          }
          //console.log('[CALLBACK] Dicoogle slot attached: ', this);
        }},
        detachedCallback: { value () {
          //console.log('[CALLBACK] Dicoogle slot detached: ', this);
        }},
        attributeChangedCallback: { value (attrName, oldVal, newVal) {
          // console.log('[CALLBACK] Dicoogle attribute changed');
          if (attrName === 'data-slot-id' || attrName === 'data-plugin-name') {
            m.updateSlot(this);
          }
        }}
      })
    });
    console.log('Registered HTMLDicoogleSlotElement');
    return elem;
  })();

  m.HTMLDicoogleSlotElement = HTMLDicoogleSlotElement;
  
  return m;
})();

export default DicoogleWebcore;
