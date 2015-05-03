var Reflux = require('reflux');
var ServiceAction = exports;
ServiceAction.getStorage = Reflux.createAction();
ServiceAction.getQuery = Reflux.createAction();
ServiceAction.setStorage = Reflux.createAction();
ServiceAction.setQuery = Reflux.createAction();

export { ServiceAction };
