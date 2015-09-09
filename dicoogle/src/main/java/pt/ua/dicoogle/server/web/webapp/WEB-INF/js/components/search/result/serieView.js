var React = require('react');
var ReactBootstrap = require('react-bootstrap');

var ModalTrigger = ReactBootstrap.ModalTrigger;

import {SearchStore} from '../../../stores/searchStore';

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';


var SeriesView = React.createClass({
  	getInitialState: function() {
    	return {data: [],
    	status: "loading", enableAdvancedSearch: this.props.enableAdvancedSearch};
  	},
    componentDidMount: function(){
   		var self = this;
   		$('#example').dataTable({paging: true,searching: false,info:true});
   	},
   	componentDidUpdate: function(){

   	},
	componentWillMount: function() {
    	// Subscribe to the store.
    	SearchStore.listen(this._onChange);
  	},
	render: function() {
		var self = this;

		var resultArray = this.props.study.series;

		var resultItems = (
				resultArray.map(function(item){
					var advOpt = (self.state.enableAdvancedSearch) ? (<td> 
				    	     	<ModalTrigger modal={<ConfirmModal onConfirm={self.onUnindexClick.bind(null, item)}/>}>
							      <button className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
							    </ModalTrigger>
				    	     	</td>) : undefined;
		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieNumber}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieModality}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.serieDescription}</td>
				    	     	<td  onclick="" onClick={self.onSeriesClick.bind(this, item)}> {item.images.length}</td>	
				    	     	{advOpt}
				    	     </tr>
			           	);
       			})
			);


		var header = (self.state.enableAdvancedSearch) ? (
				<tr>
        			<th>Number</th>
        			<th>Modality</th>
        			<th>Description</th>
        			<th>#Images</th>         			
        			<th>Options</th>    			
        		</tr>
    		) : (
				<tr>
        			<th>Number</th>
        			<th>Modality</th>
        			<th>Description</th>
        			<th>#Images</th>
        		</tr>
    		);

	return (
			<div>
				<table id="example" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
           				{header}
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
	},
  	_onChange : function(data){
	    console.log("onchange");
	    console.log(data.success);
	    console.log(data.status);
	    if (this.isMounted())
	    {
	      this.setState({data: data.data,
	      status:"stopped",
	      success: data.success, 
	      enableAdvancedSearch: data.data.advancedOptions});
	    }
  	}

});

export {SeriesView};
