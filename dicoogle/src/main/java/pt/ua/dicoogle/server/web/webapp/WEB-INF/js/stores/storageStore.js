/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');

import {StorageActions} from '../actions/storageActions';

import {Endpoints} from '../constants/endpoints';

import {request} from '../handlers/requestHandler';

var StorageStore = Reflux.createStore({
    listenables: StorageActions,
    init: function () {
       this._contents = [];
    },


    onGet : function(data){
      var self = this;

      $.ajax({

        url: Endpoints.base+"/management/settings/storage/dicom",
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
    onAdd: function(ae, ip, port){
      console.log("Onadd clicked 2");
      this._contents.push({AETitle: ae, ipAddrs: ip, port:port});

      var self = this;
      $.post("http://localhost:8080/management/settings/storage/dicom",
      {
        type: "add",
        aetitle: ae,
        ip: ip,
        port: port
      },
        function(data, status){
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          self.trigger({
            data:self._contents,
            success: true
          });
        });

    },
    onRemove: function(index){
      var ae = this._contents[index].AETitle;
      var ip = this._contents[index].ipAddrs;
      var port = this._contents[index].port;
      var self = this;
      $.post("http://localhost:8080/management/settings/storage/dicom",
      {
        type: "remove",
        aetitle: ae,
        ip: ip,
        port: port
      },
        function(data, status){
          //Response
          console.log("Data: " + data + "\nStatus: " + status);
          self._contents.splice(index,1);
          self.trigger({
            data:self._contents,
            success: true
          });
        });


    }

});

export {StorageStore};
