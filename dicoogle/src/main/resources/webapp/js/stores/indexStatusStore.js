'use strict';

import Reflux from 'reflux';
import {IndexStatusActions} from '../actions/indexStatusAction';
import {forceIndex} from '../handlers/requestHandler';
import dicoogleClient from 'dicoogle-client';

const Dicoogle = dicoogleClient();

const IndexStatusStore = Reflux.createStore({
    listenables: IndexStatusActions,
    init: function() {
       this._contents = {};
    },

    onGet: function() {

      Dicoogle.getRunningTasks((error, data) => {
        if (error) {
          this.trigger({
              success: false,
              status: error.status,
              error
            });
          return;
        }

        this._contents = data;
        this.trigger({
          data: this._contents,
          success: true
        });

      });
    },

    onStart: function(uri){
      forceIndex(uri);

      this._contents.tasks.push({taskUid: "...", taskName: uri, taskProgress: -1}); // TODO show loading instead
      this._contents.count = this._contents.count + 1;
      this.trigger({
        data: this._contents,
        success: true
      });

      console.log(this._contents);
    },

    onClose: function(uid) {
      Dicoogle.closeTask(uid, (error) => {
        console.log("closeTask: ", error || 'ok');
        for (let i = 0; i < this._contents.tasks.length; i++)
        {
          if (this._contents.tasks[i].taskUid === uid) {
            this._contents.tasks.splice(i, 1);
            break;
          }
        }
        this.trigger({
          data: this._contents,
          success: true
        });
      });
    },
    onStop: function(uid){
      console.log("Stop: ", uid);
      Dicoogle.stopTask(uid, (error) => {
        console.log("stopTask: ", error || 'ok');
      });
    }

});

export {IndexStatusStore};
