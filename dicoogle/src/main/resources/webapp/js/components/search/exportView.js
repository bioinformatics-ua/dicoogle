import React from 'react';
import {Button, Modal} from 'react-bootstrap';

import {ExportActions} from '../../actions/exportActions';
import {ExportStore} from '../../stores/exportStore';

const ExportView = React.createClass({
	getInitialState: function() {
      return {data: [],
      status: "loading",
      current: 0};
    },
    componentDidMount: function() {
    },
	componentDidUpdate: function() {
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
      <Modal {...this.props} bsStyle='primary' title='Export to CSV' animation>
        <div className='modal-body'>
            <textarea id="textFields" placeholder="Paste export fields here (one per line) ..." rows="10" className="exportlist form-control"></textarea>
        </div>
        <div id="hacked-modal-footer" className='modal-footer'>
          <Button onClick={this.onExportClicked}>Export</Button>
        </div>
      </Modal>);
	},

  onExportClicked: function(){
    //console.log("onExportCLicked", document.getElementById("textFields").value);
		var fields = document.getElementById("textFields").value.split("\n");
		console.log(fields);

		var query = this.props.query;
		ExportActions.exportCSV(query, fields);
  }
});

export {ExportView};
