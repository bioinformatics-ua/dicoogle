import React from 'react';
import {SearchStore} from '../../../stores/searchStore';

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';

var StudyView = React.createClass({
  	getInitialState: function() {
    	return {
        data: [],
    	  status: "loading",
        unindexSelected: null,
        enableAdvancedSearch: this.props.enableAdvancedSearch
      };
  	},
    componentDidMount: function(){
       $('#study-table').dataTable({paging: true,searching: false,info:true});
     },
     componentDidUpdate: function(){
       $('#study-table').dataTable({paging: true,searching: false,info: true});
     },
   	componentWillMount: function() {
    	// Subscribe to the store.
    	SearchStore.listen(this._onChange);
  	},
	render: function() {
		var self = this;

		var resultArray = this.props.patient.studies;

		var resultItems = (
				resultArray.map(function(item){
				  let advOpt = (self.state.enableAdvancedSearch) && (<td> 
                <button onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
              </td>);
		      		return (
				    	     <tr className="resultRow" style={{"cursor" : "pointer"}}>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.studyDate}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.studyDescription}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.institutionName}</td>
				    	     	<td onclick="" onClick={self.onStudyClick.bind(this, item)}> {item.modalities}</td>
				    	     	{advOpt}
				    	     </tr>
			           	);
       			})
			);

		var header = (self.state.enableAdvancedSearch) ? (
				<tr>
					<th>Data</th>
        			<th>Description</th>
        			<th>Institution name</th>
        			<th>Modalities</th>
        			<th>Options</th>
        		</tr>
    		) : (
				<tr>
					<th>Data</th>
        			<th>Description</th>
        			<th>Institution name</th>
        			<th>Modalities</th>
        		</tr>
    		);

	return (
			<div>
				<table id="study-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
           				{header}
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

export {StudyView};
