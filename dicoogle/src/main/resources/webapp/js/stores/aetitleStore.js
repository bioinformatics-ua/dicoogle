import Reflux from "reflux";
import { Endpoints } from "../constants/endpoints";
import * as AETitleActions from "../actions/aetitleActions";

import dicoogleClient from "dicoogle-client";
const Dicoogle = dicoogleClient(Endpoints.base);

const AETitleStore = Reflux.createStore({
  listenables: AETitleActions,
  init: function () {
    this._contents = {};
  },

  onGetAETitle: function () {
    const self = this;

    Dicoogle.getAETitle((err, data) => {
      if (err) {
        console.error("Service failure", err);
        return;
      }

      self.trigger({
        aetitle: data
      });
    });
  },

  onSetAETitle: function (name) {
    Dicoogle.setAETitle(name, (err) => {
      if (err) {
        console.error("Service failure", err);
        return;
      }
    });
  }
});

export default AETitleStore;
