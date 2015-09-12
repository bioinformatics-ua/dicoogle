import React from 'react';
import {SearchStore} from '../../../stores/searchStore';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table'; 

import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';

var StudyView = React.createClass({
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
    return (<div onClick={self.onStudyClick.bind(this, item)} className="" style={{"cursor" : "pointer"}}>&nbsp;  {text}
    </div>)
  },
  formatStudyDate : function(cell, item){
    return this.formatGlobal(item.studyDate, item);
  },
  formatStudyDescription : function(cell, item){
    return this.formatGlobal(item.studyDescription, item);
  },
  formatInstitutionName : function(cell, item){
    return this.formatGlobal(item.institutionName, item);
  },
  formatModalities : function(cell, item){
    return this.formatGlobal(item.modalities, item);
  }, 
  
  formatOptions : function(cell, item){
      let self = this;
      if (this.props.enableAdvancedSearch)
          return (<div><button title="Unindex (does not remove file physically)" onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> </button>
        <button title="Removes the file physically" onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o"> </button></div>

      );
      return (<div></div>);
  },
    
    
    
	render: function() {
		var self = this;
		var resultArray = this.props.patient.studies;
    
    let sizeOptions = "20%"

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
  
  
    return (
        <div>
            <BootstrapTable  data={resultArray}  selectRow={selectRowProp} pagination={true} striped={false} hover={true}  width="100%">
            <TableHeaderColumn dataAlign="right" dataField="studyDate" width="18%" isKey={true} dataFormat={this.formatStudyDate} dataSort={true}>Date</TableHeaderColumn>
            <TableHeaderColumn dataAlign="left" dataField="studyDescription" dataFormat={this.formatStudyDescription} width="40%"  isKey={false} dataSort={true}>Description</TableHeaderColumn>
            <TableHeaderColumn dataAlign="center" dataField="institutionName" dataFormat={this.formatInstitutionName} width="30%"  dataSort={true}>Institution</TableHeaderColumn>
            <TableHeaderColumn dataAlign="center" dataField="modalities" width="14%" dataFormat={this.formatModalities} dataSort={true}>Modality</TableHeaderColumn>
            <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="" width={sizeOptions} isKey={false} dataSort={false} dataFormat={this.formatOptions}>Options</TableHeaderColumn>
            </BootstrapTable>
          <ConfirmModal show={self.state.unindexSelected !== null}
                        onHide={self.hideUnindex}
                        onConfirm={self.onUnindexConfirm.bind(self, self.state.unindexSelected)} />
          <ConfirmModal show={self.state.removeSelected !== null}
                        message="The following files will be unindexed and then deleted from their storage."
                        onHide={self.hideRemove}
                        onConfirm={self.onRemoveConfirm.bind(self, self.state.removeSelected)} />
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
      unindexSelected: item,
      removeSelected: null
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
      removeSelected: item,
      unindexSelected: null
    });
  },
	extractURISFromData: function(item){
		var uris = []; 
		for(let ss in item.series)
			for(let i in item.series[ss].images)
				uris.push(item.series[ss].images[i].uri);
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
	onStudyClick:function(item){
		this.props.onItemClick(item);
	},
  onRowSelect: function(row, isSelected){
    this.props.onItemClick(row);
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
