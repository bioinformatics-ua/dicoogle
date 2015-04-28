/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {IndexStatusActions} from '../actions/indexStatusAction';

import {Endpoints} from '../constants/endpoints';

import {request, forceIndex} from '../handlers/requestHandler';

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

      self._contents.results.push({taskUid: "NA", taskName: uri, taskProgress: 0})
      self._contents.count = self._contents.count +1;
      self.trigger({
        data:self._contents,
        success: true
      });

      console.log(this._contents);
    },

    onClose : function(uid){
      $.ajax({
        url: "http://localhost:8080/index/task?type=close&uid="+uid,
        type: 'DELETE',
        success: function(result) {
            console.log(result);
        }
      });

      for(var i =0; i<this._contents.results.length; i++)
      {
        if(this._contents.results[i].uid == uid)
          this._contents.results.splice(i,1);
      }
      this.trigger({
        data:this._contents,
        success: true
      });
    }

});

export {IndexStatusStore};
