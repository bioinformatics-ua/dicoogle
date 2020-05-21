import Reflux from "reflux";
import { Endpoints } from "../constants/endpoints";
import * as PluginActions from "../actions/pluginActions";

import dicoogleClient from "dicoogle-client";
const Dicoogle = dicoogleClient(Endpoints.base);

const PluginStore = Reflux.createStore({
  listenables: PluginActions,
  init: function() {
    this._contents = {};
  },

  onGet: function(type) {
    Dicoogle.request("GET", ["plugins", type]).end((error, data) => {
      if (!error) {
        //console.log(data.body);
        this._contents[type] = data.body.plugins;
        this.trigger({
          data: this._contents[type],
          success: true
        });
      } else {
        //console.log(error.body);
        this.trigger({
          success: false,
          status: error.body.status
        });
      }
    });
  },

  onSetAction: function(type, name, action) {
    Dicoogle.request("POST", ["plugins", type, name, action]).end(
      (error, data) => {
        if (!error) {
          this._contents[type].find(plugin => plugin.name === name).enabled =
            action === "enable";
          this.trigger({
            data: this._contents[type],
            success: true
          });
        } else {
          this.trigger({
            success: false,
            status: error.status,
            error: "Could not " + action + " the plugin."
          });
        }
      }
    );
  }
});

export default PluginStore;
