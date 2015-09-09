var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var ModalTrigger = ReactBootstrap.ModalTrigger;

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';

var StudyView = React.createClass({
  	getInitialState: function() {
    	return {data: [],
    	status: "loading"};
  	},
    componentDidMount: function(){
       var self = this;
       $('#example').dataTable({paging: true,searching: false,info:true});
     },
     componentDidUpdate: function(){
       $('#example').dataTable({paging: true,searching: false,info: true});
     },
	render: function() {
		var self = this;

		var resultArray = this.props.patient.studies;

		var resultItems = (
				resultArray.map(function(item){
		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.studyDate}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.studyDescription}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.institutionName}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.modalities}</td>
				    	     	<td> 
				    	     	<ModalTrigger modal={<ConfirmModal onConfirm={self.onUnindexClick.bind(null, item)}/>}>
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
                			<th>Data</th>
                			<th>Description</th>
                			<th>Institution name</th>
                			<th>Modalities</th>                			
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
	onUnindexClick: function(item){
		console.log(item)
		var uris = []; 
		for(let ss in item.series)
			for(let i in item.series[ss].images)
				uris.push(item.series[ss].images[i].uri);
		
		let p = this.props.provider;

		ActionCreators.unindex(uris, p);
	},
	onStudyClick:function(item){
		this.props.onItemClick(item);
	}
});

export {StudyView};
