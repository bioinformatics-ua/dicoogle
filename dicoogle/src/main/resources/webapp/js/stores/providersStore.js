import Reflux from 'reflux';
import {ProvidersActions} from '../actions/providersActions';
import {Endpoints} from '../constants/endpoints';
import dicoogleClient from 'dicoogle-client';
//import {request} from '../handlers/requestHandler';

const Dicoogle = dicoogleClient(Endpoints.base);

const ProvidersStore = Reflux.createStore({
    listenables: ProvidersActions,
    init: function () {
       this._providers = [];
    },

    onGet: function() {
      if(this._providers.length !== 0) {
        this.trigger({
          data: this._providers,
          success: true
        });
        return;
      }

      Dicoogle.getQueryProviders((error, providers) => {
        if (error) {
          //FAILURE
          this.trigger({
              success: false,
              status: error.status,
              error
            });
          return;
        }
        //SUCCESS
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
