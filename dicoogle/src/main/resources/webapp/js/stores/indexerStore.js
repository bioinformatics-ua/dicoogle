'use strict';

import Reflux from 'reflux';
import {IndexerActions} from '../actions/indexerActions';
import {getIndexerSettings} from '../handlers/requestHandler';

const IndexerStore = Reflux.createStore({
    listenables: IndexerActions,
    init: function () {
       this._contents = {};
    },

    onGet: function() {
      console.log("onGet");
      getIndexerSettings((error, data) => {
        if (error) {
          this.trigger({
            success: false,
            status: error.status
          });
          return;
        }

        console.log("success", data);
        this._contents = data;
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    }
});

export {IndexerStore};
