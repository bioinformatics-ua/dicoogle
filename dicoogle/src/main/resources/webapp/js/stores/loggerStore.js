'use strict';

import Reflux from 'reflux';
import {LoggerActions} from '../actions/loggerActions';

import dicoogleClient from 'dicoogle-client';

const LoggerStore = Reflux.createStore({
    listenables: LoggerActions,
    init: function () {
      this._contents = {};

      this.dicoogle = dicoogleClient();
    },

    onGet: function() {
      this.dicoogle.getRawLog((error, log) => {
        if (error) {
          this.trigger({
            success: false,
            status: error.status
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
