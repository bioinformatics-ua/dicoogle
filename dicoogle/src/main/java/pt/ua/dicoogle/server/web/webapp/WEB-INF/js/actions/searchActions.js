/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');
var ActionCreators = exports;
ActionCreators.search = Reflux.createAction();
ActionCreators.unindex = Reflux.createAction();
ActionCreators.remove = Reflux.createAction();
ActionCreators.advancedOptionsChange = Reflux.createAction();
export { ActionCreators };
