import request from './request';
import client from 'dicoogle-client';

/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 */
const DicoogleWebcore = (function () {
  var m = { constructors: {} };
  
  // hidden properties
  
  var slots = {}; // [slotId:string]: WebUiSlot
  var plugins = {}; // [name:string]:Constructor
  var packages = {}; // [name:string]:JSONPackage
  var base_url = null;
  var Dicoogle = null;

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
      arrL = [];
      eventListeners[eventName] = arrL;
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
    m.addEventListener('result', fn);
  };
  /** @param {function(PluginDesc)} fn */
  m.addPluginLoadListener = function (fn) {
    m.addEventListener('load', fn);
  };
  /** @param {function(PluginDesc)} fn */
  m.addMenuPluginListener = function (fn) {
    m.addEventListener('menu', fn);
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
    
    // create dicoogle client access object
    // and inject webcore related methods
    Dicoogle = client(base_url);
    Dicoogle.issueQuery = issueQuery;
    Dicoogle.addMenuPluginListener = m.addMenuPluginListener;
    Dicoogle.addPluginLoadListener = m.addPluginLoadListener;
    Dicoogle.addEventListener = m.addEventListener;
    Dicoogle.addResultListener = m.addResultListener;
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
    //if (Object.keys(plugins).length !== slotIds.length) {
    m.fetchPlugins(slotIds, function(packages) {
      for (let i = 0; i < packages.length; i++) {
        load_plugin(packages[i]);
      }
    });
    //}
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

    // check for plugins of this slotId
    const pluginsOfSlot = getPluginsOf(elem.slotId);
    if (pluginsOfSlot.length > 0) {
      // we already have the plugins, attach them
      m.attachAllPlugins(elem);
    } else {
      // fetch the needed plugins and load them
      m.fetchPlugins(elem.slotId, function(packages) {
        for (let i = 0; i < packages.length; i++) {
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
    slotIds = [].concat(slotIds);
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
            for (let k = 0; k < eventListeners.menu.length; k++) {
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

  /** Issue that the JavaScript modules are loaded, even if no slot has requested it.
   * This function is asynchronous, but currently provides no callback.
   * @param {PackageJSON|PackageJSON[]} packages the JSON package descriptors
   */
  m.fetchModules = function(packages) {
    packages = [].concat(packages);
    for (let i = 0; i < packages.length; i++) {
      if (!(packages[i].name in plugins)) {
        load_plugin(packages[i]);
      }
    }
  };
      
  // --------------------- Injected Plugin-accessible methods ----------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal. The query service requested will be "search" unless modified
   * with the overrideService option.
   * function(query, options, callback)
   * @param query an object containing the query
   * @param {object} options an object containing additional options (such as query plugins to use, result limit, etc.)
   *      - overrideService [string] the name of the service to use instead of "search" 
   * @param {function(error, result)} callback an optional callback function
   */
  function issueQuery(query, options, callback) {
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
  }
  
  /** Invoked by the webUI service for registering a new plugin implementation.
   * @param {{render:function}} pluginInstance a module describing a plugin
   * @param {string} name the name of the plugin
   */
  m.onRegister = function(pluginInstance, name) {
    console.log('onRegister', pluginInstance);
    if (plugins[name]) {
        // already registered, ignore
        return;
    }
    
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
    if (slots[slotId]) {
      for (let slot of slots[slotId]) {
        slot.attachPlugin(pluginInstance);
      }
    }
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
  
  /**
   * @param {HTMLDicoogleSlotElement} elem
   */
  m.attachAllPlugins = function(elem) {
    getPluginsOf(elem.slotId).forEach(pluginInstance => {
      elem.webUi.attachPlugin(pluginInstance);
    });
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
      pluginDOM.className = this.dom.className + '-instance';
      this.dom.appendChild(pluginDOM);
      const e = plugin.render(pluginDOM, this.dom);
      
      this.dom.dispatchEvent(new CustomEvent('plugin-load', {detail: e}));
      
      this.attachments.push(plugin);
      plugin.TabIndex = this.attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      let slotDOM = this.dom;
      slotDOM.innerHTML = '';
      for (let i = 0; i < this.attachments.length; i++) {
        let pluginDOM = document.createElement('div');
        pluginDOM.className = slotDOM.className + '-instance';
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
    if (!slots[id]) {
        slots[id] = [];
    }
    slots[id].push(new m.WebUISlot(id, slotDOM));
    console.log('Created new Dicoogle ' + id + ' slot');
    return id;
  }

  function load_plugin(packageJSON, callback) {
    console.log('Loading plugin', packageJSON.name);
    const {name} = packageJSON;
    if (plugins[name]) {
        if (callback) callback(plugins[name]);
    } else {
        getScript(packageJSON.name, function() {
          console.log('Loaded module ', name);
          if (!isFunction(m.constructors[name])) {
             console.error(`The loaded module ${name} is not a function!`);
          }
          if (callback) callback(plugins[name]);
        });
    }
  }
  
  function getPluginsOf(slotId) {
    const pluginsOfSlot = [];
    if (plugins) {
      for (let name in plugins) {
        if (plugins[name].SlotId === slotId) {
            pluginsOfSlot.push(plugins[name]);
        }
      }
    }
    return pluginsOfSlot;      
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
  
  function dispatch_result(result, requestTime, options) {
    var resultSlotArray = slots.result;
    if (!resultSlotArray) {
      console.error('Cannot show results without a result slot.');
      return;
    }
    for (const resultSlot of resultSlotArray) {
      for (let i = 0; i < resultSlot.attachments.length; i++) {
        resultSlot.attachments[i].onResult(result, requestTime, options);
      }
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
            return this.getAttribute('data-slot-id');
          }
        },
        pluginName: {
          get () {
            return this.getAttribute('data-plugin-name');
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
        }},
        attachedCallback: { value () {
          console.log('[CALLBACK] Dicoogle slot attached: ', this);
          const attSlotId = this.attributes['data-slot-id'];
          const attOnLoaded = this.attributes['data-on-loaded'];
          if (!attSlotId || !attSlotId.value || attSlotId === '') {
            console.error('Dicoogle slot contains illegal data-slot-id!');
            return;
          }
          const sId = attSlotId.value;
          const self = this;
          // add content if the webcore plugin is already available
          if (base_url !== null) {
            m.updateSlot(this, pluginInstance => {
              //if (self.webUi && (!self.pluginName || pluginInstance.Name === self.pluginName)) {
              //  self.webUi.attachPlugin.call(self.webUi, pluginInstance);
              //}
              //if (plugins[this.slotId] && plugins[this.slotId].length > 0) {
              //  this.webUi.refresh();
              //}
            });
          }
          //console.log('[CALLBACK] Dicoogle slot attached: ', this);
        }},
        detachedCallback: { value () {
          //console.log('[CALLBACK] Dicoogle slot detached: ', this);
          const typedSlots = slots[this.slotId];
          for (let i = 0; i < typedSlots.length; i++) {
            if (typedSlots[i].slotDOM === this) {
                typedSlots.splice(i, 1);
                break;
            }
          }
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
