var React = require('react');
var ReactBootstrap = require('react-bootstrap');

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
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}} onclick="" onClick={self.onSeriesClick.bind(this, item)}>
				    	     	<td> {item.serieNumber}</td>
				    	     	<td> {item.serieModality}</td>
				    	     	<td> {item.serieDescription}</td>
				    	     	<td> {item.images.length}</td>
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
            			</tr>
        			</thead>
        			 <tbody>
           				{resultItems}
            		</tbody>
    			</table>
			</div>
		);
	},

	onSeriesClick:function(item){
		this.props.onItemClick(item);
	}

});

export {SeriesView};
