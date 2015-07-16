# Dicoogle Web Core

This JavaScript project aims to provide the backbone for Dicoogle Web UI plugins.
The essence of this architecture is that Dicoogle web pages will contain stub slots where plugins can be attached to.

## Building

The building process of Dicoogle Web Core is carried out by `grunt` (the "build" script in the package will execute the default task).
Install **npm** if not already installed, and perform the following commands in this directory:

    # install development dependencies
    npm install
    # then execute the task runner
    grunt
    # or run the package's build script
    npm run-script build

## Using 

 - Include a module management library that supports synchronous module loading. RequireJS is preferred (RequireJS is supported,
   but libraries must be asynchronously loaded before initializing the web core).
 - Include the resulting "dist/dicoogle-webcore.js" in your page. An HTML `<script>` element or another means of importing
   the module is sufficient.
 - Place `<dicoogle-slot>` elements in the page. They must contain a unique slot id attribute `data-slot-id`.
 - Invoke the module's `init()` to automatically detect slots, as well as to fetch and attach plugins. This should only be called once. New slots attached dynamically will be automatically filled.

A few examples of web pages using the Dicoogle Web Core are available in "test/TC".

## Runtime Dependencies

Dicoogle Web Core requires HTML custom element support.
Include the "document-register-elements" script in order to extend HTML5 custom element support to other browsers.

## Creating plugins

You can create your own plugins by providing a directory containing two essential files: a plugin descriptor and a JavaScript module.

### Plugin descriptor

A descriptor takes the form of a "package.json", an `npm` package descriptor, containing at least these attributes:

 - `name` : the unique name of the plugin (must be compliant with npm)
 - `version` : the version of the plugin (must be compliant with npm)
 - `description` _(optional)_ : a simple, one-line description of the package
 - `dicoogle` : an object containing Dicoogle-specific information:
      - `caption` _(optional, defaults to name)_ : an appropriate title for being shown as a tab (or similar) in the web page
      - `slot-id` : the unique ID of the slot where this plugin is meant to be attached
      - `module-file` _(optional, defaults to "module.js")_ : the name of the file containing the JavaScript module

An example of a package:

```json
{
  "name" : "dicoogle-cbir-query",
  "version" : "0.0.1",
  "description" : "CBIR Query-By-Example plugin",
  "tags": ["dicoogle", "dicoogle-webui"],
  "dicoogle" : {
    "caption" : "Query by Example",
    "slot-id" : "query",
    "module-file" : "module.js"
  }
}
```

### Module

In addition, a JavaScript module must be implemented, containing the entire logic and rendering of the plugin.
The final module script must define a module in loose CommonJS format (similar to the Node.js module standard).
The developer may also choose to create the module under the UMD format. The developer can make multiple node-flavored
CommonJS modules and use tools like browserify to bundle them and embed dependencies. The exported module must be
a single constructor function, in which instances must have a `render(parent)` function, which will attach the
contents of the plugin to the `parent` DOM element. The parent will be a div with its class defined as
`"dicoogle-webcore-<slotid>_<plugin-index>"` (e.g. `"dicoogle-webcore-query_0"`). The div of these parents
will have a class `"dicoogle-webcore-<slotid>"`. The Dicoogle web application may use these classes to style these
additional UI elements.

All modules will have access to the `DicoogleWeb` plugin-local alias for interfacing with Dicoogle. If the plugin
is to be attached to a result slot, it must also implement `onResult(result)`. Query plugins can invoke
`issueQuery(...)` to perform a query and expose the results on the page (via result plugins). Other REST
services exposed by Dicoogle are easily accessible with `request(...)`. See the Dicoogle Web API below for a more
thorough documentation.

Modules are meant to work independently, but can have embedded libraries if so is desired. In
addition, if the underlying web page is known to contain specific libraries, then these can also be used without being
embedded. This is particularly useful to avoid replicating dependencies and prevent modules from being too large.

Below is an example of a plugin module.

```javascript
module.exports = function() {

  // ...

  // parent is an ordinary DOMElement
  this.render = function(parent) {
    var e = document.create('span');
    e.innerHTML = 'Hello Dicoogle!';
    parent.appendChild(e);
  };
});
```

### Dicoogle Web API

Either `require` the `dicoogle-web` module (if the page supports the operation) or use the alias `DicoogleWeb` to 
perform operations to the Dicoogle server and the page's Dicoogle web core.

#### **request** : `function(service, [data,] callback)`

Make a request to Dicoogle's web services.

 - _service_ : the relative URI of the service
 - _data_ : the data to pass to the service (by default via the HTTP request's query string)
 - _callback_ : a callback function (`function(error, result)`)

#### **issueQuery** : `function(query, options, callback)`

Issue a query to the system. This operation is asynchronous and will automatically issue back a result exposal to the
page's result module. The query service requested will be "search" unless modified with the _overrideService_ option.

 - _query_ an object or string containing the query to perform
 - _options_ an object containing additional options (such as query plugins to use, result limit, etc.)
     - _overrideService_ [string] the name of the service to use instead of "search"
 - _callback_ an optional callback function(error, result)

####  **addEventListener** : `function(eventName, fn)`

Add an event listener to an event triggered by the web core.

 - _eventName_ : the name of the event (must be one of 'load','loadMenu','loadQuery','loadResult')
 - _fn_ : a callback function (arguments vary) -- `function(...)`

#### **addResultListener** : `function(fn)`

Add a listener to the 'result' event, triggered when a query result is obtained.

 - _fn_ : `function(result, requestTime, options)`

#### **addPluginLoadListener** : `function(fn)`

Add a listener to the 'load' event, triggered when a plugin is loaded.

 - _fn_ : `function(Object{name, slotId, caption})`

#### **addMenuPluginLoadListener** : `function(fn)`

Add a listener to the 'loadMenu' event, triggered when a menu plugin is loaded.

 - _fn_ : `function(Object{name, slotId, caption})`

#### **addQueryPluginLoadListener** : `function(fn)`

Add a listener to the 'loadQuery' event, triggered when a query plugin is loaded.

 - _fn_ : `function(Object{name, slotId, caption})`
 
#### **addResultPluginLoadListener** : `function(fn)`

Add a listener to the 'loadResult' event, triggered when a result plugin is loaded.

 - _fn_ : `function(Object{name, slotId, caption})`

## Installing Plugins

Place all contents of a plugin in a directory and insert the directory (by copying or linking)
into the "WebPlugins" folder at the base working directory. Plugins can then be retrieved the
next time the Dicoogle server loads.

## Testing Plugins

Web UI plugins can be tested either in a Dicoogle server or in separate pages. For the latter, please see the HTML pages in "test/TC/" for a few examples.
