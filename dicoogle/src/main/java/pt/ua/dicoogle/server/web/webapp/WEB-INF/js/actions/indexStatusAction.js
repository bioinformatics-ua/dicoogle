var Reflux = require('reflux');
var IndexStatusActions = exports;
IndexStatusActions.get = Reflux.createAction();
IndexStatusActions.start = Reflux.createAction();
IndexStatusActions.stop = Reflux.createAction();
IndexStatusActions.close = Reflux.createAction();

export { IndexStatusActions };
