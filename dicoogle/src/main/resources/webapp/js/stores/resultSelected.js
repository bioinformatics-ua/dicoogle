'use strict';

import Reflux from 'reflux';
import * as ResultSelectActions from '../actions/resultSelectAction';

/**
 * This list contains the selected results in the UI.
 * When the user wants to apply an operation in a set of results.
 */
const ResultsSelected = Reflux.createStore({
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
    onSelect: function(data) {
      console.log("Item: " );
      console.log(data);
      console.log(this._contents);

      this._contents.push(data);
    },
    onUnSelect (data) {
        let i = 0;
        for (var c of this._contents)
        {
            if (JSON.stringify(c) === JSON.stringify(data))
            {
                this._contents.splice(i, 1);
                break;
            }
            i += 1;
        }
    },
    // Send the batch of selected results to somewhere.
    onGet: function(data){
      this.trigger({
        contents: this._contents,
        level: this._level
      });
    },

    get: function(){
      console.log(this._contents);
      console.log(this._level);

      return {
        contents: this._contents,
        level: this._level
      };
    }
});

export {ResultsSelected};
