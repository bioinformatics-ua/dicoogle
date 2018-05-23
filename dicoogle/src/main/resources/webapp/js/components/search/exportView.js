import React from "react";
import Select from "react-select";
import {
  Button,
  Modal,
  FormGroup,
  FormControl,
  ControlLabel,
  Form
} from "react-bootstrap";

import { ExportActions } from "../../actions/exportActions";
import { ExportStore } from "../../stores/exportStore";
import $ from "jquery";

const ExportView = React.createClass({
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

      current: 0
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

  _onChange: function(data) {
    if (!data.success) {
      this.setState({
        status: "failed"
      });
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

      let toastMessage = this.state.error ? this.state.error : "Saved.";
      $(".toast")
        .stop()
        .text(toastMessage)
        .fadeIn(400)
        .delay(3000)
        .fadeOut(400); // fade out after 3 seconds
    }
  },

  render: function() {
    let presetNames = this.state.presets.map(preset => ({
      value: preset.name,
      label: preset.name
    }));

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

            <div className="modal-body">
              <textarea
                id="textFields"
                placeholder="Paste export fields here (one per line)"
                onChange={this.handleFieldSelectTextArea}
                rows="10"
                value={this.state.selectedFieldsAdditionals}
                className="exportlist form-control"
              />
            </div>
          </FormGroup>
        </Modal.Body>
        <Modal.Footer id="hacked-modal-footer-do-not-remove">
          <Form inline>
            <FormControl
              type="text"
              value={this.state.exportPresetName}
              placeholder="Name the preset"
              onChange={this.handlePresetNameChange}
              maxLength="100"
            />

            <Button
              bsStyle="default"
              onClick={this.handleSavePresetClicked}
              disabled={!this.canExport()}
            >
              Save Preset
            </Button>
            <Button
              bsStyle="primary"
              onClick={this.handleExportClicked}
              disabled={!this.canSave()}
            >
              Export
            </Button>
          </Form>
          <div className="toast">Saved</div>
        </Modal.Footer>
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
