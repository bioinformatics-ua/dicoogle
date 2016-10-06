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
          this._contents = data;
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
    onAdd(aetitle, ip, port) {
      console.log("Onadd clicked 2");
      this._contents.push({AETitle: aetitle, ipAddrs: ip, port});

      $.post(Endpoints.base + "/management/settings/storage/dicom",
      {
        type: "add",
        aetitle,
        ip,
        port
      },
      (data, status) => {
        //Response
        console.log("Data: " + data + "\nStatus: " + status);
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    },
    onRemove(index) {
      const aetitle = this._contents[index].AETitle;
      const ip = this._contents[index].ipAddrs;
      const port = this._contents[index].port;
      $.post(Endpoints.base + "/management/settings/storage/dicom",
      {
        type: "remove",
        aetitle,
        ip,
        port
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
