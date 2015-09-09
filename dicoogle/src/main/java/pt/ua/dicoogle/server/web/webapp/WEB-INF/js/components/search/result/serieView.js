import React from 'react';

import {SearchStore} from '../../../stores/searchStore';
import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';

var SeriesView = React.createClass({
  	getInitialState: function() {
    	return {
        data: [],
    	  status: "loading",
        unindexSelected: null,
        enableAdvancedSearch: this.props.enableAdvancedSearch
      };
  	},
    componentDidMount: function(){
   		var self = this;
   		$('#series-table').dataTable({paging: true,searching: false,info:true});
   	},
   	componentDidUpdate: function(){
       $('#series-table').dataTable({paging: true,searching: false,info: true});
   	},
	componentWillMount: function() {
    	// Subscribe to the store.
    	SearchStore.listen(this._onChange);
  	},
	render: function() {
		const self = this;

		var resultArray = this.props.study.series;

		var resultItems = (
				resultArray.map(function(item){
					let advOpt = (self.state.enableAdvancedSearch) && (<td> 
                <button onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
              </td>);
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
				<table id="series-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
            {header}
          </thead>
          <tbody>
            {resultItems}
          </tbody>
        </table>
        <ConfirmModal show={self.state.unindexSelected !== null}
                      onHide={self.hideUnindex}
                      onConfirm={self.onUnindexClick.bind(self, self.state.unindexSelected)}/>
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
