import React from "react";
import { Button, Modal } from "react-bootstrap";
import { SearchStore } from "../../../stores/searchStore";
import { ActionCreators } from "../../../actions/searchActions";
import ConfirmModal from "./confirmModal";
import { Endpoints } from "../../../constants/endpoints";
import { DumpStore } from "../../../stores/dumpStore";
import ImageLoader from "react-imageloader";
import PluginView from "../../plugin/pluginView";
import { DumpActions } from "../../../actions/dumpActions";
import { BootstrapTable, TableHeaderColumn } from "react-bootstrap-table";
import { Input } from "react-bootstrap";
import ResultSelectActions from "../../../actions/resultSelectAction";
import UserStore from "../../../stores/userStore";

const ImageView = React.createClass({
  getInitialState: function() {
    // We need this because refs are not updated in BootstrapTable.
    this.refsClone = {};
    return {
      data: [],
      image: null,
      dump: null,
      status: "loading",
      unindexSelected: null,
      removeSelected: null
    };
  },

  componentWillMount: function() {
    this.unsubscribe = SearchStore.listen(this._onChange);
    ResultSelectActions.clear();
  },
  componentWillUnmount() {
    this.unsubscribe();
  },

  /**
   * 2015-09-11:
   * This method returns a React Component that only has the text and couple of
   * events (such as click). Today, react-bootstrap-table does not support selectRows
   * without appear radio ou checkbox.
   */

  formatGlobal: function(text, item) {
    return (
      <div className="" style={{ cursor: "pointer" }}>
        &nbsp; {text}
      </div>
    );
  },
  formatFileName: function(cell, item) {
    return this.formatGlobal(item.filename, item);
  },
  formatSOPInstanceUID: function(cell, item) {
    return this.formatGlobal(item.sopInstanceUID, item);
  },
  _preloader: function() {
    return <img src="spinner.gif" />;
  },
  formatThumbUrl: function(cell, item) {
    let self = this;
    let uid = item.sopInstanceUID;
    let thumbUrl =
      Endpoints.base + "/dic2png?thumbnail=true&SOPInstanceUID=" + uid;

    return (
      <div onClick={self.showImage.bind(self, uid)}>
        <ImageLoader
          src={thumbUrl}
          style={{ width: "64px", cursor: "pointer" }}
          wrapper={React.DOM.div}
        >
          <img src="assets/image-not-found.png" width="64px" />
        </ImageLoader>
      </div>
    );
  },
  formatViewOptions: function(cell, item) {
    let self = this;
    let uid = item.sopInstanceUID;
    return (
      <div>
        <button
          title="Dump Image"
          type="button"
          onClick={self.showDump.bind(self, uid)}
          className="btn btn_dicoogle btn-xs fa fa-table"
        >
          {" "}
        </button>
        <button
          title="Show Image"
          type="button"
          onClick={self.showImage.bind(self, uid)}
          className="btn btn_dicoogle btn-xs fa fa-eye"
        >
          {" "}
        </button>
      </div>
    );
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

          {/* plugin-based result options*/}
          <PluginView
            style={{ display: "inline-block" }}
            slotId="result-options"
            data={{
              type: "image",
              uri: item.uri,
              uid: item.sopInstanceUID,
              // deprecated data properties
              "data-result-type": "image",
              "data-result-uri": item.uri,
              "data-result-uid": item.sopInstanceUID
            }}
          />
        </div>
      );
    }
    return <div />;
  },

  handleSelect(item) {
    let { sopInstanceUID } = item;
    // ResultSelectActions.select(item);
    let value = this.refsClone[sopInstanceUID].getChecked();
    if (value) ResultSelectActions.select(item, sopInstanceUID);
    else ResultSelectActions.unSelect(item, sopInstanceUID);
  },
  handleRefs: function(id, input) {
    this.refsClone[id] = input;
  },
  formatSelect: function(cell, item) {
    let { sopInstanceUID } = item;
    let classNameForIt = "advancedOptions " + sopInstanceUID;
    return (
      <div className={classNameForIt}>
        <Input
          type="checkbox"
          label=""
          onChange={this.handleSelect.bind(this, item)}
          ref={this.handleRefs.bind(this, sopInstanceUID)}
        />
      </div>
    );
  },

  sizePerPageListChange(sizePerPage) {},

  onPageChange(page, sizePerPage) {},

  onRowSelect: function(row) {
    this.props.onItemClick(row);
  },

  render: function() {
    let self = this;
    var resultArray = this.props.serie.images;

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
    // TODO trigger this action elsewhere
    ResultSelectActions.level("image");
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
            dataAlign="left"
            dataField="filename"
            isKey
            dataFormat={this.formatFileName}
            dataSort
          >
            File Name
          </TableHeaderColumn>
          <TableHeaderColumn
            dataAlign="left"
            dataField="sopInstanceUID"
            dataFormat={this.formatSOPInstanceUID}
            dataSort
          >
            SOPInstanceUID
          </TableHeaderColumn>

          <TableHeaderColumn
            dataAlign="center"
            dataFormat={this.formatViewOptions}
            dataField="sopInstanceUID"
            dataSort
          >
            View
          </TableHeaderColumn>
          <TableHeaderColumn
            dataAlign="center"
            dataField="sopInstanceUID"
            dataFormat={this.formatThumbUrl}
            dataSort
          >
            Thumbnail
          </TableHeaderColumn>

          <TableHeaderColumn
            hidden={!this.props.enableAdvancedSearch}
            dataAlign="center"
            dataField="sopInstanceUID"
            dataSort={false}
            dataFormat={this.formatOptions}
          >
            Options
          </TableHeaderColumn>
          <TableHeaderColumn
            hidden={!this.props.enableAdvancedSearch}
            dataAlign="center"
            dataField="sopInstanceUID"
            dataSort
            dataFormat={this.formatSelect}
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
        <PopOverView uid={this.state.dump} onHide={this.onHideDump} />
        <PopOverImageViewer uid={this.state.image} onHide={this.onHideImage} />
      </div>
    );
  },
  onHideDump() {
    this.setState({ dump: null });
  },
  onHideImage() {
    this.setState({ image: null });
  },
  showDump(uid) {
    this.setState({ dump: uid, image: null, unindexSelected: null });
    DumpActions.get(uid);
  },
  showImage(uid) {
    this.setState({ dump: null, image: uid, unindexSelected: null });
  },
  hideUnindex() {
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex(item) {
    this.setState({
      unindexSelected: item,
      dump: null,
      image: null
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
      dump: null,
      image: null
    });
  },
  onUnindexConfirm(item) {
    console.log(item);
    var uris = [];
    uris.push(item.uri);
    let p = this.props.provider;
    ActionCreators.unindex(uris, p);
  },
  onRemoveConfirm: function(item) {
    var uris = [];
    uris.push(item.uri);
    ActionCreators.remove(uris);
  },
  _onChange: function(data) {
    this.setState({
      data: data.data,
      status: "stopped",
      success: data.success
    });
  }
});

var PopOverView = React.createClass({
  getInitialState: function() {
    return {
      data: null,
      status: "loading",
      current: 0
    };
  },
  componentWillMount: function() {
    // Subscribe to the store.
    this.unsubscribe = DumpStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },

  _onChange: function(data) {
    this.setState({ data, status: "stopped" });
  },

  onHide() {
    this.setState({ data: null });
    this.props.onHide();
  },

  render: function() {
    if (this.state.data === null) {
      return (
        <Modal
          {...this.props}
          show={this.props.uid !== null}
          bsStyle="primary"
          title="Image Dump"
          animation
        >
          <div className="loader-inner ball-pulse">
            <div />
            <div />
            <div />
          </div>
        </Modal>
      );
    }

    var obj = this.state.data.data.results.fields;
    var rows = [];

    var fields = [];
    Object.keys(obj).forEach(function(key, i) {
      rows.push(
        <p key={i}>
          <b>{key}:</b> {obj[key]}
        </p>
      );
      fields.push({ att: key, field: obj[key] });
    });

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
    return (
      <Modal
        onHide={this.props.onHide}
        show={this.props.uid !== null}
        bsClass="modal"
        bsStyle="primary"
        dialogClassName="table-dump"
        animation
      >
        <Modal.Header>
          <Modal.Title>Dump DICOM metadata</Modal.Title>
        </Modal.Header>
        <div className="modal-body">
          <BootstrapTable
            search
            columnFilter
            data={fields}
            selectRow={selectRowProp}
            condensed
            pagination
            striped
            hover
            className="table-test table table-striped table-bordered responsive"
            cellspacing="0"
            width="100%"
          >
            <TableHeaderColumn
              dataAlign="right"
              dataField="att"
              width="20%"
              isKey
              dataSort
            >
              Attribute
            </TableHeaderColumn>
            <TableHeaderColumn
              dataAlign="left"
              dataField="field"
              width="40%"
              isKey={false}
              dataSort
            >
              Field
            </TableHeaderColumn>
          </BootstrapTable>
        </div>
        <div className="modal-footer">
          <Button bsClass="btn btn_dicoogle" onClick={this.props.onHide}>
            Close
          </Button>
        </div>
      </Modal>
    );
  }
});

var PopOverImageViewer = React.createClass({
  render() {
    let url =
      this.props.uid !== null &&
      Endpoints.base + "/dic2png?SOPInstanceUID=" + this.props.uid;
    return (
      <Modal
        onHide={this.props.onHide}
        show={this.props.uid !== null}
        bsStyle="primary"
        animation
      >
        <Modal.Header>
          <Modal.Title>View Image</Modal.Title>
        </Modal.Header>
        <div className="modal-body">
          <ImageLoader
            src={url}
            style={{ width: "100%" }}
            wrapper={React.DOM.div}
          >
            <img src="assets/image-not-found.png" width="100%" />
          </ImageLoader>
        </div>
        <div className="modal-footer">
          <Button bsClass="btn btn_dicoogle" onClick={this.props.onHide}>
            Close
          </Button>
        </div>
      </Modal>
    );
  }
});

export { ImageView };
