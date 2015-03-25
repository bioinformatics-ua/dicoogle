/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 */

module.exports = (function () {
  var m = {};
  
  // dependencies
  var $ = require('jquery');
  $.ajaxSettings.traditional = true;

  // custom element definitions
  var HTMLDicoogleSlotElement = (function() {

    var elem = document.registerElement('dicoogle-slot', {
      prototype: Object.create(HTMLDivElement.prototype, {
        slotId: {
          get: function() {
            return this.attributes['data-slot-id'] ? this.attributes['data-slot-id'].value : null;
          }
        },
        createdCallback: { value: function() {
          //console.log('[CALLBACK] Dicoogle slot ', this.slotId,' created: ', this);
        }},
        attachedCallback: { value: function() {
          var attSlotId = this.attributes['data-slot-id'];
          if (!attSlotId || !attSlotId.value || attSlotId === '') {
            console.error('Dicoogle slot contains illegal data-slot-id!');
            return;
          }
          //console.log('[CALLBACK] Dicoogle slot attached: ', this);
        }}
      })
    });
    console.log('Registered HTMLDicoogleSlotElement');
    return elem;
  })();

  m.HTMLDicoogleSlotElement = HTMLDicoogleSlotElement;
  console.log('Associated HTMLDicoogleSlotElement to DicoogleWeb');
  
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
    var qs = '';
    if (Array.isArray(slotIds)) {
      var params = [];
      for (let i = 0 ; i < slotIds.length ; i++) {
        params.push('slot-id=' + slotIds[i]);
      }
      qs = params.join('&');
    } else if (typeof slotIds === 'string') {
      qs = 'slot-id=' + slotIds;
    }
    $.ajax({
      dataType: 'json',
      url: base_url+uri,
      data: qs
    }).done(function (data){
      var packageArray = data.plugins;
      for (let i = 0 ; i < packageArray.length ; i++) {
        packages[packageArray[i].name] = packageArray[i];
        load_plugin(packageArray[i]);
      }
    }).fail(function(error) {
      console.error('Failed to fetch plugin descriptors:' , error);
    });
  };
  
  m.onRegister = function (pluginInstance, name) {
    if (typeof pluginInstance !== 'object' || typeof pluginInstance.render !== 'function') {
      console.error('Dicoogle web UI plugin ', name, ' is corrupted');
      return;
    }
    var thisPackage = packages[name];
    var slotId = thisPackage.dicoogle['slot-id'];
    if (slotId === 'result' && typeof pluginInstance.onResult !== 'function') {
      console.error('Dicoogle web UI plugin ', name, ' does not provide onResult');
      return;
    }
    console.log('Executed plugin:' + name);
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
  };
    
  // --------------------- Plugin-accessible methods --------------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal.
   * @param query an object containing the query
   * @param options an object containing additional options (such as query plugins to use, result limit, etc.)
   * @param callback an optional callback function(error, result)
   */
  m.issueQuery = function(query, options, callback) {
    options.query = query;
    var requestTime = new Date();
    $.getJSON(base_url+'search', options).done(function(data) {
      dispatch_result(data, requestTime, options);
      if (callback) {
        callback(null, data);
      }
    }).fail(function(error) {
      callback(error, null);
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
    $.getJSON(base_url+service, data)
      .done(function(result) { callback(null, result); })
      .fail(function(error) { callback(error, null); });
  };
  
  // ----------------------------------------------------------------------------
  m.WebUISlot = function(id, dom) {
    this.id = id;
    this.dom = dom;
    this.attachments = [];
    
    this.attachPlugin = function(plugin) {
      if (plugin.SlotId !== this.id) {
        console.error('Attempt to attach plugin ' + plugin.Name + ' to the wrong slot');
        return;
      }
      var slotDOM = this.dom;
      if (this.attachments.length === 0) {
        slotDOM.innerHTML = '';
      }
      if (this.attachments.length > 0) {
        slotDOM.appendChild(document.createElement('hr'));
      }
      slotDOM.appendChild(plugin.render());
      this.attachments.push(plugin);
      plugin.TabIndex = this.attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      var slotDOM = this.dom;
      slotDOM.innerHTML = '';
      for (let i = 0 ; i < this.attachments.length ; i++) {
        if (i > 0) {
          slotDOM.appendChild(document.createElement('hr'));
        }
        slotDOM.appendChild(this.attachments[i].render());
      }
    };
  };
      
  // ---------------- private methods ----------------
  
  function load_plugin(packageJSON) {
    var slotId = slots[packageJSON.dicoogle['slot-id']];
    if (!slotId) {
      console.error('Unexistent slot ID ', packageJSON.dicoogle['slot-id'], '!');
      return;
    }
    $.getScript(base_url+'webui?module=' + packageJSON.name).done(function () {
      console.log('Loaded plugin:', packageJSON.name);
    });
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
  
  return m;
})();
