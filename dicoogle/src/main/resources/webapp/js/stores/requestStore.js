import Reflux from "reflux";

import { RequestActions } from "../actions/requestActions";

const RequestStore = Reflux.createStore({
  listenables: RequestActions,

  init: function() {
    this._contents = "";
  },

  onQuery: function(data) {
    this._contents = data
    self.trigger({
      contents: this._contents
    });
  },

  get: function() {
    return {
      contents: this._contents
    };
  }
});

export { RequestStore };

window.store = RequestStore;
