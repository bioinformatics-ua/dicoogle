import Reflux from 'reflux';
import {DumpActions} from '../actions/dumpActions';
import {getImageInfo} from '../handlers/requestHandler';

const DumpStore = Reflux.createStore({
    listenables: DumpActions,
    init: function () {
       this._contents = {};
    },

    onGet: function(data){
      var self = this;
      getImageInfo(data,
        function(data){
          //SUCCESS
          console.log("success", data);
          self._contents = data;


          self.trigger({
            data: self._contents,
            success: true
          });
        },
        function(xhr){
          //FAILURE
          self.trigger({
              success: false,
              status: xhr.status
            });
        }
      );


    }
});

export {DumpStore};
