import Reflux from "reflux";
import { VersionActions } from "../actions/versionAction";
import { getVersion } from "../handlers/requestHandler";

const VersionStore = Reflux.createStore({
  listenables: VersionActions,
  init: function() {
    this._contents = {};
  },

  onGet: function() {
    const self = this;
    getVersion(
      function(data) {
        //SUCCESS
        console.log("success", data);
        self._contents = data;

        self.trigger({
          data: self._contents,
          success: true
        });
      },
      function(xhr) {
        //FAILURE
        self.trigger({
          success: false,
          status: xhr.status
        });
      }
    );
  }
});

export { VersionStore };
