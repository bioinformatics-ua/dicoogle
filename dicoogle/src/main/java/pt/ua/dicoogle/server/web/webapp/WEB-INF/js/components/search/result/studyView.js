var React = require('react');
var ReactBootstrap = require('react-bootstrap');

var StudyView = React.createClass({
  	getInitialState: function() {
    	return {data: [],
    	status: "loading"};
  	},
    componentDidMount: function(){
       var self = this;
       $('#example').dataTable({paging: false,searching: false,info:false});
     },
     componentDidUpdate: function(){
       $('#example').dataTable({paging: false,searching: false,info: false});
     },
	render: function() {
		var self = this;

		var resultArray = this.props.patient.studies;

		var resultItems = (
				resultArray.map(function(item){
		      		return (
				    	     <tr className="resultRow" onClick={self.onStudyClick.bind(this, item)}>
				    	     	<td> {item.studyDate}</td>
				    	     	<td> {item.studyDescription}</td>
				    	     	<td> {item.institutionName}</td>
				    	     	<td> {item.modalities}</td>
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
            			</tr>
        			</thead>
        			 <tbody>
           				{resultItems}
            		</tbody>
    			</table>
			</div>
		);
	},

	onStudyClick:function(item){
		this.props.onItemClick(item);
	}

});

export {StudyView};
