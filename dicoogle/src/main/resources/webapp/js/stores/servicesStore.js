import Reflux from 'reflux';
import ServiceAction from '../actions/servicesAction';
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
      Dicoogle.storage.getStatus((error, data) => {
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
      Dicoogle.queryRetrieve.getStatus((error, data) => {
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
        Dicoogle.storage.start(callback);
      } else {
        Dicoogle.storage.stop(callback);
      }
    },
    onSetStorageAutostart (enabled) {
      Dicoogle.storage.configure({autostart: enabled}, (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
          this._contents.storageAutostart = enabled;
          this.trigger(this._contents);
      });
    },

    onSetStoragePort(port) {
      Dicoogle.storage.configure({port}, (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
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
        Dicoogle.queryRetrieve.start(callback);
      } else {
        Dicoogle.queryRetrieve.stop(callback);
      }
    },
    onSetQueryAutostart (enabled) {
      Dicoogle.queryRetrieve.configure({autostart: enabled}, (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
          this._contents.queryAutostart = enabled;
          this.trigger(this._contents);
      });
    },

    onSetQueryPort(port) {
      Dicoogle.queryRetrieve.configure({port}, (error) => {
          if (error) {
            console.error('Dicoogle service error', error);
            return;
          }
          this._contents.queryPort = port;
          this.trigger(this._contents);
      });
    },

    onGetQuerySettings: function() {
      Dicoogle.queryRetrieve.getDicomQuerySettings((error, data) => {
        if (error) {
          console.error("Dicoogle service error", error);
          return;
        }
        this._querySettings = data;
        this._contents.querySettings = this._querySettings;
        this.trigger(this._contents);
      });
    },
  onSaveQuerySettings(connectionTimeout, acceptTimeout, idleTimeout, maxAssociations, maxPduReceive, maxPduSend, responseTimeout) {
    Dicoogle.queryRetrieve.setDicomQuerySettings({
      connectionTimeout,
      acceptTimeout,
      idleTimeout,
      maxAssociations,
      maxPduReceive,
      maxPduSend,
      responseTimeout
    },
      (error, data) => {
        if (error) {
          console.error("Dicoogle service error", error);
        }
      });
  }

});

export default ServicesStore;
