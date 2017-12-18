import React from 'react';
import Select from 'react-select';
import {Button, Modal, FormGroup, FormControl, ControlLabel, Form} from 'react-bootstrap';

import {ExportActions} from '../../actions/exportActions';
import {ExportStore} from '../../stores/exportStore';
import $ from 'jquery';

const ExportView = React.createClass({
	getInitialState: function() {
		return {
			fields: [],
			presets: [],
			status: "loading",

      selectedFields: [],
      selectedPresetName: "",
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

	_onChange: function(data){
    if (!data.success) {
      this.setState({
        status: "failed"
      });
      return;
    }

    let fields = data.data.fields;
    let presets = data.data.presets;

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
      $('.toast').stop().text(toastMessage).fadeIn(400).delay(3000).fadeOut(400); // fade out after 3 seconds
    }
	},

	render: function() {
    let fieldList = this.state.fields.map(field => ({value: field, label: field}));
    let presetList = this.state.presets.map(preset => ({value: preset.name, label: preset.name}));

    return (
			<Modal {...this.props} bsStyle='primary' animation>
				<Modal.Header>
					<Modal.Title>Export to CSV</Modal.Title>
				</Modal.Header>
				<Modal.Body>
					<FormGroup>
            <ControlLabel>Preset (optional):</ControlLabel>
            <Select simpleValue
                    id="selected-preset"
                    value={this.state.selectedPresetName}
                    options={presetList}
                    placeholder="Choose a preset"
                    onChange={this.handlePresetSelect}
            />
          </FormGroup>
          <FormGroup>
            <ControlLabel>Fields to export:</ControlLabel>
            <Select multi
                    id="selected-fields"
                    value={this.state.selectedFields}
                    options={fieldList}
                    placeholder="Choose fields"
                    onChange={this.handleFieldSelect}
            />
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
            <Button bsStyle="default" onClick={this.handleSavePresetClicked} disabled={!this.canExport()}>Save Preset</Button>
            <Button bsStyle="primary" onClick={this.handleExportClicked} disabled={!this.canSave()}>Export</Button>
          </Form>
          <div className="toast">Saved</div>
        </Modal.Footer>
			</Modal>)
	},

  handleFieldSelect: function(fields) {
    this.setState({
      selectedFields: fields.map((e) => e.value)
    });
  },

  handlePresetSelect: function(name) {
    let fields = this.state.presets.filter(preset => preset.name === name)[0].fields;

    this.setState({
      exportPresetName: name,
      selectedPresetName: name,
      selectedFields: fields
    });
  },

  handlePresetNameChange: function(e) {
    this.setState({
      exportPresetName: e.target.value
    });
  },

  handleSavePresetClicked: function() {
    ExportActions.savePresets(this.state.exportPresetName, this.state.selectedFields);

    this.setState({
      actionPerformed: true
    });
  },

  handleExportClicked: function() {
    let fields = this.state.selectedFields;
    let query = this.props.query;
    ExportActions.exportCSV(query, fields);
  },

  canSave: function () {
    return this.state.selectedFields.length !== 0;
  },

  canExport: function () {
    return this.state.exportPresetName && this.canSave();
  }
});

export {ExportView};
