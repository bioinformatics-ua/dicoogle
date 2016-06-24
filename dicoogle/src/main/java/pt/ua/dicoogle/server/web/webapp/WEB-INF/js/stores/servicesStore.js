import Reflux from 'reflux';
import ServiceAction from '../actions/servicesAction';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';
import $ from 'jquery';

const ServicesStore = Reflux.createStore({
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
        responseTimeout: "..."
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

    onGetStorage: function(){
      request(
        Endpoints.base + "/management/dicom/storage",
          (data) => {
            this._contents.storageRunning = data.isRunning;
            this._contents.storagePort = data.port;
            this._contents.storageAutostart = data.autostart;
            this.trigger(this._contents);
          },
          function(error) {
            console.log("onGetStoreage: failure");
          }
      );
    },
    onGetQuery: function(){
      request(
        Endpoints.base + "/management/dicom/query",
        (data) => {
          this._contents.queryRunning = data.isRunning;
          this._contents.queryPort = data.port;
          this._contents.queryAutostart = data.autostart;
          this.trigger(this._contents);

        }, (error) => {
          console.log("onGetStoreage: failure");
        }
      );
    },
    onSetStorage: function(state){
      $.post(Endpoints.base + "/management/dicom/storage",
      {
        running: state
      }, (data, status) => {
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.storageRunning = state;
          this.trigger(this._contents);
        });

    },
    onSetStorageAutostart (enabled) {
      $.post(Endpoints.base + "/management/dicom/storage",
      {
        autostart: enabled
      },
        (data, status) => {
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.storageAutostart = enabled;
          this.trigger(this._contents);
        });
    },

    onSetStoragePort(port) {
      $.post(Endpoints.base + "/management/dicom/storage", {
        port
      }, (data, status) => {
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.storagePort = port;
          this.trigger(this._contents);
        });
    },

    onSetQuery: function(state){
      $.post(Endpoints.base + "/management/dicom/query",
      {
        running: state
      },
        (data, status) => {
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.queryRunning = state;
          this.trigger(this._contents);

        });
    },
    onSetQueryAutostart (enabled) {
      $.post(Endpoints.base + "/management/dicom/query",
      {
        autostart: enabled
      }, (data, status) => {
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.queryAutostart = enabled;
          this.trigger(this._contents);
      });
    },

    onSetQueryPort(port) {
      $.post(Endpoints.base + "/management/dicom/query", {
        port
      }, (data, status) => {
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.queryPort = port;
          this.trigger(this._contents);
        });
    },

    onGetQuerySettings: function(){
      request(
        Endpoints.base + "/management/settings/dicom/query",
        (data) => {
          this._querySettings = data;
          this._contents.querySettings = this._querySettings;
          this.trigger(this._contents);
        }, (error) => {
          console.log("onGetQuerySettigns: failure");
        }

      );
    },
  onSaveQuerySettings: function(connectionTimeout, acceptTimeout, idleTimeout, maxAssociations, maxPduReceive, maxPduSend, responseTimeout) {
    $.post(Endpoints.base + "/management/settings/dicom/query",
    {
      connectionTimeout,
      acceptTimeout,
      idleTimeout,
      maxAssociations,
      maxPduReceive,
      maxPduSend,
      responseTimeout
    },
      (data, status) => {
        //Response
        console.log("Data: " + data + "\nStatus: " + status);
      });
  }

});

export default ServicesStore;
