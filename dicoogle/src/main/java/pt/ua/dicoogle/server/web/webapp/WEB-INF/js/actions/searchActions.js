/*jshint esnext: true*/
'use strict';


var Reflux = require('reflux');
var ActionCreators = exports;
ActionCreators.search = Reflux.createAction();
ActionCreators.unindex = Reflux.createAction();
export { ActionCreators };
