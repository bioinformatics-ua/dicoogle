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
constructor function, in which instances must have a `render()` function returning a DOM element. Most tools
however, do not support creating a named AMD module, which is why these module specifications may change in the
future.

All modules will have access to the `dicoogle-web` module for interfacing with Dicoogle. If the
plugin is to be attached to a result slot, it must also implement `onResult(result)`. Query plugins can invoke
`issueQuery(...)` to perform a query and expose the results on the page (via result plugins). Other REST
services exposed by Dicoogle are easily accessible with `request(...)`.

Modules are meant to work independently, but they can have embedded libraries if so is desired (such as React). In
addition, if the underlying web page is known to contain specific libraries, then these can also used without being
embedded. This is particularly useful to avoid replicating dependencies and prevent modules from being too large.

Below is an example of a plugin module (assuming file name "example.js" and plugin name "example-plugin").

```javascript
define('example', function(require) {

  var DicoogleWeb = require('dicoogle-webcore'); // use Dicoogle

  return function() {

    // ...

    this.render = function() {
      var e = document.create('span');
      e.innerHTML('This method must return a DOM element.');
      return e;
    };
  };
});
```

