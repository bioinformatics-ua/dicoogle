var React = require('react');

import {ActionCreators} from '../../../actions/searchActions';
import {SearchStore} from '../../../stores/searchStore';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';

var PatientView = React.createClass({
  getInitialState() {
    return {
      unindexSelected: null
    }
  },
	componentDidMount: function(){
		$('#patient-table').dataTable({paging: true, searching: false, info:true});
	},
  	componentWillMount: function() {
    	// Subscribe to the store.
    	SearchStore.listen(this._onChange);
  	},
	componentDidUpdate: function(){
		$('#patient-table').dataTable({paging: true, searching: false, info: true});
	},
	render: function() {

		const self = this;

		var resultArray = this.props.items.results;

		var resultItems = (
				resultArray.map(function(item, index){
					var advOpt = (self.props.enableAdvancedSearch) && (<td> 
                <button onClick={self.showUnindex.bind(null, index)} className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
              </td>);

		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.id}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.name}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.gender}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.nStudies}</td>
				    	     	{advOpt}
				    	     </tr>
			           	);
       			})
			);

		var header = (self.props.enableAdvancedSearch) ? (
						<tr>
						<th>Id</th>
	        			<th>Name</th>
	        			<th>Gender</th>
	        			<th>Studies</th>
	        			<th>Options</th>
	        			</tr>
	        		) : (
	        			<tr>
						<th>Id</th>
	        			<th>Name</th>
	        			<th>Gender</th>
	        			<th>Studies</th>
	        			</tr>
	        		);

    return (
			<div>
				<table id="patient-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
            {header}            			
          </thead>
          <tbody>
            {resultItems}
          </tbody>
    			</table>
          <ConfirmModal selected={this.state.unindexSelected !== null}
                        onHide={this.hideUnindex}
                        onConfirm={this.onUnindexConfirm.bind(this, this.state.unindexSelected)}/>
      </div>
		);
	},
  hideUnindex () {
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex (index) {
    this.setState({
      unindexSelected: index
    });
  },
	onUnindexConfirm: function(index){
		var uris = []; 
		for(let s in this.props.items.results[index].studies)
			for(let ss in this.props.items.results[index].studies[s].series)
				for(let i in this.props.items.results[index].studies[s].series[ss].images)
					uris.push(this.props.items.results[index].studies[s].series[ss].images[i].uri);
		
		let p = this.props.provider;

		ActionCreators.unindex(uris, p);
	},
	onPatientClick:function(id, index){
		console.log("Patient", id, "clicked");
		this.props.onItemClick(this.props.items.results[index]);
	},
  	_onChange : function(data){
	    console.log("onchange");
	    console.log(data.success);
	    console.log(data.status);
	    if (this.isMounted())
	    {
	      this.setState({data:data.data,
	      status:"stopped",
	      success: data.success});
	    }
  	}
});

export {PatientView};
