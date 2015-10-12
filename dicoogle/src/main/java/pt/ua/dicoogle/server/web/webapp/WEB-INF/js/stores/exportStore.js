/*jshint esnext: true*/
'use strict';

var Reflux = require('reflux');
import $ from 'jquery';

import {ExportActions} from '../actions/exportActions';
import {Endpoints} from '../constants/endpoints';
import {request} from '../handlers/requestHandler';

var ExportStore = Reflux.createStore({
    listenables: ExportActions,
    init: function () {
       this._contents = {};
    },

    onGetFieldList : function(data){
      var self = this;
      var url = Endpoints.base + "/export/list";
      request(url ,
        function(data){
          //SUCCESS
          //console.log("success", data);
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

    onExportCSV : function(data, fields){

      var freetext = data.text;
      var isKeyword = data.keyword;
      if(data.text.length ==0)
      {
        freetext = "*:*";
        isKeyword = true;
      }

      console.log(fields);
      $.post(Endpoints.base + "/exportFile",
      {
        query: freetext,
        keyword: isKeyword,
        fields: JSON.stringify(fields)
      },
        function(data, status){
          //Response
          var response = JSON.parse(data);
          console.log("\NUID: "+response.uid);
          var link = document.createElement("a");
          link.download = "file";
          link.href = Endpoints.base + "/exportFile?UID="+response.uid;
          link.click();
        });

    }
});

export {ExportStore};
