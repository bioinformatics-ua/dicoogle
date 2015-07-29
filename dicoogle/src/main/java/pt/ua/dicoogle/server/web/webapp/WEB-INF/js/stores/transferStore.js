/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {TransferActions} from '../actions/transferActions';

import {Endpoints} from '../constants/endpoints';

import {request} from '../handlers/requestHandler';

var TransferStore = Reflux.createStore({
    listenables: TransferActions,
    init: function () {
       this._contents = {};
    },

    onGet : function(bilo){
      console.log("onGet");
      var self = this;

      //Check if store is a
      if(Object.keys(self._contents).length != 0)
      {
        self.trigger({
          data:self._contents,
          success: true
        });
        return;
      }


      var url = Endpoints.base + "/management/settings/transfer";
      request(url ,
        function(data){
          //SUCCESS
          console.log("success", data);
          self._contents = data;


          self.trigger({
            data:self._contents,
            success: true
          });
        },
        function(xhr){
          //FAILURE
          self.trigger({
              success:false,
              status: xhr.status
            });
        }
      );



    },

    onSet:function(index, indexOption, value){
      console.log(this._contents);
      console.log("sdf: ", index);


      this._contents[index].options[indexOption].value = value;
      this.trigger({
        data:this._contents,
        success: true
      });

    }
});

export {TransferStore};
