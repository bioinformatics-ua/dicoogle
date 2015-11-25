/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {IndexStatusActions} from '../actions/indexStatusAction';
import {Endpoints} from '../constants/endpoints';
import {request, forceIndex} from '../handlers/requestHandler';
import $ from 'jquery';

var IndexStatusStore = Reflux.createStore({
    listenables: IndexStatusActions,
    init: function () {
       this._contents = {};
    },

    onGet : function(data){
      var self = this;

      $.ajax({

        url: Endpoints.base+"/index/task",
        dataType: 'json',
        success: function(data) {
          self._contents = data;

          self.trigger({
            data:self._contents,
            success: true
          });

        },
        error: function(xhr, status, err) {
          //FAILURE
          self.trigger({
              success:false,
              status: xhr.status
            });
        }
      });

    },

    onStart : function(uri){
      var self = this;
      forceIndex(uri);

      self._contents.results.push({taskUid: "...", taskName: uri, taskProgress: -1})
      self._contents.count = self._contents.count +1;
      self.trigger({
        data:self._contents,
        success: true
      });

      console.log(this._contents);
    },

    onClose : function(uid){

      $.post(Endpoints.base + "/index/task",
      {
        uid: uid,
        action: "delete",
        type: "close"
      },
        function(data, status){
          //Response
          console.log("Data: ",  data, " ; Status: ", status);
        });

      for (var i = 0; i < this._contents.results.length; i++)
      {
        if (this._contents.results[i].taskUid === uid) {
          this._contents.results.splice(i,1);
          break;
        }
      }
      this.trigger({
        data:this._contents,
        success: true
      });
    },
    onStop : function(uid){
      console.log("Stop: ", uid);
      $.post(Endpoints.base + "/index/task",
      {
        uid: uid,
        action: "delete",
        type: "stop"
      },
        function(data, status){
          //Response
          console.log("Data: ",  data, " ; Status: ", status);
        });
    }

});

export {IndexStatusStore};
