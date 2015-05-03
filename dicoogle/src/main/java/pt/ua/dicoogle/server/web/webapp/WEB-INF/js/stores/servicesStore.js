/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {ServiceAction} from '../actions/servicesAction';

import {Endpoints} from '../constants/endpoints';

import {request} from '../handlers/requestHandler';

var ServicesStore = Reflux.createStore({
    listenables: ServiceAction,
    init: function () {
       this._storageRunning = false;
       this._storagePort = 0;
       this._queryRunning = false;
       this._queryPort = 0;
       this._contents = {
         storageRunning: false,
         storagePort: 0,
         queryRunning: false,
         queryPort: 0
         };
    },

    onGetStorage :function(){
      var self = this;
      request(
        Endpoints.base + "/management/dicom/storage",
        function(data){
          console.log("merda", data );
          self._contents.storageRunning = data.isRunning;
          self._contents.storagePort = data.port;
          self.trigger(self._contents);

        },
        function(error){
          console.log("omnGetStoreage: failure");
        }

      );
    },
    onGetQuery :function(){
      var self = this;
      request(
        Endpoints.base + "/management/dicom/query",
        function(data){
          console.log("merda", data );
          self._contents.queryRunning = data.isRunning;
          self._contents.queryPort = data.port;
          self.trigger(self._contents);

        },
        function(error){
          console.log("omnGetStoreage: failure");
        }

      );
    },
    OnSetStorage :function(){

    },
    OnSetQuery :function(){

    }


});

export {ServicesStore};
