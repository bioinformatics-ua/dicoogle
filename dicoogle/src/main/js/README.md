# Dicoogle Web Core

This JavaScript project aims to provide the backbone for Dicoogle Web UI plugins.

## Building

**npm** is required.

    npm install
    npm run-script build

## Using

Add the resulting "build/dicoogle-webcore.js" as a `<script>` to the Dicoogle web page.
Invoke `DicoogleWeb.init()` to automatically fetch and attach plugins.


## Creating plugins

You can create your own plugins by providing a directory containing two essential files: a plugin descriptor and a JavaScript module.

### Plugin descriptor

A descriptor takes the form of a "package.json", an `npm` package descriptor.
It contemplate at least these attributes:

 - **name** : the unique name of the plugin
 - **version** : the version of the plugin
 - **description** _(optional)_ : a simple, one-line description of the package
 - **dicoogle** : an object containing Dicoogle-specific information:
      - _caption_ : an appropriate title for being shown in the web page
      - _slot-id_ : the unique ID of the slot where this plugin is meant to be attached
      - _module-file_ : the name of the file containing the JavaScript module
 
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
It must use the CommonJS module format (like in Node.js) to expose a constructor function. Instances must have
a `render()` function, and will have access to a `DicoogleWeb` object for interfacing with Dicoogle. If the
plugin is to be attached to a result slot, it must also implement `onResult(result)`. Query plugins can invoke
`issueQuery(...)` to perform a query and expose the results on the page (via result plugins). Other REST
services exposed by Dicoogle are easily accessible with `request(...)`.

Modules are meant to work independently, but they can have embedded libraries if so is desired (such as React).

Below is an example of a plugin.

```javascript
var ExamplePlugin = function() {

  // ...

  this.render = function() {
    var e = document.create('span');
    e.innerHTML('This method must return a DOM element.');
    return e;
  };
};
module.exports = ExamplePlugin;
```

