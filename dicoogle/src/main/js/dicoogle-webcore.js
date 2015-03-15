/*
 * Copyright (C) 2015  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

/** Dicoogle web application core.
 * This module provides support to web interface plugins.
 */
var DicoogleWeb = new (function () {

  // dependencies
  var $ = require('jquery');

  // hidden properties
  
  var slots = {};
  var plugins = {};
  var packages = {};
  var base_url = '';

  var resultListeners = [];
  var pluginLoadListeners = [];
  var menuPluginLoadListeners = [];
  var queryPluginLoadListeners = [];
  var menuPluginLoadListeners = [];

  /** @param fn function(result) */
  this.addResultListener = function (fn) {
    resultListeners.push(fn);
  };
  /** @param fn function(name, slotId) */
  this.addPluginLoadListener = function (fn) {
    pluginLoadListeners.push(fn);
  };
  /** @param fn function(name) */
  this.addMenuPluginLoadListener = function (fn) {
    menuPluginLoadListeners.push(fn);
  };
  /** @param fn function(name) */
  this.addQueryPluginLoadListener = function (fn) {
    queryPluginLoadListeners.push(fn);
  };
  /** @param fn function(name) */
  this.addResultPluginLoadListener = function (fn) {
    resultPluginLoadListeners.push(fn);
  };
  
  this.init = function(baseURL) {
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
    if (!document) {
      throw "no DOM environment!";
    }
    console.log('Initializing Dicoogle web core ...');
    
    // take all <dicoogle-slot> elements in page
    var slotsDOM = document.getElementsByTagName('dicoogle-slot');
    for (var i = 0 ; i < slotsDOM.length ; i++) {
      var elemAttributes = slotsDOM[i].attributes;
      var id = elemAttributes.id;
      if (!id) {
        console.error('Dicoogle web UI slot lacking id attribute!');
        continue;
      }
      slots[id.value] = new this.WebUISlot(id.value, rename_element(slotsDOM[i], 'div'));
      console.log('Loaded Dicoogle slot', id.value);
    }
    
    // finally, fetch plugins and load each
    this.fetchPlugins(null);
  };
  
  this.fetchPlugins = function (slotId) {
    console.log('Fetching Dicoogle web UI plugin descriptors ...');
    var uri = 'webui';
    if (slotId) {
      uri += '?slot-id=' + slotId;
    }
    $.getJSON(base_url+uri).done(function (data){
      var packageArray = data.plugins;
      for (var i = 0 ; i < packageArray.length ; i++) {
        packages[packageArray[i].name] = packageArray[i];
        load_plugin(packageArray[i]);
      }
    }).fail(function(error) {
      console.error('Failed to fetch plugin descriptors:', error);
    });
  };
  
  this.onRegister = function (pluginInstance, name) {
    if (typeof pluginInstance !== 'object' || typeof pluginInstance.render !== 'function') {
      console.error('Dicoogle web UI plugin', name, 'is corrupted');
      return;
    }
    var thisPackage = packages[name];
    var slotId = thisPackage.dicoogle['slot-id'];
    if (slotId === 'result' && typeof pluginInstance.onResult !== 'function') {
      console.error('Dicoogle web UI plugin', name, 'does not provide onResult');
      return;
    }
    console.log('Executed plugin:' + name);
    pluginInstance.Name = name;
    pluginInstance.SlotId = slotId;
    pluginInstance.Caption = thisPackage.dicoogle.caption || name;
    plugins[name] = pluginInstance;
    slots[slotId].attachPlugin(pluginInstance);
    for (var i = 0 ; i < pluginLoadListeners.length ; i++) {
      pluginLoadListeners[i](name, slotId);
    }
    if (slotId === 'query') {
      for (var i = 0 ; i < queryPluginLoadListeners.length ; i++) {
        queryPluginLoadListeners[i](name);
      }
    } else if (slotId === 'result') {
      for (var i = 0 ; i < resultPluginLoadListeners.length ; i++) {
        resultPluginLoadListeners[i](name);
      }
    } else if (slotId === 'menu') {
      for (var i = 0 ; i < menuPluginLoadListeners.length ; i++) {
        menuPluginLoadListeners[i](name);
      }
    }
  }
    
  // --------------------- Plugin-accessible methods --------------------------------
  
  /** Issue a query to the system. This operation is asynchronous
   * and will automatically issue back a result exposal.
   * @param query an object containing the query
   * @param options an object containing additional options (such as query plugins to use, result limit, etc.)
   */
  this.issueQuery = function(query, options) {
    var data = { query: query,
                 n : options.n,
                 provider : options.provider
                // TODO check for more
    };
    $.getJSON(base_url+'search', data).done(function(data) {
      dispatch_result(data, options.plugins);
    });
  };
  
  /** Make a request to Dicoogle.
   * function(service, [data,] callback)
   * @param service the name of the service
   * @param data the data to pass
   * @param callback function(error, result)
   */
  this.request = function(service, arg1, arg2) {
    var data = (typeof arg1 === 'object') ? arg1 : {};
    var callback = (typeof arg1 === 'function') ? arg1 : arg2;
    if (typeof callback !== 'function')  {
      console.error('invalid call to DicoogleWeb.request : a callback function is required');
      return;
    }
    $.getJSON(base_url+service, data)
      .done(function(result) { callback(null, result); })
      .fail(function(error) { callback(error, null); });
  }
  
  // ----------------------------------------------------------------------------
  
  this.WebUISlot = function(id, dom) {
    this.id = id;
    this.dom = dom;
    var attachments = [];
    
    this.attachPlugin = function(plugin) {
      if (plugin.SlotId !== this.id) {
        console.error('Attempt to attach plugin' + plugin.Name + 'to the wrong slot');
      }
      var slotDOM = this.dom;
      if (attachments.length === 0) {
        slotDOM.innerHTML = '';
      }
      if (attachments.length > 0) {
        slotDOM.appendChild(document.createElement('hr'));
      }
      slotDOM.appendChild(plugin.render());
      attachments.push(plugin);
      plugin.TabIndex = attachments.length - 1;
      plugin.Slot = this; // provide slot object
    };

    this.refresh = function() {
      var slotDOM = this.dom;
      slotDOM.innerHTML = '';
      for (var i = 0 ; i < attachments.length ; i++) {
        if (i > 0) {
          slotDOM.appendChild(document.createElement('hr'));
        }
        slotDOM.appendChild(attachments[i].render());
      }
    }
  };
      
  // ---------------- private methods ----------------
  
  function load_plugin(packageJSON) {
    var slotId = slots[packageJSON.dicoogle['slot-id']];
    if (!slotId) {
      console.error('Unexistent slot ID' + slotId + '!');
      return;
    }
    $.getScript(base_url+'webui?module=' + packageJSON.name).done(function () {
      console.log('Loaded plugin: ' + packageJSON.name);
    });
  };

  function rename_element(node,name) {
    var renamed = document.createElement(name); 
    for (var i = 0 ; i < node.attributes.length ; i++) {
      var a = node.attributes[i];
      renamed.setAttribute(a.nodeName, a.nodeValue);
    }
    while (node.firstChild) {
      renamed.appendChild(node.firstChild);
    }
    node.parentNode.replaceChild(renamed, node);
    return renamed;
  }
  
  function dispatch_result(result, plugins) {
    var resultSlot = slots['result'];
    for (var i = 0 ; i < resultSlot.attachments.length ; i++) {
      resultSlot.attachments[i].onResult(result);
    }
    //resultSlot.refresh();
    for (var i = 0 ; i < resultListeners.length ; i++) {
      resultListeners[i](result);
    }
  }
  
})();

module.exports = DicoogleWeb;
