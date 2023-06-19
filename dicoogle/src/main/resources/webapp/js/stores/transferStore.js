import Reflux from "reflux";
import { TransferActions } from "../actions/transferActions";
import { getTransferSettings } from "../handlers/requestHandler";

import dicoogleClient from "dicoogle-client";

const TransferStore = Reflux.createStore({
  listenables: TransferActions,
  init: function() {
    this._contents = {};

    this.dicoogle = dicoogleClient();
  },

  getSizeOptions: function() {
    return Object.keys(this._contents).length;
  },

  onGet: function() {
    console.log("onGet");
    //Check if store is a non-empty object
    if (Object.keys(this._contents).length !== 0) {
      this.trigger({
        data: this._contents,
        success: true
      });
      return;
    }

    getTransferSettings((error, data) => {
      if (error) {
        this.trigger({
          success: false,
          status: error.status
        });
        return;
      }

      console.log("success", data);
      this._contents = data;
      this.trigger({
        data: this._contents,
        success: true
      });
    });
  },

  onSelectAll() {
    this.select(true);
  },

  onUnSelectAll() {
    this.select(false);
  },

  onSelectAllSOP(sopClassOption) {
    this.selectSOP(sopClassOption, true);
  },

  onUnSelectAllSOP(sopClassOption) {
    this.selectSOP(sopClassOption, false);
  },

  selectSOP(sopClassUid, value) {
    let sop = this._contents.find((sop) => sop.uid === sopClassUid);
    
    Promise.all(sop.options.map((tsOptions) => {
      tsOptions.value = value;
      return this.request(sop.uid, tsOptions.name, tsOptions.value);
    })).then(() => {
      this.trigger({
        data: this._contents,
        success: true
      });
    });
  },

  select(value) {
    for (let index of this._contents) {
      for (let indexOptions of index.options) {
        indexOptions.value = value;
        this.request(index.uid, indexOptions.name, indexOptions.value);
      }
    }
    this.trigger({
      data: this._contents,
      success: true
    });
  },

  async request(uid, id, value) {
    await this.dicoogle.setTransferSyntaxOption(uid, id, value);
  },

  onSet(index, indexOption, uid, id, value) {
    this.request(uid, id, value);
    this._contents[index].options[indexOption].value = value;
    this.trigger({
      data: this._contents,
      success: true
    });
  }
});

export { TransferStore };
