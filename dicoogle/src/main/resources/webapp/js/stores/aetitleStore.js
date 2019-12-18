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
    console.log("aetitleStore - onGetAETitle");
    return true;
    // Dicoogle.request("GET", ["plugins", type], (error, data) => {
    //   if (!error) {
    //     self._contents[type] = data.plugins;
    //     self.trigger({
    //       data: self._contents[type],
    //       success: true
    //     });
    //   } else {
    //     self.trigger({
    //       success: false,
    //       status: error.status
    //     });
    //   }
    // });
  },

  onSetAETitle: function (name) {
    console.log("aetitleStore - onSetAETitle -> " + name);
    return true;
    // Dicoogle.request("POST", ["plugins", type, name, action], (error, data) => {
    //   if (!error) {
    //     self._contents[type].find(plugin => plugin.name === name).enabled =
    //       action === "enable";
    //     self.trigger({
    //       data: self._contents[type],
    //       success: true
    //     });
    //   } else {
    //     self.trigger({
    //       success: false,
    //       status: error.status,
    //       error: "Could not " + action + " the plugin."
    //     });
    //   }
    // });
  }
});

export default AETitleStore;
