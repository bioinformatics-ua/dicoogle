import React from 'react';
import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';

var StudyView = React.createClass({
  	getInitialState: function() {
    	return {
        data: [],
    	  status: "loading",
        unindexSelected: null
      };
  	},
    componentDidMount: function(){
       var self = this;
       $('#study-table').dataTable({paging: true,searching: false,info:true});
     },
     componentDidUpdate: function(){
       $('#study-table').dataTable({paging: true,searching: false,info: true});
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
							      <button onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle fa fa-eraser"> Unindex</button>
				    	     	</td>
				    	     </tr>
			           	);
       			})
			);

	return (
			<div>
				<table id="study-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
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
          <ConfirmModal show={self.state.unindexSelected !== null}
                        onHide={self.hideUnindex}
                        onConfirm={self.onUnindexConfirm.bind(self, self.state.unindexSelected)} />
			</div>
		);
	},
  hideUnindex () {
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex (item) {
    this.setState({
      unindexSelected: item
    });
  },
	onUnindexConfirm: function(item){
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
