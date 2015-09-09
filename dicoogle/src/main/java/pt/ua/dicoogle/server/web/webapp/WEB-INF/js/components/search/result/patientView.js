var React = require('react');

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';

var PatientView = React.createClass({
  getInitialState() {
    return {
      unindexSelected: null
    }
  },
	componentDidMount: function(){
		$('#patient-table').dataTable({paging: true, searching: false, info:true});
	},
	componentDidUpdate: function(){
		$('#patient-table').dataTable({paging: true, searching: false, info: true});
	},
	render: function() {

		var self = this;

		var resultArray = this.props.items.results;

		var resultItems = (
				resultArray.map(function(item, index){
		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.id}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.name}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.gender}</td>
				    	     	<td onclick="" onClick={self.onPatientClick.bind(null, item.id, index)}> {item.nStudies}</td>				    	     	   
				    	     	<td> 
							      <button className="btn btn_dicoogle fa fa-eraser" onClick={self.showUnindex.bind(null, index)}> Unindex</button>
				    	     	</td>
				    	     </tr>
			           	);
       			})
			);

	return (
			<div>
				<table id="patient-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
              <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Gender</th>
                <th>Studies</th>
                <th>Options</th>
              </tr>
            </thead>
            <tbody>
              {resultItems}
            </tbody>
    			</table>
          <ConfirmModal selected={self.state.unindexSelected}
                        onHide={self.hideUnindex}
                        onConfirm={self.onUnindexClick.bind(self)}/>
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
	onUnindexClick: function(index){
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
	}

});

export {PatientView};
