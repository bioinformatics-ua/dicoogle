'use strict';

import Reflux from 'reflux';
import {ProvidersActions} from '../actions/providersActions';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';

var ProvidersStore = Reflux.createStore({
    listenables: ProvidersActions,
    init: function () {
       this._providers = [];
    },

    onGet: function(data){
      var self = this;
      if(this._providers.length !== 0)
      {
        self.trigger({
          data: self._providers,
          success: true
        });
        return;
      }

      request(Endpoints.base + "/providers",
        function(data){
          //SUCCESS
          console.log("success", data);
          self._providers = data;
          self._providers.splice(0, 0, "All providers");

          self.trigger({
            data: self._providers,
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

export {ProvidersStore};
