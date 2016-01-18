import React from 'react';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table'; 
import {ActionCreators} from '../../../actions/searchActions';
import {SearchStore} from '../../../stores/searchStore';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';
import PluginView from '../../plugin/pluginView.jsx';
import {Input, ButtonInput} from 'react-bootstrap';

/**
   * 2015-09-11.
   * TODO: Read the Note. 
   * Note:
   * Dear Dicoogle Hacker, I know that you are upset with me 
   * because we did not use mixin here, studies, series and image. 
   * I understand your frustration, but I strongly believe, that I near future
   * we will be able to improve it as it desires. 
   * Best Regards.
   * 
   * ^^^
   * WAT
   */
var PatientView = React.createClass({
  getInitialState() {
    return {
      unindexSelected: null,
      removeSelected: null
    }
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
    return (<div onClick={self.onPatientClick.bind(this, item)} className="" style={{"cursor" : "pointer"}}>&nbsp;  {text}
    </div>)
  },
  formatID : function(cell, item){
    return this.formatGlobal(item.id, item);
  },
  formatGender : function(cell, item){
    return this.formatGlobal(item.gender, item);
  },
  formatNumberOfStudies : function(cell, item){
    return this.formatGlobal(item.nStudies, item);
  },
  formatName : function(cell, item){
    return this.formatGlobal(item.name, item);
  },

  formatOptions : function(cell, item){
      let self = this;
      if (this.props.enableAdvancedSearch)
          return (<div>
            <button title="Unindex (does not remove file physically)" onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser" />
            <button title="Removes the file physically" onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o" />
            {/* plugin-based result options */}
            <PluginView style={{display: 'inline-block'}} slotId="result-options" data={{
              'data-result-type': 'patient',
              'data-result-patient': item.name,
              'data-result-patientid': item.id
            }} />
        </div>

      );
      return (<div></div>);
  },
  
  
  formatSelect : function (cell, item){
      let self = this;
      if (this.props.enableAdvancedSearch)
          return (<div><Input className="" 
                         type="checkbox" label=" " ref="chk_{item.name}"/></div>
                    );
      return (<div></div>);
  },
  
  onRowSelect: function(row, isSelected){
    this.props.onItemClick(row);
  },
  onPatientClick: function(item){
    this.props.onItemClick(item);
  },

	render: function() {

		let self = this;

		var resultArray = this.props.items.results;
    let sizeOptions = "20%"
    let sizeSelect = "5%"

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };
    return (
			<div>

        <BootstrapTable  data={resultArray}  selectRow={selectRowProp} pagination={true} striped={true} hover={true}  width="100%">
          <TableHeaderColumn dataAlign="right" dataField="id" width="25%" isKey={true} dataFormat={this.formatID} dataSort={true}>ID</TableHeaderColumn>
          <TableHeaderColumn dataAlign="left" dataField="name" dataFormat={this.formatName} width="50%"  isKey={false} dataSort={true}>Name</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="gender" dataFormat={this.formatGender} width="12%"  dataSort={true}>Gender</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="nStudies" width="13%" dataFormat={this.formatNumberOfStudies} dataSort={true}>#Studies</TableHeaderColumn>
          <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="" width={sizeOptions} isKey={false} dataSort={false} dataFormat={this.formatOptions}>Options</TableHeaderColumn>
          <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="" width={sizeSelect} isKey={false} dataSort={false} dataFormat={this.formatSelect}></TableHeaderColumn>
          </BootstrapTable>

        <ConfirmModal show={this.state.unindexSelected !== null}
                      onHide={this.hideUnindex}
                      onConfirm={this.onUnindexConfirm.bind(this, this.state.unindexSelected)}/>
        <ConfirmModal show={this.state.removeSelected !== null}
                      message="The following files will be unindexed and then deleted from their storage."
                      onHide={this.hideRemove}
                      onConfirm={this.onRemoveConfirm.bind(this, this.state.removeSelected)}/>
      </div>
		);
	},
  extractURISFromData: function(item){
    var uris = [];
    for(let s in item.studies)
      for(let ss in item.studies[s].series)
        for(let i in item.studies[s].series[ss].images)
          uris.push(item.studies[s].series[ss].images[i].uri);
    return uris;
  },
    onUnindexConfirm: function(item){
      let uris = this.extractURISFromData(item);
      let p = this.props.provider;

      ActionCreators.unindex(uris, p);
    },
    onRemoveConfirm: function(item){
      let uris = this.extractURISFromData(item);
      ActionCreators.remove(uris);
    },

      hideUnindex () {

        this.setState({
          unindexSelected: null
        });
      },
      showUnindex (index) {
          console.log("Change the state");
            console.log(index);
            this.setState({
                unindexSelected: index
            });
      },
      hideRemove () {
        if (this.isMounted())
            this.setState({
                removeSelected: null
            });
      },
      showRemove (index) {
          this.setState({
              removeSelected: index,
              unindexSelected: null
          });


      },

      _onChange : function(data){

        if (this.isMounted())
        {
          this.setState({data:data.data,
          status:"stopped",
          success: data.success});
        }
  }
});

export {PatientView};
