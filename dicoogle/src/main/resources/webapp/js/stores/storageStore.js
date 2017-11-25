import Reflux from 'reflux';
import {StorageActions} from '../actions/storageActions';

import dicoogleClient from 'dicoogle-client';

const StorageStore = Reflux.createStore({
    listenables: StorageActions,
    init() {
      this._contents = [];

      this.dicoogle = dicoogleClient();
    },

    onGet() {
      this.dicoogle.storage.getRemoteServers((error, data) => {
        if (error) {
          console.log("onGet: failure", error);
          this.trigger({
            success: false,
            status: error.status
          });
          return;
        }

        this._contents = data;
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    },

    onAdd(aetitle, ip, port, description, isPublic) {
      this.dicoogle.storage.addRemoteServer({
          aetitle, ip, port, description, public: isPublic
        }, (error) => {
          if (error) {
            console.log("onAdd: failure", error);
            this.trigger({
              success: false,
              status: error.status
            });
            return;
          }

          this._contents.push({
            aetitle, ip, port, description, public: isPublic
          });
          this.trigger({
            data: this._contents,
            success: true
          });
        }
      );
    },

    onRemove(index) {
      const {aetitle, ip, port, description} = this._contents[index];
      const isPublic = this._contents[index].public;

      this.dicoogle.storage.removeRemoteServer({
        aetitle, ip, port, description, public: isPublic
      }, (error, removed) => {
        if (error) {
          console.log("onRemove: failure", error);
          this.trigger({
            success: false,
            status: error.status
          });
          return;
        }

        if (removed) {
          this._contents.splice(index, 1);
        }
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    }
});

export {StorageStore};
