import Reflux from 'reflux';
import {Endpoints} from '../constants/endpoints';
import * as PluginActions from "../actions/pluginActions";
import {request} from '../handlers/requestHandler';
import $ from "jquery";

const PluginStore = Reflux.createStore({
  listenables: PluginActions,
  init: function () {
    this._contents = {};
  },

  onGet: function(type) {
    const self = this;

    request(Endpoints.base + "/plugins/" + type,
      function(data) {
        self._contents[type] = data.plugins;
        self.trigger({
          data: self._contents[type],
          success: true
        });
      },
      function(xhr) {
        self.trigger({
          success: false,
          status: xhr.status
        });
      }
    );
  },
  
  onSetAction: function(type, name, action) {
    const self = this;

    $.ajax({
      url: Endpoints.base + "/plugins/" + type + "/" + name + "/" + action,
      method: 'post',
      success: function(data) {
        self._contents[type].find(plugin => (plugin.name === name)).enabled = (action === "enable");
        self.trigger({
          data: self._contents[type],
          success: true
        });
      },
      error: function(xhr, status, err) {
        self.trigger({
          success: false,
          status: xhr.status,
          error: "Could not " + action + " the plugin."
        });
      }
    });
  }
});

export default PluginStore;
