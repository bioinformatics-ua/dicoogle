import Reflux from 'reflux';
import {Endpoints} from '../constants/endpoints';
import * as PluginActions from "../actions/pluginActions";

import dicoogleClient from 'dicoogle-client';
const Dicoogle = dicoogleClient(Endpoints.base);

const PluginStore = Reflux.createStore({
  listenables: PluginActions,
  init: function () {
    this._contents = {};
  },

  onGet: function(type) {
    const self = this;

    Dicoogle.request('GET', ['plugins', type],
      (error, data) => {
        if (!error) {
          self._contents[type] = data.plugins;
          self.trigger({
            data: self._contents[type],
            success: true
          });
        } else {
          self.trigger({
            success: false,
            status: error.status
          });
        }
      }
    );
  },
  
  onSetAction: function(type, name, action) {
    const self = this;

    Dicoogle.request('POST', ['plugins', type, name, action],
      (error, data) => {
        if (!error) {
          self._contents[type].find(plugin => (plugin.name === name)).enabled = (action === 'enable');
          self.trigger({
            data: self._contents[type],
            success: true
          });
        } else {
          self.trigger({
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
