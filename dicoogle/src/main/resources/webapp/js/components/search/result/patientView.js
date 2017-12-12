import React from 'react';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {ActionCreators} from '../../../actions/searchActions';
import {SearchStore} from '../../../stores/searchStore';
import ConfirmModal from './confirmModal';
import PluginView from '../../plugin/pluginView.jsx';
import {Input} from 'react-bootstrap';
import ResultSelectActions from '../../../actions/resultSelectAction';
import {UserStore} from '../../../stores/userStore';


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
const PatientView = React.createClass({
  getInitialState() {

    // We need this because refs are not updated in BootstrapTable.
    this.refsClone = {};

    return {
      unindexSelected: null,
      removeSelected: null,
      resultsSelected: []
    }
  },

  componentWillMount: function() {
    // Subscribe to the store.
    SearchStore.listen(this._onChange);
    ResultSelectActions.clear();
  },

  /**
   * 2015-09-11:
   * This method returns a React Component that only has the text and couple of
   * events (such as click). Today, react-bootstrap-table does not support selectRows
   * without appear radio ou checkbox.
   */

  formatGlobal: function(text, item){
    return (<div onClick={this.onPatientClick.bind(this, item)} className="" style={{"cursor": "pointer"}}>&nbsp; {text}
    </div>)
  },
  formatID: function(cell, item){
    return this.formatGlobal(item.id, item);
  },
  formatGender: function(cell, item){
    return this.formatGlobal(item.gender, item);
  },
  formatNumberOfStudies: function(cell, item){
    return this.formatGlobal(item.nStudies, item);
  },
  formatName: function(cell, item){
    return this.formatGlobal(item.name, item);
  },

  formatOptions: function(cell, item){

          let isAdmin = UserStore.isAdmin();
          let unindex = null;
          let removeFiles = null;
          if (this.props.enableAdvancedSearch)
          {
              if (isAdmin) {
                  unindex = (
                      <button title="Unindex (does not remove file physically)" onClick={this.showUnindex.bind(null, item)}
                              className="btn btn_dicoogle btn-xs fa fa-eraser"/>);

                  removeFiles = (<button title="Removes the file physically" onClick={this.showRemove.bind(null, item)}
                                         className="btn btn_dicoogle btn-xs fa fa-trash-o"/>);
              }
          }
          if (this.props.enableAdvancedSearch)
              return (<div>
                    {unindex}
                    {removeFiles}
                {/* plugin-based result options */}
                <PluginView style={{display: 'inline-block'}} slotId="result-options" data={{
                  type: 'patient',
                  patientName: item.name,
                  patientId: item.id,
                  // deprecated data fields
                  'data-result-type': 'patient',
                  'data-result-patient': item.name,
                  'data-result-patientid': item.id
                }} />
            </div>);
          return (<div></div>);

  },
  handleSelect(item){
      let {id} = item;
     // ResultSelectActions.select(item);
      let value = this.refsClone[id].getChecked();
      if (value)
        ResultSelectActions.select(item, id);
      else
        ResultSelectActions.unSelect(item, id);


  },
  handleRefs: function (id, input){
      this.refsClone[id] = input;
  },
  formatSelect: function (cell, item){
    let {id} = item;
    let classNameForIt = "advancedOptions " + id;
    return (<div className={classNameForIt}>
              <Input type="checkbox" label=""
                    onChange={this.handleSelect.bind(this, item)}
                    ref={this.handleRefs.bind(this, id)}/>
            </div>
    );
  },
  onRowSelect: function(row, isSelected){
    this.props.onItemClick(row);
  },
  onPatientClick: function(item){
    this.props.onItemClick(item);
  },
  sizePerPageListChange(sizePerPage){

  },

  onPageChange(page, sizePerPage) {

  },
	render: function() {
    this.options = {
      sortName: 'id',
      sortOrder: 'desc',
      sizePerPageList: [ 5, 10, 20, 50, 100, 200 ],
      sizePerPage: 50,
      onPageChange: this.onPageChange
    };

    let resultArray = this.props.items.results;
    let selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };

    // TODO trigger this action elsewhere
    ResultSelectActions.level("patient");
    return (
        <div>
          <BootstrapTable options={this.options} data={resultArray} selectRow={selectRowProp} condensed pagination striped hover width="100" >
            <TableHeaderColumn dataAlign="right" dataField="id" isKey dataFormat={this.formatID} dataSort>ID</TableHeaderColumn>
            <TableHeaderColumn dataAlign="left" dataField="name" dataFormat={this.formatName} isKey={false} dataSort>Name</TableHeaderColumn>
            <TableHeaderColumn dataAlign="center" dataField="gender" dataFormat={this.formatGender} dataSort>Gender</TableHeaderColumn>
            <TableHeaderColumn dataAlign="center" dataField="nStudies" dataFormat={this.formatNumberOfStudies} dataSort>#Studies</TableHeaderColumn>
            <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="Opts" dataSort={false} dataFormat={this.formatOptions}>Options</TableHeaderColumn>
            <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="Select" dataSort dataFormat={this.formatSelect}>#S</TableHeaderColumn>
          </BootstrapTable>

          <ConfirmModal show={this.state.unindexSelected !== null}
                      onHide={this.hideUnindex}
                      onConfirm={this.onUnindexConfirm.bind(this, this.state.unindexSelected)}/>
          <ConfirmModal show={this.state.removeSelected !== null}
                      message="The following files will be unindexed and then deleted from their storage."
                      onHide={this.hideRemove}
                      onConfirm={this.onRemoveConfirm.bind(this, this.state.removeSelected)}/>
        </div>);
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
  _onChange: function(data){
    if (this.isMounted())
    {
      this.setState({data: data.data,
      status: "stopped",
      success: data.success});
    }
  }
});

export {PatientView};
