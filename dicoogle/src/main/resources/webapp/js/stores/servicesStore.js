import Reflux from 'reflux';
import ServiceAction from '../actions/servicesAction';
import {Endpoints} from '../constants/endpoints';
import $ from 'jquery';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient();

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


    onGetStorage() {
      Dicoogle.getStorageServiceStatus((error, data) => {
        if (error) {
          console.log("onGetStoreage: failure", error);
          return;
        }

        this._contents.storageRunning = data.isRunning;
        this._contents.storagePort = data.port;
        this._contents.storageAutostart = data.autostart;
        this.trigger(this._contents);
      });
    },

    onGetQuery() {
      Dicoogle.getQueryRetrieveServiceStatus((error, data) => {
        if (error) {
          console.log("onGetStoreage: failure");
          return;
        }

        this._contents.queryRunning = data.isRunning;
        this._contents.queryPort = data.port;
        this._contents.queryAutostart = data.autostart;
        this.trigger(this._contents);
      });
    },
    onSetStorage (running) {
      const callback = (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
          //Response
          this._contents.storageRunning = running;
          this.trigger(this._contents);
      }
      if (running) {
        Dicoogle.startStorageService(callback);
      } else {
        Dicoogle.stopStorageService(callback);
      }
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
    onSetQuery (running) {
      const callback = (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
          //Response
          this._contents.queryRunning = running;
          this.trigger(this._contents);
      }
      if (running) {
        Dicoogle.startQueryRetrieveService(callback);
      } else {
        Dicoogle.stopQueryRetrieveService(callback);
      }
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

    onGetQuerySettings: function() {
      Dicoogle.request('GET', ['management', 'settings', 'dicom', 'query'], {}, (error, data) => {
        if (error) {
          console.log("onGetQuerySettings: failure");
          return;
        }
        if (typeof data.text === 'string') {
          data = JSON.parse(data.text);
        }
        this._querySettings = data;
        this._contents.querySettings = this._querySettings;
        this.trigger(this._contents);
      });
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
