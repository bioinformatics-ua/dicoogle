var React = require('react');
var ReactBootstrap = require('react-bootstrap');

var ModalTrigger = ReactBootstrap.ModalTrigger;

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';


var SeriesView = React.createClass({
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

		var resultArray = this.props.study.series;

		var resultItems = (
				resultArray.map(function(item){
		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieNumber}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieModality}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieDescription}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.images.length}</td>	
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
                			<th>Number</th>
                			<th>Modality</th>
                			<th>Description</th>
                			<th>#Images</th>
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
		for(let i in item.images)
			uris.push(item.images[i].uri);
		
		let p = this.props.provider;

		ActionCreators.unindex(uris, p);
	},
	onSeriesClick:function(item){
		this.props.onItemClick(item);
	}

});

export {SeriesView};
