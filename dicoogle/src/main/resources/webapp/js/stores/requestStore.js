import Reflux from "reflux";

import { RequestActions } from "../actions/requestActions";

const RequestStore = Reflux.createStore({
  listenables: RequestActions,

  init: function() {
    this._contents = "";
  },

  onQuery: function(data) {
    this._contents = data
  },

  get: function() {
    return {
      contents: this._contents
    };
  }
});

export { RequestStore };
