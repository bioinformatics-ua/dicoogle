

var Reflux = require('reflux');
var StorageActions = exports;
StorageActions.get = Reflux.createAction();
StorageActions.add = Reflux.createAction();
StorageActions.remove = Reflux.createAction();

export { StorageActions };
