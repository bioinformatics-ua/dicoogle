var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;
var ModalTrigger = ReactBootstrap.ModalTrigger;
var Modal = ReactBootstrap.Modal;


var ExportView = React.createClass({
	getInitialState: function() {
    	return {data: [],
    	status: "loading",
    	current: 0};
  	},
    componentDidMount: function() {

      $('#my-select').multiSelect();
    },


	render:function(){
	  //var url = Endpoints.base + "/dic2png?SOPInstanceUID="+this.props.uid;
		return (
			<Modal  {...this.props} bsStyle='primary' title='Export to CSV' animation={true}>
		        <div className='modal-body'>
              <select className="testdapissa" multiple="multiple" id="my-select" name="my-select[]">
                  <option value='elem_1'>elem 1</option>
                  <option value='elem_2'>elem 2</option>
                  <option value='elem_3'>elem 3</option>
                  <option value='elem_4'>elem 4</option>
                  <option value='elem_100'>elem 100</option>
              </select>
		        </div>
		        <div className='modal-footer'>
		          <Button onClick={this.onExportClicked}>Export</Button>
		        </div>
			</Modal>

			);
	},

  onExportClicked : function(){
    console.log("onExportCLicked");
  }
});

export{ExportView}
