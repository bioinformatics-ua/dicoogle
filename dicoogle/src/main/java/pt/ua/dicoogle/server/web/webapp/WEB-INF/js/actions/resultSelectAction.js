var Reflux = require('reflux');


/**
 * The goal of this action is to handle results batch. 
 */

var ResultSelectActions = exports;
ResultSelectActions.select = Reflux.createAction();
ResultSelectActions.clear = Reflux.createAction();
ResultSelectActions.get = Reflux.createAction();

export { ResultSelectActions };
