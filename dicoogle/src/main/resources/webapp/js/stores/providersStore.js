'use strict';

import Reflux from 'reflux';
import {ProvidersActions} from '../actions/providersActions';

import dicoogleClient from 'dicoogle-client';

const ProvidersStore = Reflux.createStore({
    listenables: ProvidersActions,
    init: function () {
      this._providers = [];

      this.dicoogle = dicoogleClient();
    },

    onGet: function(data){
      var self = this;
      if(this._providers.length !== 0)
      {
        self.trigger({
          data: self._providers,
          success: true
        });
        return;
      }

      this.dicoogle.getQueryProviders((error, providers) => {
        if (error) {
          this.trigger({
            success: false,
            status: error.status,
            error
          });
          return;
        }

        console.log("success", providers);
        this._providers = providers;
        this.trigger({
          data: this._providers,
          success: true
        });
      });
    }
});

export {ProvidersStore};
