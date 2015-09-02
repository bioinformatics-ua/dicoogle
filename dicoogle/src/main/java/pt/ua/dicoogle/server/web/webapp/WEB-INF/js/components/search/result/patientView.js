var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var ModalTrigger = ReactBootstrap.ModalTrigger;

import {ActionCreators} from '../../../actions/searchActions';

import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';

var PatientView = React.createClass({
	componentDidMount: function(){
		var self = this;
		$('#example').dataTable({paging: true,searching: false,info:true});
	},
	componentDidUpdate: function(){
		$('#example').dataTable({paging: true,searching: false,info: true});
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
				    	     	<ModalTrigger modal={<ConfirmModal onConfirm={self.onUnindexClick.bind(null, index)}/>}>
							      <button className="btn btn_dicoogle fa fa-eraser"> Unindex</button>
							    </ModalTrigger>
				    	     	</td>
				    	     </tr>
			           	);
       			})
			);

	return (
			<div>
				<table id="example" className="table table-striped table-bordered" cellspacing="0" width="100%">
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
			</div>
		);
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
		console.log(id," clicked");
		//this.props.onItemClick(this.props.items.results[index]);
	}

});

export {PatientView};
