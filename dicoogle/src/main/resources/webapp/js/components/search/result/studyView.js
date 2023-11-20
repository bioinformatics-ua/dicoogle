import React from "react";
import createReactClass from "create-react-class";
import { SearchStore } from "../../../stores/searchStore";
import { BootstrapTable, TableHeaderColumn } from "react-bootstrap-table";

import { ActionCreators } from "../../../actions/searchActions";
import ConfirmModal from "./confirmModal";
import PluginView from "../../plugin/pluginView.jsx";
import { Checkbox } from "react-bootstrap";
import ResultSelectActions from "../../../actions/resultSelectAction";

import UserStore from "../../../stores/userStore";

const StudyView = createReactClass({
  getInitialState: function() {
    // We need this because refs are not updated in BootstrapTable.
    this.refsClone = {};
    return {
      data: [],
      status: "loading",
      unindexSelected: null,
      removeSelected: null
    };
  },

  componentWillMount: function() {
    // Subscribe to the store.
    this.unsubscribe = SearchStore.listen(this._onChange);
    ResultSelectActions.clear();
  },
  componentWillUnmount: function() {
    console.log("Study Component will umount. ");
    this.unsubscribe();
  },

  /**
   * 2015-09-11:
   * This method returns a React Component that only has the text and couple of
   * events (such as click). Today, react-bootstrap-table does not support selectRows
   * without appear radio ou checkbox.
   *
   */

  formatGlobal: function(text, item) {
    let self = this;
    return (
      <div
        onClick={self.onStudyClick.bind(this, item)}
        className=""
        style={{ cursor: "pointer" }}
      >
        &nbsp; {text}
      </div>
    );
  },
  formatStudyDate: function(cell, item) {
    return this.formatGlobal(item.studyDate, item);
  },
  formatStudyDescription: function(cell, item) {
    return this.formatGlobal(item.studyDescription, item);
  },
  formatInstitutionName: function(cell, item) {
    return this.formatGlobal(item.institutionName, item);
  },
  formatModalities: function(cell, item) {
    return this.formatGlobal(item.modalities, item);
  },

  formatOptions: function(cell, item) {
    let self = this;
    let isAdmin = UserStore.isAdmin();
    let unindex = null;
    let removeFiles = null;
    if (this.props.enableAdvancedSearch) {
      if (isAdmin) {
        unindex = (
          <button
            title="Unindex (does not remove file physically)"
            onClick={self.showUnindex.bind(null, item)}
            className="btn btn_dicoogle btn-xs fa fa-eraser"
          />
        );

        removeFiles = (
          <button
            title="Removes the file physically"
            onClick={self.showRemove.bind(null, item)}
            className="btn btn_dicoogle btn-xs fa fa-trash-o"
          />
        );
      }
      return (
        <div>
          {unindex}
          {removeFiles}
          {/* plugin-based result options */}
          <PluginView
            style={{ display: "inline-block" }}
            slotId="result-options"
            data={{
              type: "study",
              uid: item.studyInstanceUID,
              // deprecated data fields
              "data-result-type": "study",
              "data-result-uid": item.studyInstanceUID,
              "data-result-patientid": item.patientid
            }}
          />
        </div>
      );
    }
    return <div />;
  },
  handleSelect(item) {
    let { studyInstanceUID } = item;
    let value = this.refsClone[studyInstanceUID].checked;
    if (value) ResultSelectActions.select(item, studyInstanceUID);
    else ResultSelectActions.unSelect(item, studyInstanceUID);
  },
  handleRefs: function(id, input) {
    this.refsClone[id] = input;
  },
  formatSelect: function(cell, item) {
    let { studyInstanceUID } = item;
    let classNameForIt = "advancedOptions " + studyInstanceUID;
    return (
      <div className={classNameForIt}>
        <Checkbox
          label=""
          onChange={this.handleSelect.bind(this, item)}
          inputRef={this.handleRefs.bind(this, studyInstanceUID)}
        />
      </div>
    );
  },

  sizePerPageListChange(sizePerPage) {},

  onPageChange(page, sizePerPage) {},

  render: function() {
    var self = this;
    var resultArray = this.props.patient.studies;

    for (let i = 0; i < resultArray.length; i++)
      resultArray[i]['patientid'] = this.props.patient.id;

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
    // TODO trigger this action elsewhere
    ResultSelectActions.level("study");
    return (
      <div>
        <BootstrapTable
          data={resultArray}
          selectRow={selectRowProp}
          condensed
          pagination
          striped
          hover
          width="100%"
        >
          <TableHeaderColumn
            dataAlign="right"
            dataField="studyDate"
            isKey
            dataFormat={this.formatStudyDate}
            dataSort
            width="16%"
          >
            Date
          </TableHeaderColumn>
          <TableHeaderColumn
            dataAlign="left"
            dataField="studyDescription"
            dataFormat={this.formatStudyDescription}
            dataSort
          >
            Description
          </TableHeaderColumn>
          <TableHeaderColumn
            dataAlign="left"
            dataField="institutionName"
            dataFormat={this.formatInstitutionName}
            dataSort
          >
            Institution
          </TableHeaderColumn>
          <TableHeaderColumn
            dataAlign="center"
            dataField="modalities"
            dataFormat={this.formatModalities}
            dataSort
            width="15%"
          >
            Modality
          </TableHeaderColumn>
          <TableHeaderColumn
            hidden={!this.props.enableAdvancedSearch}
            dataAlign="center"
            dataField="Opts"
            isKey={false}
            dataSort={false}
            dataFormat={this.formatOptions}
            width="128"
          >
            Options
          </TableHeaderColumn>
          <TableHeaderColumn
            hidden={!this.props.enableAdvancedSearch}
            dataAlign="center"
            dataField="Select"
            dataSort
            dataFormat={this.formatSelect}
            width="48"
          >
            #S
          </TableHeaderColumn>
        </BootstrapTable>
        <ConfirmModal
          show={self.state.unindexSelected !== null}
          onHide={self.hideUnindex}
          onConfirm={self.onUnindexConfirm.bind(
            self,
            self.state.unindexSelected
          )}
        />
        <ConfirmModal
          show={self.state.removeSelected !== null}
          message="The following files will be unindexed and then deleted from their storage."
          onHide={self.hideRemove}
          onConfirm={self.onRemoveConfirm.bind(self, self.state.removeSelected)}
        />
      </div>
    );
  },
  hideUnindex() {
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex(item) {
    this.setState({
      unindexSelected: item,
      removeSelected: null
    });
  },
  hideRemove() {
    this.setState({
      removeSelected: null
    });
  },
  showRemove(item) {
    this.setState({
      removeSelected: item,
      unindexSelected: null
    });
  },
  extractURISFromData: function(item) {
    var uris = [];
    for (let ss in item.series)
      for (let i in item.series[ss].images)
        uris.push(item.series[ss].images[i].uri);
    return uris;
  },
  onUnindexConfirm: function(item) {
    console.log(item);
    var uris = this.extractURISFromData(item);
    let p = this.props.provider;

    ActionCreators.unindex(uris, p);
  },
  onRemoveConfirm: function(item) {
    let uris = this.extractURISFromData(item);
    ActionCreators.remove(uris);
  },
  onStudyClick: function(item) {
    this.props.onItemClick(item);
  },
  onRowSelect: function(row) {
    this.props.onItemClick(row);
  },
  _onChange: function(data) {
    this.setState({
      data: data.data,
      status: "stopped",
      success: data.success,
      enableAdvancedSearch: data.data.advancedOptions
    });
  }
});

export { StudyView };
