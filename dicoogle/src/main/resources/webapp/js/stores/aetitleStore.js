import Reflux from "reflux";
import { Endpoints } from "../constants/endpoints";
import * as AETitleActions from "../actions/aetitleActions";

import dicoogleClient from "dicoogle-client";
const Dicoogle = dicoogleClient(Endpoints.base);

const AETitleStore = Reflux.createStore({
  listenables: AETitleActions,
  init: function() {
    this._contents = {};
  },

  onGetAETitle: function() {
    const self = this;

    Dicoogle.getAETitle((err, data) => {
      if (err) {
        console.error("Service failure", err);

        self.trigger({
          success: false,
          message: err
        });

        return;
      }

      self.trigger({
        success: true,
        message: data
      });
    });
  },

  onSetAETitle: function(name) {
    const self = this;

    if (!/^[ A-Za-z0-9_.+-]*$/.test(name)) {
      self.trigger({
        success: false,
        message: "Invalid AETitle provided"
      });
      return;
    }

    Dicoogle.setAETitle(name, err => {
      if (err) {
        console.error("Service failure", err);

        self.trigger({
          success: false,
          message: err
        });
      }

      self.trigger({
        success: true,
        message: name
      });
    });
  }
});

export default AETitleStore;
