import Reflux from 'reflux';
import $ from 'jquery';

import {StorageActions} from '../actions/storageActions';
import {Endpoints} from '../constants/endpoints';

const StorageStore = Reflux.createStore({
    listenables: StorageActions,
    init() {
       this._contents = [];
    },

    onGet(data){

      $.ajax({
        url: Endpoints.base + "/management/settings/storage/dicom",
        dataType: 'json',
        success: (data) => {
          this._contents = data.map((store) => ({
            aetitle: store.AETitle,
            ip: store.ipAddrs,
            port: store.port,
            description: store.description,
            public: store.isPublic
          }));
          this.trigger({
            data: this._contents,
            success: true
          });
        },
        error: (xhr, status, err) => {
          //FAILURE
          this.trigger({
              success: false,
              status: xhr.status
            });
        }
      });

    },
    onAdd(aetitle, ip, port, description, isPublic) {
      console.log("Onadd clicked 2");

      $.post(Endpoints.base + "/management/settings/storage/dicom",
      {
        type: "add",
        aetitle,
        ip,
        port,
        description,
        public: isPublic
      },
      (data, status) => {
        this._contents.push({aetitle, ip, port, description, public: isPublic});
        //Response
        console.log("Data: " + data + "\nStatus: " + status);
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    },
    onRemove(index) {
      const {aetitle, ip, port, description} = this._contents[index];
      const isPublic = this._contents[index].public;
      $.post(Endpoints.base + "/management/settings/storage/dicom",
      {
        type: "remove",
        aetitle,
        ip,
        port,
        description,
        public: isPublic
      },
      (data, status) => {
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          this._contents.splice(index, 1);
          this.trigger({
            data: this._contents,
            success: true
          });
        });

    }

});

export {StorageStore};
