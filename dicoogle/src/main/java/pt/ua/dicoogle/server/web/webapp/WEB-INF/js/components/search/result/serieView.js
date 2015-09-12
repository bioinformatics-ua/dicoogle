import React from 'react';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
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
   
    componentWillMount: function() {
    	// Subscribe to the store.
    	SearchStore.listen(this._onChange);
  	},
    
  /**
   * 2015-09-11:
   * This method returns a React Component that only has the text and couple of 
   * events (such as click). Today, react-bootstrap-table does not support selectRows
   * without appear radio ou checkbox.
   * 
   */
  
  formatGlobal : function(text, item){
    let self = this;
    return (<div onClick={self.onSeriesClick.bind(this, item)} className="" style={{"cursor" : "pointer"}}>&nbsp;  {text}
    </div>)
  },

  formatNumber : function(cell, item){
    return this.formatGlobal(item.serieNumber, item);
  },
  formatModality : function(cell, item){
    return this.formatGlobal(item.serieModality, item);
  },
  formatDescription : function(cell, item){
    return this.formatGlobal(item.serieDescription, item);
  },
  formaImages : function(cell, item){
    return this.formatGlobal(item.images.length, item);
  },

  formatOptions : function(cell, item){
      let self = this;
      if (this.props.enableAdvancedSearch)
          return (<div><button title="Unindex (does not remove file physically)" onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> </button>
        <button title="Removes the file physically" onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o"> </button></div>

      );
      return (<div></div>);
  },
  onRowSelect: function(row, isSelected){
    this.props.onItemClick(row);
  },
  onSeriesClick:function(item){
		this.props.onItemClick(item);
	},
	render: function() {
		const self = this;

		var resultArray = this.props.study.series;
    let sizeOptions = "20%"

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
    

    return ( // FIXME bad labels and bad values in table
			<div>
        <BootstrapTable  data={resultArray}  selectRow={selectRowProp} pagination={true} striped={true} hover={true}  width="100%">
          <TableHeaderColumn dataAlign="right" dataField="id" width="20%" isKey={true} dataFormat={this.formatNumber} dataSort={true}>Number</TableHeaderColumn>
          <TableHeaderColumn dataAlign="left" dataField="name" dataFormat={this.formatModality} width="20%"  isKey={false} dataSort={true}>Modality</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="gender" dataFormat={this.formatDescription} width="40%"  dataSort={true}>Description</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="nStudies" width="20%" dataFormat={this.formaImages} dataSort={true}>#Images</TableHeaderColumn>
          <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="" width={sizeOptions} isKey={false} dataSort={false} dataFormat={this.formatOptions}>Options</TableHeaderColumn>
          </BootstrapTable>
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
      if (this.isMounted())
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex (item) {
      if (this.isMounted())
    this.setState({
      unindexSelected: item
    });
  },
  hideRemove () {
      if (this.isMounted())
    this.setState({
      removeSelected: null
    });
  },
  showRemove (item) {
      if (this.isMounted())
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
