import React from 'react';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {SearchStore} from '../../../stores/searchStore';
import {ActionCreators} from '../../../actions/searchActions';
import ConfirmModal from './confirmModal';
import PluginView from '../../plugin/pluginView.jsx';
import {Input} from 'react-bootstrap';
import ResultSelectActions from '../../../actions/resultSelectAction';
import {UserStore} from '../../../stores/userStore';


const SeriesView = React.createClass({
  getInitialState: function() {
    // We need this because refs are not updated in BootstrapTable.
    this.refsClone = {};
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
    ResultSelectActions.clear();
  },

  /**
   * 2015-09-11:
   * This method returns a React Component that only has the text and couple of
   * events (such as click). Today, react-bootstrap-table does not support selectRows
   * without appear radio ou checkbox.
   *
   */

  formatGlobal: function(text, item){
    let self = this;
    return (<div onClick={self.onSeriesClick.bind(this, item)} className="" style={{"cursor": "pointer"}}>&nbsp; {text}
    </div>)
  },

  _wrapResult: function(result){
    if (result === undefined)
      result = "";
    return result;
  },
  formatNumber: function(cell, item){
    return this._wrapResult(this.formatGlobal(item.serieNumber, item));

  },
  formatModality: function(cell, item){
    return this._wrapResult(this.formatGlobal(item.serieModality, item));
  },
  formatDescription: function(cell, item){
    return this._wrapResult(this.formatGlobal(item.serieDescription, item));
  },
  formaImages: function(cell, item){
    return this._wrapResult(this.formatGlobal(item.images.length, item));
  },

  formatOptions: function(cell, item){
      let self = this;
      let isAdmin = UserStore.isAdmin();
      let unindex = null;
      let removeFiles = null;



    if (this.props.enableAdvancedSearch)
      {
        if (isAdmin) {
          unindex = (
              <button title="Unindex (does not remove file physically)" onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> </button>);
          removeFiles = (<button title="Removes the file physically" onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o"> </button>);
        }
        return (<div>

              {unindex}
              {removeFiles}

              {/* plugin-based result options */}
              <PluginView style={{display: 'inline-block'}} slotId="result-options" data={{
                type: 'series',
                uid: item.serieInstanceUID,
                // deprecated data fields
                'data-result-type': 'series',
                'data-result-uid': item.serieInstanceUID
         }} />
            </div>
        );

      }

      return (<div></div>);
  },

  handleSelect(item){

    let {serieInstanceUID} = item;
    // ResultSelectActions.select(item);
    let value = this.refsClone[serieInstanceUID].getChecked();
    if (value)
      ResultSelectActions.select(item, serieInstanceUID);
    else
      ResultSelectActions.unSelect(item, serieInstanceUID);

  },
  handleRefs: function (id, input){
    this.refsClone[id] = input;
  },
  formatSelect: function (cell, item){
    let {serieInstanceUID} = item;
    let classNameForIt = "advancedOptions " + serieInstanceUID;
    return (<div className={classNameForIt}>
              <Input type="checkbox" label=""
                    onChange={this.handleSelect.bind(this, item)}
                    ref={this.handleRefs.bind(this, serieInstanceUID)}/>
            </div>
    );
  },
  sizePerPageListChange(sizePerPage){

  },

  onPageChange(page, sizePerPage) {

  },

  onRowSelect: function(row){
    this.props.onItemClick(row);
  },
  onSeriesClick: function(item){
		this.props.onItemClick(item);
	},
	render: function() {
		const self = this;

    var resultArray = this.props.study.series;

    var selectRowProp = {
      clickToSelect: true,
      mode: "none",
      bgColor: "rgb(163, 210, 216)",
      onSelect: this.onRowSelect
    };


    // TODO trigger this action elsewhere
    ResultSelectActions.level("series");


    return (
			<div>
        <BootstrapTable data={resultArray} selectRow={selectRowProp} condensed pagination striped hover width="100%">
          <TableHeaderColumn dataAlign="right" dataField="serieInstanceUID" isKey dataFormat={this.formatNumber} dataSort>Number</TableHeaderColumn>
          <TableHeaderColumn dataAlign="left" dataField="serieModality" dataFormat={this.formatModality} isKey={false} dataSort>Modality</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="serieDescription" dataFormat={this.formatDescription} dataSort>Description</TableHeaderColumn>
          <TableHeaderColumn dataAlign="center" dataField="serieInstanceUID" dataFormat={this.formaImages} dataSort>#Images</TableHeaderColumn>
          <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="serieInstanceUID" isKey={false} dataSort={false} dataFormat={this.formatOptions}>Options</TableHeaderColumn>
          <TableHeaderColumn hidden={!this.props.enableAdvancedSearch} dataAlign="center" dataField="serieInstanceUID" dataSort dataFormat={this.formatSelect}>#S</TableHeaderColumn>
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
		let uris = [];
		for(let i in item.images)
			uris.push(item.images[i].uri);
		return uris;
	},
	onUnindexConfirm: function(item){
		console.log(item)
		let uris = this.extractURISFromData(item);
		let p = this.props.provider;
		ActionCreators.unindex(uris, p);
	},
	onRemoveConfirm: function(item){
		let uris = this.extractURISFromData(item);
		ActionCreators.remove(uris);
	},

  _onChange: function(data){

    if (this.isMounted())
    {
      this.setState({data: data.data,
      status: "stopped",
      success: data.success,
      enableAdvancedSearch: data.data.advancedOptions});
    }
  }
});

export {SeriesView};
