'use strict';

import Reflux from 'reflux';
import {IndexerActions} from '../actions/indexerActions';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';

const IndexerStore = Reflux.createStore({
    listenables: IndexerActions,
    init: function () {
       this._contents = {};
    },

    onGet: function(){
      console.log("onGet");
      var self = this;
      var url = Endpoints.base + "/management/settings/index";
      request(url,
        function(data){
          //SUCCESS
          console.log("success", data);
          self._contents = data;

          self.trigger({
            data: self._contents,
            success: true
          });
        },
        function(xhr){
          //FAILURE
          self.trigger({
              success: false,
              status: xhr.status
            });
        }
      );

    }
});

export {IndexerStore};
