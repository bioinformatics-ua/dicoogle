import React from 'react';
import {Button, Modal, FormGroup, FormControl, ControlLabel} from 'react-bootstrap';

import * as ExportActions from '../../actions/exportActions';
import {ExportStore} from '../../stores/exportStore';

const ExportView = React.createClass({
	getInitialState: function() {
      return {data: [],
      status: "loading",
			fieldText: '',
      current: 0};
	},
	componentWillMount: function() {
		// Subscribe to the store.
		console.log("subscribe listener");
		this.unsubscribe = ExportStore.listen(this._onChange);
	},
	componentWillUnmount() {
		this.unsubscribe();
	},
	_onChange: function(data){
		this.setState({data: data.data, status: "done"});
	},

	render: function(){
    return (
      <Modal {...this.props} bsStyle='primary' animation>
				<Modal.Header>
					<Modal.Title>Export to CSV</Modal.Title>
				</Modal.Header>
        <Modal.Body>
          <FormGroup>
            <ControlLabel>Export Field List</ControlLabel>
            <FormControl componentClass="textarea" placeholder="Paste export fields here (one per line) ..."
                         rows="10" bsClass="exportlist"
                         onChange={this.handleTextChanged} value={this.state.fieldText} />
					</FormGroup>
        </Modal.Body>
        <Modal.Footer id="hacked-modal-footer-do-not-remove">
          <Button bsStyle="primary" onClick={this.handleExportClicked}>Export</Button>
        </Modal.Footer>
      </Modal>);
	},

	handleTextChanged(e) {
		this.setState({fieldText: e.target.value});
	},

  handleExportClicked: function(){
		const fields = this.state.fieldText.split("\n");
		console.log(fields);

		const query = this.props.query;
		ExportActions.exportCSV(query, fields);
  }
});

export {ExportView};
