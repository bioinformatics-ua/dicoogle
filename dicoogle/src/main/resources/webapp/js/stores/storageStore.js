import Reflux from 'reflux';

import {StorageActions} from '../actions/storageActions';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient();

const StorageStore = Reflux.createStore({
    listenables: StorageActions,
    init() {
       this._contents = [];
       this.dicoogle = dicoogleClient();
    },

    onGet(data){
      Dicoogle.storage.getRemoteServers((err, data) => {
          if (err) {
            this.trigger({
                success: false,
                status: err
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
      Dicoogle.storage.addRemoteServer({
        aetitle, ip, port, description, public: isPublic
      }, (err) => {
        if (err) {
          this.trigger({
            success: false,
            status: err
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
      });
    },
    onRemove(index) {
      const {
        aetitle, ip, port, description
      } = this._contents[index];
      const p = this._contents[index].public;

      Dicoogle.storage.removeRemoteServer({
        aetitle, ip, port, description, public: p
      }, (err, removed) => {
        if (err) {
          this.trigger({
            success: false,
            status: err
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
