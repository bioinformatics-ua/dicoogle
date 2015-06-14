/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {ProvidersActions} from '../actions/providersActions';

import {Endpoints} from '../constants/endpoints';

import {request} from '../handlers/requestHandler';

var ProvidersStore = Reflux.createStore({
    listenables: ProvidersActions,
    init: function () {
       this._providers = [];
    },


    onGet : function(data){
      var self = this;
      console.log("BALO");
      if(this._providers.length != 0)
      {
        console.log("BILO",self._providers);
        self.trigger({
          data:self._providers,
          success: true
        });
        return;
      }
      console.log("XUPALO");

      request(Endpoints.base + "/providers" ,
        function(data){
          //SUCCESS
          console.log("success", data);
          self._providers = data;
          self._providers.splice(0, 0, "All providers");

          self.trigger({
            data:self._providers,
            success: true
          });
        },
        function(xhr){
          //FAILURE
          self.trigger({
              success:false,
              status: xhr.status
            });
        }
      );


    }
});

export {ProvidersStore};
