# Dicoogle Web Core

This JavaScript project aims to provide the backbone for Dicoogle Web UI plugins.
The essence of this architecture is that Dicoogle web pages will contain stub slots where plugins can be attached to.

## Creating plugins

You can create your own plugins by providing a directory containing two essential files: a plugin descriptor and a JavaScript module.

### Plugin descriptor

A descriptor takes the form of a "package.json", a package descriptor compliant with npm, containing at least these attributes:

 - `name` : the unique name of the plugin (must be compliant with npm)
 - `version` : the version of the plugin (must be compliant with npm)
 - `description` _(optional)_ : a simple, one-line description of the package
 - `dicoogle` : an object containing Dicoogle-specific information:
      - `caption` _(optional, defaults to name)_ : an appropriate title for being shown as a tab (or similar) on the web page
      - `slot-id` : the unique ID of the slot where this plugin is meant to be attached
      - `module-file` _(optional, defaults to "module.js")_ : the name of the file containing the JavaScript module

In addition, these attributes are recommended:

  - `author` : the author of the plugin
  - `tags` : the tags "dicoogle" and "dicoogle-plugin" are recommended
  - `private` : if you do not intend to publish the plugin into an npm repository, set this to `true`.

An example of a valid "package.json":

```json
{
  "name" : "dicoogle-cbir-query",
  "version" : "0.0.1",
  "description" : "CBIR Query-By-Example plugin",
  "author": "John Doe <jdoe@somewhere.net>",
  "tags": ["dicoogle", "dicoogle-plugin"],
  "dicoogle" : {
    "caption" : "Query by Example",
    "slot-id" : "query",
    "module-file" : "module.js"
  }
}
```

### Module

In addition, a JavaScript module must be implemented, containing the entire logic and rendering of the plugin.
The final module script must be exported in CommonJS format (similar to the Node.js module standard), or using
the ECMAScript Harmony import/export notation (as in ES2015) when transpiled with Babel.
The developer may also choose to create the module under the UMD format, although this is not required. The developer
can make multiple node-flavored CommonJS modules and use tools like browserify to bundle them and embed dependencies.
Some of those however, can be required without embedding: "react" and "dicoogle-client" can be retrieved via `require`
and must be marked as external dependencies.

The exported module must be a single constructor function (or class), in which instances must have a `render(parent, slot)` method:

```javascript
/** Render and attach the contents of a new plugin instance to the given DOM element.
 * @param {DOMElement} parent the parent element of the plugin component
 * @param {DOMElement} slot the DOM element of the Dicoogle slot
 * @return Alternatively, return a React element while leaving `parent` intact. (Experimental, still unstable!)
 */
function render(parent, slot) {
    // ...
}
```

Furthermore, the `onResult` method must be implemented if the plugin is for a "result" slot:

```javascript
/** Handle result retrieval here by rendering them.
 * @param {object} results an object containing the results retrieved from Dicoogle's search service 
 */
function onResult(results) {
    // ...
}
```

All modules will have access to the `Dicoogle` plugin-local alias for interfacing with Dicoogle.
Query plugins can invoke `issueQuery(...)` to perform a query and expose the results on the page (via result plugins).
Other REST services exposed by Dicoogle are easily accessible with `request(...)`.
See the [Dicoogle JavaScript client package](https://github.com/bioinformatics-ua/dicoogle-client-js) and the Dicoogle
Web API section below for a more thorough documentation.

Modules are meant to work independently, but can have embedded libraries if so is desired. In
addition, if the underlying web page is known to contain specific libraries, then these can also be used without being
embedded. This is particularly useful to avoid replicating dependencies and prevent modules from being too large.

Below is an example of a plugin module.

```javascript
module.exports = function() {

  // ...

  this.render = function(parent, slot) {
    var e = document.create('span');
    e.innerHTML = 'Hello Dicoogle!';
    parent.appendChild(e);
  };
};
```

Exporting a class in ECMAScript 6 also works (since classes are syntatic sugar for ES5 constructors).
The code below can be converted to ES5 using Babel:

```javascript
export default class MyPluginModule() {

  render(parent) {
    let e = document.create('span');
    e.innerHTML = 'Hello Dicoogle!';
    parent.appendChild(e);
  }
};
```

### Dicoogle Web API

Either `require` the `dicoogle-client` module (if the page supports the operation) or use the alias `Dicoogle` to 
access and perform operations on Dicoogle and the page's web core. All methods described in
[`dicoogle-client`](https://github.com/bioinformatics-ua/dicoogle-client-js) are available. Furthermore, the web
core injects the following methods in the `webcore` namespace:

#### **webcore.issueQuery** : `function(query, options, callback)`

Issue a query to the system. This operation is asynchronous and will automatically issue back a result exposal to the
page's result module. The query service requested will be "search" unless modified with the _overrideService_ option.

 - _query_ an object or string containing the query to perform
 - _options_ an object containing additional options (such as query plugins to use, result limit, etc.)
     - \[_overrideService_\] {string} the name of the service to use instead of "search"
 - _callback_ an optional callback function(error, result)

####  **webcore.addEventListener** : `function(eventName, fn)`

Add an event listener to an event triggered by the web core.

 - _eventName_ : the name of the event (can be one of 'load','menu' or a custom one)
 - _fn_ : a callback function (arguments vary) -- `function(...)`

#### **webcore.addResultListener** : `function(fn)`

Add a listener to the 'result' event, triggered when a query result is obtained.

 - _fn_ : `function(result, requestTime, options)`

#### **webcore.addPluginLoadListener** : `function(fn)`

Add a listener to the 'load' event, triggered when a plugin is loaded.

 - _fn_ : `function(Object{name, slotId, caption})`

#### **webcore.addMenuPluginListener** : `function(fn)`

Add a listener to the 'menu' event, triggered when a menu plugin descriptor is retrieved.
This may be useful for a web page to react to retrievals by automatically adding menu entries.

 - _fn_ : `function(Object{name, slotId, caption})`

#### **webcore.emit**: `function(eventName, ...args)`

Emit an event through the webcore's event emitter.

 - _eventName_ : the name of the event
 - _args_ : variable list of arguments to be passed to the listeners

#### **webcore.emitSlotSignal**: `function(slotDOM, eventName, data)`

Emit a DOM custom event from the slot element.

 - _slotDOM_ : the slot DOM element to emit the event from
 - _name_ : the event name
 - _data_ : the data to be transmitted as custom event detail

### Webcore Events

Full list of events that can be used by plugins and the webapp. _(Work in Progress)_

 - "load" : Emitted when a plugin package is retrieved.
 - "menu" : Emitted when a new menu entry is added to the webapp's side bar.
 - "result" : Emitted when a list of search results is obtained from the search interface.

## Installing Plugins

Place all contents of a plugin in a directory and insert the directory (by copying or linking)
into the "WebPlugins" folder at the base working directory. Alternatively, package a "WebPlugins"
directory with the same contents in a Dicoogle plugin jar. All plugins will then be retrieved the
next time the Dicoogle server loads.

## Building and Using

> Note: These details are only relevant to developers of Dicoogle and its webapp. To learn how to develop web UI plugins, please ignore this section.

The project can be built by calling `npm install`. On Dicoogle, simply install `dicoogle-webcore` as a dependency and `import` (or `require`) the package module. Then:

 - Place `<dicoogle-slot>` elements in the page. They must contain a unique slot id attribute `data-slot-id`.
 - Invoke the module's `init()` to initialize the module. It will automatically detect slots, as well as fetch and attach plugins. This method
   should only be called once. New slots attached dynamically will be automatically filled.
 - In order to know what menu plugins are available, invoke `fetchPlugins('menu'[, callback])`.

The optional web component attribute `data-plugin-name` can be passed to the `<dicoogle-slot>` in order to retrieve a specific plugin (rather than all compatible plugins for that slot).

Furthermore, slot elements will emit a `plugin-load` custom event (not to be confused with the webcore's event emitter) each time a specific plugin is created and rendered. The event can be listened by adding a typical DOM event listener:

```javascript
slotElement.addEventListener('plugin-load', fnHandleEvent);
```

The `detail` property of the event will contain the object returned by the render method. In Dicoogle, this can be used for attaching React elements without rendering directly to the DOM.

Plugin web components will be attached to a div in the `<dicoogle-slot>` with its class defined as
`"dicoogle-webcore-<slotid>-instance"` (e.g. `"dicoogle-webcore-query-instance"`). The div of these parents
will have a class `"dicoogle-webcore-<slotid>"`. The Dicoogle web application may use these classes to style these
additional UI elements.

A few examples of web pages using the Dicoogle Web Core are available in "test/TC".

### Runtime Dependencies

Dicoogle Web Core requires HTML custom element support.
Include the "document-register-elements" script in order to extend HTML5 custom element support to other browsers.

## Testing Plugins

Web UI plugins can be tested by deploying them into Dicoogle.
