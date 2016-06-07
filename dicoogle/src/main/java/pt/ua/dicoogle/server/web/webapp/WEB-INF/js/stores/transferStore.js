import Reflux from 'reflux';
import {TransferActions} from '../actions/transferActions';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';
import $ from 'jquery';

const TransferStore = Reflux.createStore({
    listenables: TransferActions,
    init: function () {
       this._contents = {};
    },
    getSizeOptions: function() {
        return Object.keys(this._contents).length;
    },
    onGet: function() {
      console.log("onGet");
      //Check if store is a non-empty object
      if(Object.keys(this._contents).length !== 0) {
        this.trigger({
          data: this._contents,
          success: true
        });
        return;
      }
      let url = Endpoints.base + "/management/settings/transfer";
      request(url, (data) => {
          //SUCCESS
          console.log("success", data);
          this._contents = data;

          this.trigger({
            data: this._contents,
            success: true
          });
        }, (xhr) => {
          //FAILURE
          this.trigger({
              success: false,
              status: xhr.status
            });
        });
    },

    onSelectAll() {
        this.select(true);
    },
    onUnSelectAll() {
        this.select(false);
    },

    selec(value) {
        for (let index of this._contents)
        {
            for (let indexOptions of index.options)
            {
                indexOptions.value = value;
                this.request(index.uid, indexOptions.name, indexOptions.value);
            }
        }
        this.trigger({
            data: this._contents,
            success: true
        });

    },
    request(uid, id, value) {

        $.post(Endpoints.base + "/management/settings/transfer", {
            uid: uid,
            option: id,
            value: value
        }, (data, status) => {
            //Response
            console.log("Data: " + data + "\nStatus: " + status);
        });
    },


    onSet: function(index, indexOption, value){
      this._contents[index].options[indexOption].value = value;
      this.trigger({
        data: this._contents,
        success: true
      });
    }
});

export {TransferStore};
