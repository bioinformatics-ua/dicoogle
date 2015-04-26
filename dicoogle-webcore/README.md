# Dicoogle Web Core

This JavaScript project aims to provide the backbone for Dicoogle Web UI plugins.
The essence of this architectue is that Dicoogle web pages will contain stub slots where plugins can be attached to.

## Building

The building process of Dicoogle Web Core is carried out by `grunt`. Install **npm**, if you haven't got it yet, and perform the following commands in this directory:

    npm install
    npm run-script build

## Using 

 - Add the resulting "build/dicoogle-webcore.js" as a `<script>` to the Dicoogle web page.
 - Place <dicoogle-slot> elements in the page. They must contain a unique slot id attribute `data-slot-id`.
 - Invoke `DicoogleWeb.init()` to automatically detect slots, as well as to fetch and attach plugins.

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
  "name" : "cbir-query",
  "version" : "0.0.1",
  "description" : "CBIR Query-By-Example plugin",
  "dicoogle" : {
    "caption" : "Query by Example",
    "slot-id" : "query",
    "module-file" : "module.js"
  }
}
```

### Module

In addition, a JavaScript module must be implemented, containing the entire logic and rendering of the plugin.
The final script must define a new named AMD module, with the exact same name of the plugin.

Ths developer may also choose to create the module under the UMD format. The developer can make a node-flavored
CommonJS module and use tools like browserify to convert it (and embed dependencies). The module must be a single
constructor function, in which instances must have a `render(parent)` function, which will attach the contents of the
plugin to the `parent` DOM element (as of 0.6.0, it should no longer return a DOM element). Most tools however
 do not support creating a named AMD module, which is why these module specifications may change in the
future.

All modules will have access to the `dicoogle-web` module for interfacing with Dicoogle. If the
plugin is to be attached to a result slot, it must also implement `onResult(result)`. Query plugins can invoke
`issueQuery(...)` to perform a query and expose the results on the page (via result plugins). Other REST
services exposed by Dicoogle are easily accessible with `request(...)`. See the Dicoogle Web API below for a more
thorough documentation.

Modules are meant to work independently, but can have embedded libraries if so is desired (such as React). In
addition, if the underlying web page is known to contain specific libraries, then these can also used without being
embedded. This is particularly useful to avoid replicating dependencies and prevent modules from being too large.

Below is an example of a plugin module (assuming file name "example.js" and plugin name "example-plugin").

```javascript
define('example-plugin', function(require) {

  var DicoogleWeb = require('dicoogle-webcore'); // use Dicoogle

  return function() {

    // ...

    // parent is an ordinary DOMElement
    this.render = function(parent) {
      var e = document.create('span');
      e.innerHTML = 'Hello Dicoogle!';
      parent.appendChild(e);
    };
  };
});
```

### Dicoogle Web API

Retrieve the `dicoogle-web` module to perform operations to the Dicoogle server and the page's Dicoogle web core.

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

## Installing Plugins

Place all contents of a plugin in a directory and insert the directory (by copying or linking) into the "WebPlugins" folder at the base working directory. Plugins can then be retrieved the next time the Dicoogle server loads.

## Testing Plugins

Web UI plugins can be tested either in a Dicoogle server or in separate pages. For the latter, please see the HTML pages in "test/TC/" for a few examples.
