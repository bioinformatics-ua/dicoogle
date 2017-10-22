import Reflux from 'reflux';
import {Endpoints} from '../constants/endpoints';
import {PluginActions} from "../actions/pluginActions";
import {request} from '../handlers/requestHandler';

const PluginStore = Reflux.createStore({
  listenables: PluginActions,
  init: function () {
    this._contents = {};
  },

  onGet: function(type) {
    const self = this;

    request(Endpoints.base + "/plugins" + "/" + type,
      function(data){
        self._contents = data.plugins;
        self.trigger({
          data: self._contents,
          success: true
        });
      },
      function(xhr){
        self.trigger({
          success: false,
          status: xhr.status
        });
      }
    );
  }
});

export {PluginStore};
