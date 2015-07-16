var Reflux = require('reflux');
var ServiceAction = exports;
ServiceAction.getStorage = Reflux.createAction();
ServiceAction.getQuery = Reflux.createAction();
ServiceAction.setStorage = Reflux.createAction();
ServiceAction.setQuery = Reflux.createAction();

ServiceAction.getQuerySettings = Reflux.createAction();
ServiceAction.saveQuerySettings = Reflux.createAction();

export { ServiceAction };
