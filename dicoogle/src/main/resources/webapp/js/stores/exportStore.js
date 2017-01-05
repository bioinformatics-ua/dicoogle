import * as Reflux from 'reflux';
import dicoogleClient from 'dicoogle-client';
import * as ExportActions from '../actions/exportActions';
import {Endpoints} from '../constants/endpoints';
import {getDICOMFieldList} from '../handlers/requestHandler';

const ExportStore = Reflux.createStore({
    listenables: ExportActions,
    init: function () {
       this._contents = {};
       this.dicoogle = dicoogleClient();
    },

    onGetFieldList: function(data){
      const self = this;
      getDICOMFieldList((error, data) => {
        if (error) {
          //FAILURE
          self.trigger({
              success: false,
              status: error.status
            });
          return;
        }

        //SUCCESS
        //console.log("success", data);
        self._contents = data;

        self.trigger({
          data: self._contents,
          success: true
        });
      });
    },

    onExportCSV: function(data, fields){

      let {text, keyword, provider} = data;
      if(text.length === 0)
      {
        text = "*:*";
        keyword = true;
      }

      this.dicoogle.issueExport(text, fields, {keyword, providers: provider}, (error, id) => {
          if (error) {
            console.error("Failed to issue the export:", error);
            return;
          }
          // create a download link and trigger it automatically
          const link = document.createElement("a");
          const hacked_footer = document.getElementById("hacked-modal-footer-do-not-remove");
          link.style.visibility = "hidden";
          link.download = "file";
          link.href = Endpoints.base + "/exportFile?UID=" + id;
          hacked_footer.appendChild(link);
          link.click();
          hacked_footer.removeChild(link);
      });

    }
});

export {ExportStore};
