import Reflux from 'reflux';

import {ActionCreators} from '../actions/searchActions';

import {getPatients, unindex, remove} from '../handlers/requestHandler';

const SearchStore = Reflux.createStore({
    listenables: ActionCreators,
    init: function () {
        this._contents = {advancedOptions: false};
        //this.listenTo(ActionCreators, "request");

        // subscribe to listen for whole ProductStore first as there is no `waitFor` in Reflux
        // (https://github.com/voronianski/flux-samples/blob/master/facebook-flux/js/stores/CartStore.js#L55)
    },

    request: function(url){

    },

    onSearch: function(data){
      var self = this;
      getPatients(data.text, data.keyword, data.provider,
        function(data){
          //SUCCESS
          console.log("success", data);
          data["advancedOptions"] = self._contents.advancedOptions;
          self._contents = data;

          //Trigger search
          self.triggerWithDelay()
        },
        function(xhr){
          //FAILURE
          self.trigger({
              success: false,
              status: xhr.status
            });
        }
      );
    },
    onUnindex: function(uris, provider){
      console.log("fired action");
      console.log(uris);

      unindex(uris, provider,
          function() {
        console.log("sucess");
      }, function(){
        console.log("Error");
      });
    },
    onRemove: function(uris){
      unindex(uris, "all",
          function() {
        console.log("sucess");
      }, function(){
        console.log("Error");
      });
      remove(uris,
          function() {
        console.log("sucess");
      }, function(){
        console.log("Error");
      });
    },

  /* onAdvancedOptionsChange: function(){
      this._contents.advancedOptions = !this._contents.advancedOptions;
      this.trigger({
        data:this._contents,
        success: true
      });
    },
  */
    triggerWithDelay: function(){
      this.trigger({
        data: this._contents,
        success: true
      });
    }

});

export {SearchStore};

window.store = SearchStore;
