import React from 'react';
import {Button, Modal} from 'react-bootstrap';

import {ExportActions} from '../../actions/exportActions';
import {ExportStore} from '../../stores/exportStore';

var ExportView = React.createClass({
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
		ExportStore.listen(this._onChange);
	},
	_onChange: function(data){
		if (this.isMounted()){

			this.setState({data:data.data,status: "done"});
		}
	},

	render:function(){
	  //var url = Endpoints.base + "/dic2png?SOPInstanceUID="+this.props.uid;
		//if(this.state.status == "loading"){
			return (
				<Modal  {...this.props} bsStyle='primary' title='Export to CSV' animation={true}>

					<div className='modal-body'>

										<textarea id="textFields" placeholder="Paste export fields here (one per line) ..." rows="10" className="exportlist form-control"></textarea>
										</div>
							<div className='modal-footer'>
								<Button onClick={this.onExportClicked}>Export</Button>
							</div>
				</Modal>

				);
			/*
			DEAD CODE
*/
		var options = this.state.data.map(
			function(item){
					return(
						<option key={item}>{item}</option>
					);
			}
		);

		return (
			<Modal  {...this.props} bsStyle='primary' title='Export to CSV' animation={true}>
		        <div id="bilo" className='modal-body clusterize-scroll '>
              <select className="testdapissa clusterize-content" multiple="multiple" id="my-select" name="my-select[]">

              </select>
		        </div>
		        <div className='modal-footer'>
		          <Button onClick={this.onExportClicked}>Export</Button>
		        </div>
			</Modal>

			);
	},

  onExportClicked : function(){
    //console.log("onExportCLicked", document.getElementById("textFields").value);
		var fields = document.getElementById("textFields").value.split("\n");
		console.log(fields);

		var query = this.props.query;
		ExportActions.exportCSV(query, fields);
  },
	clusterize: function(){
		var data = [];
		this.state.data.map(
			function(item){

						data.push('<option>'+item+'</option>');

			}
		);
		// JavaScript
	//var data = ['<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>', '<option>balo</option>'];
	var clusterize = new Clusterize({
  	rows: data,
  	scrollId: 'bilo',
  	contentId: 'my-select'
	});
	}
});

export {ExportView};
