/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');
import {ResultSelectActions} from '../actions/resultSelectAction';
import {Endpoints} from '../constants/endpoints';
import {getImageInfo} from '../handlers/requestHandler';

/**
 * This list contains the selected results in the UI.
 * When the user wants to apply an operation in a set of results.
 */
var ResultsSelected = Reflux.createStore({
    listenables: ResultSelectActions,
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
        console.log("Item: " );
        console.log(data);
        console.log(this._contents);
        
        
        this._contents.push(data);
    },
    // Send the batch of selected results to somewhere. 
    onGet: function(data){
        let self = this;
        self.trigger({
          contents:self._contents,
          level: self._level
        });
    },
    
    get: function(){
        let self = this;
        console.log(this._contents);
        console.log(this._level);
        
        return {
          contents:self._contents,
          level: self._level
        };
    }
});

export {ResultsSelected};
