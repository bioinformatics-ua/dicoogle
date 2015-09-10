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
        removeSelected: null
      };
  	},
    componentDidMount: function(){
   		$('#series-table').dataTable({paging: true,searching: false,info:true});
   	},
   	componentDidUpdate: function(){
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
					let advOpt = (self.props.enableAdvancedSearch) && (<td> 
                <button onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
                <button onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o"> Remove</button>
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
		var header = (self.props.enableAdvancedSearch) ? (
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
                      onConfirm={self.onUnindexConfirm.bind(self, self.state.unindexSelected)}/>
        <ConfirmModal show={self.state.removeSelected !== null}
                      message="The following files will be unindexed and then deleted from their storage."
                      onHide={self.hideRemove}
                      onConfirm={self.onRemoveConfirm.bind(self, self.state.removeSelected)}/>
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
  hideRemove () {
    this.setState({
      removeSelected: null
    });
  },
  showRemove (item) {
    this.setState({
      removeSelected: item
    });
  },
	extractURISFromData: function(item){
		var uris = []; 
		for(let i in item.images)
			uris.push(item.images[i].uri);
		return uris;
	},
	onUnindexConfirm: function(item){
		console.log(item)
		var uris = this.extractURISFromData(item);
		
		let p = this.props.provider;

		ActionCreators.unindex(uris, p);
	},
	onRemoveConfirm: function(item){
		let uris = this.extractURISFromData(item);
		ActionCreators.remove(uris);
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
