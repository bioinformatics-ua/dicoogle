/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');
import {DumpActions} from '../actions/dumpActions';
import {Endpoints} from '../constants/endpoints';
import {getImageInfo} from '../handlers/requestHandler';



/**
 * This list contains the selected results in the UI.
 * When the user wants to apply an operation in a set of results.
 * 
 * 
 * 
 */
var ResultsSelected = Reflux.createStore({
    listenables: DumpActions,
    init: function () {
       this._contents = [];
       this._level = "";
    },
    
    // Change Level 
    onLevel: function (level) {
       this._level = level;
    },
    // Clear the lists, probably changed of level. 
    onClear: function () {
       this.init();
    }, 
    // A new selection, and so need to store in the list.
    onSelect: function(data){
        this._contents.push(data);
    },
    // Send the batch of selected results to somewhere. 
    onGet: function(data){
        let self = this;
        self.trigger({
          contents:self._contents,
          level: self._level;
        });
    }
});

export {DumpStore};
