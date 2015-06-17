/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {LoggerActions} from '../actions/loggerActions';

import {Endpoints} from '../constants/endpoints';

import {request} from '../handlers/requestHandler';

var LoggerStore = Reflux.createStore({
    listenables: LoggerActions,
    init: function () {
       this._contents = {};
    },


    onGet : function(data){
      var self = this;

      $.ajax({

        url: Endpoints.base+"/logger",
        dataType: 'text',
        success: function(data) {
          self._contents = data;

          self.trigger({
            data:self._contents,
            success: true
          });

        },
        error: function(xhr, status, err) {
          //FAILURE
          self.trigger({
              success:false,
              status: xhr.status
            });
        }
      });




    }
});

export {LoggerStore};
