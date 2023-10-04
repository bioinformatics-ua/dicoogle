import Reflux from "reflux";

import { RequestActions } from "../actions/requestActions";

const RequestStore = Reflux.createStore({
  listenables: RequestActions,

  init: function() {
    this._query = "";
    this._provider = "";
  },

  onQuery: function(query, provider) {
    this._query = query
    this._provider = provider
  },

  get: function() {
    return {
      query: this._query,
      provider: this._provider
    };
  }
});

export { RequestStore };
