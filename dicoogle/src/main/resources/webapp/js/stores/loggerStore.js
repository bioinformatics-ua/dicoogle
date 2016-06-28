import Reflux from 'reflux';
import {LoggerActions} from '../actions/loggerActions';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient();

const LoggerStore = Reflux.createStore({
    listenables: LoggerActions,
    init: function () {
       this._contents = {};
    },

    onGet: function(data) {
      Dicoogle.getRawLog((error, log) => {
        if (error) {
          //FAILURE
          this.trigger({
              success: false,
              error
            });
        }
        this._contents = log;
        this.trigger({
            data: log,
            success: true
        });
      });
    }
});

export {LoggerStore};
