/**
 * Provide a `require` function to the global scope so that web UI plugins
 * can retrieve internal dependencies. Webpack does not expose its own
 * require function to the outside unlike Browserify.
 */
const EXTERNAL_MODULES = {
  react: require("react"),
  "react-router": require("react-router"),
  reflux: require("reflux"),
  "dicoogle-webcore": require("dicoogle-webcore"),
  "dicoogle-client": require("dicoogle-client"),
  "react-bootstrap": require("react-bootstrap"),
  "react-router-bootstrap": require("react-router-bootstrap"),
  "react-bootstrap-table": require("react-bootstrap-table"),
  "react-imageloader": require("react-imageloader"),
  "react-dom": require("react-dom")
};

let outerRequire =
  window.require ||
  function require_stub(name) {
    throw new Error(`Cannot resolve module '${name}'`);
  };

window.require = function require(name) {
  let m = EXTERNAL_MODULES[name];
  return m || outerRequire(name);
};
