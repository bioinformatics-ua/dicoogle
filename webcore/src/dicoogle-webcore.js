/*
 * Copyright (C) 2015  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-webcore.
 *
 * Dicoogle/dicoogle-webcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-webcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

import {EventEmitter} from 'events';
import client from 'dicoogle-client';

/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 */
let m = { constructors: {} };

// hidden properties

let slots = {}; // [slotId:string]: WebUiSlot
let plugins = {}; // [name:string]:Constructor
let packages = {}; // [name:string]:JSONPackage
let base_url = null;
let Dicoogle; // eslint-disable-line no-unused-vars
let event_hub = null;

export const EVENT_NAMES = Object.freeze([ // eslint-disable-line no-unused-vars
  'load',
  'menu',
  'loadMenu',
  'loadQuery',
  'loadResult',
  'result'
]);


// development-time checker
function check_initialized() {
    if (base_url === null) {
        console.error('Dicoogle Webcore has not been initialized! Please call the init method.');
        return false;
    }
    return true;
}

  /** Initialize Dicoogle Webcore. This should be called once and at the beginning
   * of the web page's life time.
   * @param {string} baseURL the base URL to the Dicoogle services
   * @return {void}
   */
  m.init = function(baseURL) {
    if (typeof document !== 'object') {
      throw "no DOM environment!";
    }
    console.log('Initializing Dicoogle web core ...');
    if (typeof baseURL === 'string') {
      base_url = baseURL;
      if (base_url[base_url.length-1] === '/') {
        base_url = base_url.slice(0, -1);
      }
    }
    slots = {};
    plugins = {};
    packages = {};
    event_hub = new EventEmitter();
    
    // create dicoogle client access object
    // and inject webcore related methods
    Dicoogle = client(base_url);
    Object.assign(Dicoogle, {
        issueQuery,
        emit,
        emitSlotSignal,
        addMenuPluginListener: m.addMenuPluginListener,
        addPluginLoadListener: m.addPluginLoadListener,
        addEventListener: m.addEventListener,
        addResultListener: m.addResultListener,
        removeEventListener: m.removeEventListener
    });
  };
export const init = m.init;
  
  /** Emit an event from the webcore's event emitter.
   * @param {string} name the event name
   * @param {...any} args the data to be transmitted as function arguments to the listeners
   * @return {void}
   */
  function emit(name, ...args) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.emit(name, ...args);
  }

  /** Emit a DOM custom event from the slot element.
   * @param {HTMLDicoogleSlotElement} slotDOM the slot DOM element to emit the event from
   * @param {string} name the event name
   * @param {any} data the data to be transmitted as custom event detail
   * @return {void}
   */
  function emitSlotSignal(slotDOM, name, data) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    slotDOM.dispatchEvent(new CustomEvent(name, {detail: data}));
  }
  
  /** Add an event listener to the webcore's event emitter.
   * @param {string} eventName the name of the event (can be one of 'load','loadMenu','loadQuery','loadResult', or custom event names)
   * @param {function(...any)} fn the listener function
   * @return {DicoogleWebcore} the webcore module itself, used for chaining
   */
  m.addEventListener = function(eventName, fn) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.on(eventName, fn);
    return m;
  };
export const addEventListener = m.addEventListener;
  
  /** Remove an event listener from the webcore's event emitter.
   * @param {string} eventName the name of the event
   * @param {function(...any)} fn the listener function
   * @return {DicoogleWebcore} the webcore module itself, used for chaining
   */
  m.removeEventListener = function(eventName, fn) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.removeListener(eventName, fn);
    return m;
  };
export const removeEventListener = m.removeEventListener;
  
  /**@typedef {object} PluginDesc
   * @property {string} name the unique name of the plugin
   * @property {string} slotId the slot ID that must be attached to
   * @property {?string} caption a caption for the plugin
   */
  
  /** Add a listener to the 'result' event
   * @param {function(result, requestTime, options)} fn the listener function
   * @return {DicoogleWebcore} the webcore module itself, used for chaining
   */
  m.addResultListener = function (fn) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.on('result', fn);
    return m;
  };
export const addResultListener = m.addResultListener;
  
  /** Add a listener to the 'load' event
   * @param {function(PluginDesc)} fn the listener function
   * @return {DicoogleWebcore} the webcore module itself, used for chaining
   */
  m.addPluginLoadListener = function (fn) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.on('load', fn);
  };
export const addPluginLoadListener = m.addPluginLoadListener;
  
  /** Add a listener to the 'menu' event
   * @param {function(PluginDesc)} fn the listener function
   * @return {DicoogleWebcore} the webcore module itself, used for chaining
   */
  m.addMenuPluginListener = function (fn) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    event_hub.on('menu', fn);
  };
export const addMenuPluginListener = m.addMenuPluginListener;
  
  m.updateSlots = function() {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
export const updateSlots = m.updateSlots;
  
  /** Update a given slot.
   * @param {HTMLDicoogleSlotElement} elem the Dicoogle slot DOM element
   * @param {function()} callback called once per plugin
   * @return {void}
   */
  m.updateSlot = function(elem, callback) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
export const updateSlot = m.updateSlot;
  
  /** Fetch the plugin information from the server.
   * @param {string|string[]} slotIds a slot id name or an array of slot id's
   * @param {function(object[])} [callback] a callback function
   * @return {void}
   */
  m.fetchPlugins = function (slotIds, callback) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
            event_hub.emit('menu', {name: packageArray[i].name, slotId: 'menu', caption: packageArray[i].dicoogle.caption});
          }
        }
      }
      if (callback) {
        callback(packageArray);
      }
    });
  };
export const fetchPlugins = m.fetchPlugins;
  
  /** Issue that the JavaScript modules are loaded, even if no slot has requested it.
   * This function is asynchronous, but currently provides no callback.
   * @param {PackageJSON|PackageJSON[]} packages the JSON package descriptors
   * @return {void}
   */
  m.fetchModules = function(packages) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    packages = [].concat(packages);
    for (let i = 0; i < packages.length; i++) {
      if (!(packages[i].name in plugins)) {
        load_plugin(packages[i]);
      }
    }
  };
export const fetchModules = m.fetchModules;
  
  // --------------------- Injected Plugin-accessible methods ----------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal. The query service requested will be "search" unless modified
   * with the overrideService option.
   * function(query, options, callback)
   * @param {any} query an object containing the query (usually a string)
   * @param {object} options an object containing additional options (such as query plugins to use, result limit, etc.)
   *      - overrideService [string] the name of the service to use instead of "search" 
   * @param {function(error, result)} callback an optional callback function
   * @return {void}
   */
  function issueQuery(query, options, callback) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
   * @return {void}
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
    const eventData = {name, slotId, caption: pluginInstance.Caption};
    event_hub.emit('load', eventData);
  };
export const onRegister = m.onRegister;
  
  /** Attach all discovered plugins to the given compatible slot if compatible.
   * @param {HTMLDicoogleSlotElement} elem the slot element
   * @return {void}
   */
  m.attachAllPlugins = function(elem) {
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
    getPluginsOf(elem.slotId).forEach(pluginInstance => {
      elem.webUi.attachPlugin(pluginInstance);
    });
  };
export const attachAllPlugins = m.attachAllPlugins;
  
  // ----------------------------------------------------------------------------
  m.WebUISlot = function(id, dom) {
    this.id = id;
    this.dom = dom;
    this.pluginName = dom.pluginName;
    this.attachments = [];
    this.dom.className = 'dicoogle-webcore-' + this.id;
    this.dom.webUi = this;
    
    this.attachPlugin = function(plugin) {
      if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
      if (plugin.SlotId !== this.id) {
        console.error(`Attempt to attach plugin ${plugin.Name} to the wrong slot`);
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
      
      emitSlotSignal(dom, 'plugin-load', e);
      
      this.attachments.push(plugin);
      plugin.TabIndex = this.attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
export const webUISlot = m.webUISlot;
  

  // ---------------- private methods ----------------
  const ostring = Object.prototype.toString;
  function isFunction(it) {
    return ostring.call(it) === '[object Function]';
  }

  /** Load a new Dicoogle slot into the core.
   * @param {HTMLDicoogleSlotElement} slotDOM a DOM element in the document with the correct tag name
   * @return {string} the id of the slot
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
    if (process.env.NODE_ENV !== 'production' && !check_initialized()) return;
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
    event_hub.emit('result', result, requestTime, options);
  }
  
  /**
   * Send a GET request to a Dicoogle service.
   *
   * @param {string} uri the request URI in string or array form
   * @param {string} qs an object containing query string parameters (or a QS without '?')
   * @param {function(error, outcome)} callback a callback function
   * @return {void}
   */
  function service_get(uri, qs, callback) {
    // issue request
    Dicoogle.request('GET', uri, qs, callback);
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
    script.src = base_url+'/webui/module/'+moduleName;
    prior.parentNode.insertBefore(script, prior);
  }

  // custom element definitions
  export const HTMLDicoogleSlotElement = customElements.define('dicoogle-slot', class HTMLDicoogleSlotElement extends HTMLDivElement {
        constructor() {
          super();
        }

        get slotId() {
            return this.getAttribute('data-slot-id');
        }

        get pluginName() {
            return this.getAttribute('data-plugin-name');
        }

        get webUi() {
            return this._webUi;
        }
      
        set webUi(webUi) {
            this._webUi = webUi;
        }

        get data() {
            return this._data;
        }
        set data(data) {
            this._data = data;
            for (let i = 0; i < this.webUi.attachments.length; i++) {
              if (isFunction(this.webUi.attachments[i].onReceiveData)) {
                this.webUi.attachments[i].onReceiveData(data);
              }
            }
        }

        connectedCallback() {
          console.log('[CALLBACK] Dicoogle slot connected: ', this);
          const attSlotId = this.attributes['data-slot-id'];
          if (!attSlotId || !attSlotId.value || attSlotId === '') {
            console.error('Dicoogle slot contains illegal data-slot-id!');
            return;
          }

          // add content if the webcore plugin is already available
          if (base_url !== null) {
            m.updateSlot(this, (/* pluginInstance */) => {
            });
          }
          //console.log('[CALLBACK] Dicoogle slot attached: ', this);
        }

        disconnectedCallback() {
          //console.log('[CALLBACK] Dicoogle slot detached: ', this);
          const typedSlots = slots[this.slotId];
          for (let i = 0; i < typedSlots.length; i++) {
            if (typedSlots[i].slotDOM === this) {
                typedSlots.splice(i, 1);
                break;
            }
          }
        }

        attributeChangedCallback(attrName, oldValue, newValue) {
          // console.log('[CALLBACK] Dicoogle attribute changed');
          if (attrName === 'data-slot-id' || attrName === 'data-plugin-name') {
            m.updateSlot(this);
          }
        }
    });
    console.log('Registered HTMLDicoogleSlotElement');

m.HTMLDicoogleSlotElement = HTMLDicoogleSlotElement;

export default m;
