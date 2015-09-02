/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import ServiceAction from '../actions/servicesAction';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';

var ServicesStore = Reflux.createStore({
    listenables: ServiceAction,
    init: function () {
       this._storageRunning = false;
       this._storagePort = 0;
       this._storageAutostart = false;
       this._queryRunning = false;
       this._queryPort = 0;
       this._queryAutostart = false;

      this._querySettings = {
        acceptTimeout: "...",
        connectionTimeout: "...",
        idleTimeout: "...",
        maxAssociations: "...",
        maxPduReceive: "...",
        maxPduSend: "...",
        responseTimeout: "...",
      };

      this._contents = {
        storageRunning: false,
        storagePort: 0,
        storageAutostart: false,
        queryRunning: false,
        queryPort: 0,
        queryAutostart: false,
        querySettings: this._querySettings
       };
    },

    onGetStorage :function(){
      var self = this;
      request(
        Endpoints.base + "/management/dicom/storage",
          function(data) {
            self._contents.storageRunning = data.isRunning;
            self._contents.storagePort = data.port;
            self._contents.storageAutostart = data.autostart;
            self.trigger(self._contents);
          },
          function(error) {
            console.log("onGetStoreage: failure");
          }
      );
    },
    onGetQuery :function(){
      var self = this;
      request(
        Endpoints.base + "/management/dicom/query",
        function(data){
          self._contents.queryRunning = data.isRunning;
          self._contents.queryPort = data.port;
          self._contents.queryAutostart = data.autostart;
          self.trigger(self._contents);

        },
        function(error){
          console.log("omnGetStoreage: failure");
        }

      );
    },
    onSetStorage :function(state){
      var self = this;
      console.log(state);
      $.post(Endpoints.base + "/management/dicom/storage",
      {
        running: state
      },
        function(data, status){
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          self._contents.storageRunning = state;
          self.trigger(self._contents);

        });

    },
    onSetStorageAutostart (enabled) {
      let self = this;
      $.post(Endpoints.base + "/management/dicom/storage",
      {
        autostart: enabled
      },
        function(data, status){
          console.log("Data: " + data + "\nStatus: " + status);
          self._contents.storageAutostart = enabled;
          self.trigger(self._contents);
        });
    },
    onSetQuery :function(state){
      var self = this;
      console.log(state);
      $.post(Endpoints.base + "/management/dicom/query",
      {
        running: state
      },
        function(data, status) {
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          self._contents.queryRunning = state;
          self.trigger(self._contents);

        });
    },
    onSetQueryAutostart (enabled) {
      let self = this;
      $.post(Endpoints.base + "/management/dicom/query",
      {
        autostart: enabled
      },
        function(data, status){
          console.log("Data: " + data + "\nStatus: " + status);
          self._contents.queryAutostart = enabled;
          self.trigger(self._contents);
        });
    },

    onGetQuerySettings : function(){
      var self = this;
      request(
        Endpoints.base + "/management/settings/dicom/query",
        function(data){
          self._querySettings = data;
          self._contents.querySettings = self._querySettings;
          self.trigger(self._contents);
        },
        function(error){
          console.log("onGetQuerySettigns: failure");
        }

      );
    },
  onSaveQuerySettings : function(connectionTimeout, acceptTimeout, idleTimeout,maxAssociations, maxPduReceive, maxPduSend, responseTimeout  ){
    $.post(  Endpoints.base +"/management/settings/dicom/query",
    {
      connectionTimeout:connectionTimeout,
      acceptTimeout:acceptTimeout,
      idleTimeout:idleTimeout,
      maxAssociations:maxAssociations,
      maxPduReceive: maxPduReceive,
      maxPduSend: maxPduSend,
      responseTimeout: responseTimeout
    },
      function(data, status){
        //Response
        console.log("Data: " + data + "\nStatus: " + status);

      });
  }


});

export default ServicesStore;
