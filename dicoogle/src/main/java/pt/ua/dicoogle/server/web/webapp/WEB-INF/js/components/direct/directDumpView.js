var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;
var Modal = ReactBootstrap.Modal;
import {DumpStore} from '../../stores/dumpStore';
import {DumpActions} from '../../actions/dumpActions';

import {getUrlVars} from '../../utils/url';
import {Endpoints} from '../../constants/endpoints';


var DirectDumpView = React.createClass({
	getInitialState: function() {
    	return {data: [],
    	status: "loading",
    	current: 0};
  	},
  	componentDidMount: function() {
			var uid = getUrlVars()['SOPInstanceUID'];
  		DumpActions.get(uid);
  	},
  	componentWillMount: function() {
    	// Subscribe to the store.
    	DumpStore.listen(this._onChange);
  	},
    componentDidUpdate: function(){
      $('#dumptable').dataTable({paging: false, searching: false, info: false, responsive: false});
    },

  	_onChange: function(data){
  		if (this.isMounted())
	    {
	    	this.setState({data:data,
	      status:"stopped"}
	      );
	    }
  	},

	render:function(){
		if(this.state.status == "loading")
		  return (<div className="loader-inner ball-pulse"/>);

				var obj = this.state.data.data.results.fields;
				var rows = [];

        var fields = [];
				Object.keys(obj).forEach(function(key, i) {
        			rows.push(<p key={i}><b>{key}:</b> {obj[key]}</p>);
              fields.push({att: key, field: obj[key]});
   				 });

			var fieldstable = fields.map(function(item){
			  return (
			    <tr>
			      <td> <p>{item.att}</p></td>
			      <td> <p>{item.field}</p></td>
			      </tr>
			  );
			});



		return (
              <table id="dumptable" className="table-test table table-striped table-bordered responsive" cellspacing="0" width="100%">
                <thead>
                         <tr>
                            <th>Attribute</th>
                            <th>Field</th>
                          </tr>
                    </thead>
                     <tbody>
                         {fieldstable}
                      </tbody>
                </table>


			);
	}
});

export {DirectDumpView};
