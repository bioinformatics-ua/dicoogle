import React from "react";
import createReactClass from "create-react-class";
import Select from "react-select";
import {
  Modal,
  FormGroup,
  FormControl,
  ControlLabel,
  Form
} from "react-bootstrap";

import { ExportActions } from "../../actions/exportActions";
import { ExportStore } from "../../stores/exportStore";
import { ToastView } from "../mixins/toastView";

const ExportView = createReactClass({
  getInitialState: function() {
    return {
      fields: [],
      presets: [],
      status: "loading",

      selectedFields: [],
      selectedPresetName: null,
      selectedFieldsAdditionals: "",
      exportPresetName: "default",

      actionPerformed: false,

      current: 0,

      showToast: false,
      toastType: "default",
      toastMessage: {}
    };
  },

  componentWillMount: function() {
    // Subscribe to the store.
    console.log("subscribe listener");
    this.unsubscribe = ExportStore.listen(this._onChange);
  },

  componentDidMount: function() {
    ExportActions.getFieldList();
    ExportActions.getPresets();
  },

  componentWillUnmount() {
    this.unsubscribe();
  },

  showToastMessage: function(toastType, toastMessage) {
    this.setState(
      {
        showToast: true,
        toastType,
        toastMessage
      },
      () => setTimeout(() => this.setState({ showToast: false }), 3000)
    );
  },

  _onChange: function(data) {
    if (!data.success) {
      this.setState({
        status: "failed"
      });
      this.showToastMessage("error", { title: "Error" });
      return;
    }

    let fields = data.data.fields;
    if (fields) fields = fields.map(field => ({ value: field, label: field }));

    let presets = data.data.presets;
    if (presets)
      presets = presets.sort((p1, p2) => p1.name.localeCompare(p2.name));

    let status = this.state.status;
    if (fields && presets) status = "done";

    this.setState({
      fields: fields,
      presets: presets,
      status: status
    });

    if (this.state.actionPerformed) {
      this.setState({
        actionPerformed: false
      });

      this.showToastMessage("success", { title: "Success" });
    }
  },

  render: function() {
    let presetNames = this.state.presets.map(preset => ({
      value: preset.name,
      label: preset.name
    }));

    const { showToast, toastType, toastMessage } = this.state;

    return (
      <Modal {...this.props} bsStyle="primary" animation>
        <Modal.Header>
          <Modal.Title>Export to CSV</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <FormGroup>
            <ControlLabel>Preset (optional):</ControlLabel>
            <Select
              simpleValue
              id="selected-preset"
              value={this.state.selectedPresetName}
              options={presetNames}
              placeholder="Choose a preset"
              onChange={this.handlePresetSelect}
            />
          </FormGroup>
          <FormGroup>
            <ControlLabel>Fields to export:</ControlLabel>
            <Select.AsyncCreatable
              multi
              id="selected-fields"
              value={this.state.selectedFields}
              loadOptions={this.loadOptionFields}
              placeholder="Choose fields"
              onChange={this.handleFieldSelect}
            />
          </FormGroup>
          <FormGroup>
            <ControlLabel>Inline fields to export (optional):</ControlLabel>

            <textarea
              id="textFields"
              placeholder="Paste export fields here (one per line)"
              onChange={this.handleFieldSelectTextArea}
              rows="10"
              value={this.state.selectedFieldsAdditionals}
              className="exportlist form-control"
            />
          </FormGroup>
        </Modal.Body>
        <Modal.Footer id="hacked-modal-footer-do-not-remove" className="modal-dicoogle">
          <Form inline>
            <FormControl
              type="text"
              value={this.state.exportPresetName}
              placeholder="Name the preset"
              onChange={this.handlePresetNameChange}
              maxLength="100"
            />

            <button className="btn btn_dicoogle btn-export" id="export-btn" onClick={this.handleExportClicked} disabled={!this.canSave()}>
              Export
            </button>

            <button className="btn btn_dicoogle btn-export" id="save-preset-btn" onClick={this.handleSavePresetClicked} disabled={!this.canExport()}>
              Save Preset
            </button>
          </Form>
        </Modal.Footer>
        <ToastView
          show={showToast}
          message={toastMessage}
          toastType={toastType}
        />
      </Modal>
    );
  },

  loadOptionFields: function(input, callback) {
    // display no options if the input is empty
    let options =
      input.length === 0
        ? []
        : this.state.fields.filter(
            i =>
              i.value.toLowerCase().substr(0, input.length) ===
              input.toLowerCase()
          );

    let data = {
      options: options,
      complete: true
    };

    callback(null, data);
  },

  handleFieldSelectTextArea: function(event) {
    this.setState({
      selectedFieldsAdditionals: event.target.value
    });
  },

  handleFieldSelect: function(selectedFields) {
    this.setState({
      selectedFields: selectedFields
    });
  },

  handlePresetSelect: function(name) {
    // default values if no preset is selected
    let selectedFields = [];
    let exportPresetName = "default";

    if (name) {
      exportPresetName = name;
      let fields = this.state.presets.filter(preset => preset.name === name)[0]
        .fields;
      selectedFields = fields.map(field => ({ value: field, label: field }));
    }

    this.setState({
      exportPresetName: exportPresetName,
      selectedPresetName: name,
      selectedFields: selectedFields,
      selectedFieldsAdditionals: ""
    });
  },

  handlePresetNameChange: function(e) {
    this.setState({
      exportPresetName: e.target.value
    });
  },
  __getSelectedFields: function() {
    let fields = this.state.selectedFields.map(i => i.value);
    if (this.state.selectedFieldsAdditionals !== "") {
      let fieldsInline = this.state.selectedFieldsAdditionals.split("\n");
      fields = fields.concat(fieldsInline);
    }

    return fields;
  },
  handleSavePresetClicked: function() {
    let fields = this.__getSelectedFields();
    ExportActions.savePresets(this.state.exportPresetName, fields);

    this.setState({
      selectedPresetName: this.state.exportPresetName,
      actionPerformed: true
    });
  },

  handleExportClicked: function() {
    let fields = this.__getSelectedFields();
    let query = this.props.query;
    ExportActions.exportCSV(query, fields);
  },

  canSave: function() {
    return this.__getSelectedFields().length !== 0;
  },

  canExport: function() {
    return this.state.exportPresetName && this.canSave();
  }
});

export { ExportView };
