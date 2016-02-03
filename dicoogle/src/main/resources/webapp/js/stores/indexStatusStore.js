'use strict';

import Reflux from 'reflux';
import {IndexStatusActions} from '../actions/indexStatusAction';
import {Endpoints} from '../constants/endpoints';
import {forceIndex} from '../handlers/requestHandler';
import $ from 'jquery';
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

    onClose: function(uid){

      // TODO use Dicoogle client
      $.post(Endpoints.base + "/index/task",
      {
        uid: uid,
        action: "delete",
        type: "close"
      },
        function(data, status){
          //Response
          console.log("Data: ", data, " ; Status: ", status);
        });

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
    },
    onStop: function(uid){
      console.log("Stop: ", uid);
      // TODO use Dicoogle client
      $.post(Endpoints.base + "/index/task",
      {
        uid: uid,
        action: "delete",
        type: "stop"
      }, function(data, status) {
        //Response
        console.log("Data: ", data, " ; Status: ", status);
      });
    }

});

export {IndexStatusStore};
