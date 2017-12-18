import Reflux from 'reflux';

import {ExportActions} from '../actions/exportActions';
import {Endpoints} from '../constants/endpoints';
import {getDICOMFieldList} from '../handlers/requestHandler';

import dicoogleClient from 'dicoogle-client';
import {UserStore} from "./userStore";

const ExportStore = Reflux.createStore({
    listenables: ExportActions,
    init: function () {
      this._contents = {};

      this.dicoogle = dicoogleClient();
    },

    onGetFieldList: function() {
      var self = this;

      getDICOMFieldList((error, data) => {
        if (error) {
          self.trigger({
            success: false,
            status: error.status
          });
          return;
        }

        self._contents.fields = JSON.parse(data).sort();
        self.trigger({
          data: self._contents,
          success: true
        });
      });
    },

    onExportCSV: function(data, fields) {
      let {text, keyword, provider} = data;
      if(text.length === 0) {
        text = "*:*";
        keyword = true;
      }

      this.dicoogle.issueExport(text, fields, {keyword, provider}, (error, id) => {
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
    },

    onGetPresets: function () {
      let self = this;
      let username = UserStore._username;

      this.dicoogle.request('GET', ['presets', username])
        .end((error, data) => {
          if (error) {
            self.trigger({
              success: false,
              status: error.status
            });
            return;
          }

          self._contents.presets = JSON.parse(data.text);
          self.trigger({
            data: self._contents,
            success: true
          });
        });
    },

    onSavePresets: function (name, fields) {
      let self = this;

      let queryParams = "";
      fields.forEach((field) => (queryParams += "field=" + field + "&"));
      if (queryParams.length !== 0) queryParams.slice(0, -1);

      let username = UserStore._username;

      this.dicoogle
        .request('POST', ['presets', username, name])
        .query(queryParams)
        .then((res) => {
          if (res.status !== 200) {
            self.trigger({
              success: false,
              status: res.status
            });
            return;
          }

          // refresh list of presets
          self.onGetPresets();
        })
    }
});
export {ExportStore};
