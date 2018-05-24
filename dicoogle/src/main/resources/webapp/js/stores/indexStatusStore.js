"use strict";

import Reflux from "reflux";
import { IndexStatusActions } from "../actions/indexStatusAction";
import { forceIndex } from "../handlers/requestHandler";

import dicoogleClient from "dicoogle-client";

const IndexStatusStore = Reflux.createStore({
  listenables: IndexStatusActions,
  init: function() {
    this._contents = {};

    this.dicoogle = dicoogleClient();
  },

  onGet: function() {
    this.dicoogle.tasks.list((error, data) => {
      if (error) {
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

  onStart: function(uri, providers) {
    forceIndex(uri, providers, error => {
      if (error) {
        this.trigger({
          success: false,
          status: error.status
        });
        return;
      }

      this.onGet();
    });
  },

  onClose: function(uid) {
    this.dicoogle.tasks.close(uid, error => {
      console.log("closeTask: ", error || "ok");

      if (error) {
        this.trigger({
          success: false,
          status: error.status
        });
        return;
      }

      for (let i = 0; i < this._contents.tasks.length; i++) {
        if (this._contents.tasks[i].taskUid === uid) {
          this._contents.tasks.splice(i, 1);
          break;
        }
      }
      this.trigger({
        data: this._contents,
        success: true
      });
    });
  },

  onStop: function(uid) {
    console.log("Stop: ", uid);
    this.dicoogle.tasks.stop(uid, error => {
      console.log("stopTask: ", error || "ok");
    });
  }
});

export { IndexStatusStore };
