import React from 'react';

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import {PatientView} from './result/patientView';
import {StudyView} from './result/studyView';
import {SeriesView} from './result/serieView';
import {ImageView} from './result/imageView';
import {ExportView} from './exportView';
import Webcore from 'dicoogle-webcore';
import PluginForm from '../plugin/pluginForm.jsx';

var ResultSearch = React.createClass({

  getInitialState: function() {
    return {
      data: [],
      status: "loading",
      showExport: false,
      showDangerousOptions: false,
      current: 0,
      batchPlugins: [],
      currentPlugin: null
    };
  },

  componentDidMount: function() {
    this.initSearch(this.props.items);
  },

  componentWillMount: function() {
    // Subscribe to the store.
    SearchStore.listen(this._onChange);
    Webcore.fetchPlugins('result-batch', (packages) => {
      Webcore.fetchModules(packages);
      this.setState({batchPlugins: packages.map(pkg => ({
        name: pkg.name,
        caption: pkg.dicoogle.caption || pkg.name
      }))});
    });
  },

	initSearch: function(props){
    ActionCreators.search(props);
	},

  handleClickExport() {
    this.setState({showExport: true, currentPlugin: null});
  },

  handleClickBatchPluginButton(plugin) {
    this.setState({currentPlugin: plugin, showExport: false});
  },

  handleHideExport() {
    this.setState({showExport: false});
  },

  handleHideBatchForm() {
    this.setState({currentPlugin: null});
  },

  render: function() {

		if (this.state.status === "loading"){
      //loading animation
      return (<div className="loader-inner ball-pulse">
        <div/>
        <div/>
        <div/>
      </div>);
		}

    //Check if search fails
    if(this.state.success === false)
    {
      return (<div>Search error</div>);
    }
    //Check if search return no results
    if(this.state.data.numResults === 0)
    {
      return (
        <div>
        No results for that query
        </div>

        );
    }

    const pluginButtons = this.state.batchPlugins.map(plugin =>(
              <button key={plugin.name} className="btn btn_dicoogle fa dicoogle-webcore-result-batch-button"
                      onClick={this.handleClickBatchPluginButton.bind(this, plugin)}>
                {plugin.caption}
              </button>)
    );

    let toggleModalClassNames = this.state.showDangerousOptions ? "btn btn_dicoogle fa fa-toggle-on" : "btn btn_dicoogle fa fa-toggle-off";
    return (<div>
        <Step current={this.state.current} onClick={this.onStepClicked}/>
        <div id="step-container">
          {this.getCurrentView()}
        </div>
        <button className="btn btn_dicoogle fa fa-download" onClick={this.handleClickExport}>Export</button>
        <button className={toggleModalClassNames} onClick={this.toggleAdvOpt}> Advanced Options </button>
        {pluginButtons}
        <ExportView show={this.state.showExport} onHide={this.handleHideExport} query={this.props.items}/>
        <PluginForm show={!!this.state.currentPlugin} slotId="result-batch"
                    plugin={this.state.currentPlugin} onHide={this.handleHideBatchForm}
                    data={{results: this.state.data.results}} />
      </div>);
	},

  _onChange: function(data) {
    if (this.isMounted())
    {
      this.setState({data: data.data,
      status: "stopped",
      success: data.success});

      //init StepView
      if(!this.state.current)
        this.onStepClicked(0);
    }
  },

  getCurrentView() {
    let view;
    switch (this.state.current) {
      case 0:
        view = ( <PatientView items={this.state.data}
                              provider={this.props.items.provider}
                              enableAdvancedSearch={this.state.showDangerousOptions}
                              onItemClick={this.onPatientClicked}/>);
        break;
      case 1:
        view = ( <StudyView patient={this.state.patient}
                            enableAdvancedSearch={this.state.showDangerousOptions}
                            onItemClick={this.onStudyClicked}/>);
        break;
      case 2:
        view = ( <SeriesView study={this.state.study}
                             enableAdvancedSearch={this.state.showDangerousOptions}
                             onItemClick={this.onSeriesClicked}/>);
        break;
      case 3:
        view = ( <ImageView serie={this.state.serie}
                            enableAdvancedSearch={this.state.showDangerousOptions}/>);
        break;
    }
    return view;
  },

  onStepClicked: function(stepComponent){
    this.setState({current: stepComponent});
  },
  onPatientClicked: function(patient){
    this.setState({current: 1, patient});
  },
  onStudyClicked: function(study){
    this.setState({current: 2, study});
  },
  onSeriesClicked: function(serie){
    this.setState({current: 3, serie});
  },
  toggleAdvOpt: function(){
    this.setState({showDangerousOptions: !this.state.showDangerousOptions});
  }
});

var Step = React.createClass({
  getInitialState: function() {
    return {current: this.props.current};
  },
  componentWillReceiveProps: function(nextProps){
    this.setState({current: nextProps.current});
  },
  render: function() {

    return (
        <div className="row">
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current, 0)} onClick={this.onStepClicked.bind(this, 0)}>Patient</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current, 1)} onClick={this.onStepClicked.bind(this, 1)}>Study</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current, 2)} onClick={this.onStepClicked.bind(this, 2)}>Series</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current, 3)} onClick={this.onStepClicked.bind(this, 3)}>Image</div>
          </div>

        </div>
      );
  },
  getStep: function(current, step) {
    var state1 = "step current";
    var state2 = "step done";
    var state3 = "step disabled";

    if(step == current)
      return state1;
    else if(step > current)
      return state3;
    else if(step < current)
      return state2;
  },
  onStepClicked: function(current) {
      if(this.state.current <= current)
        return;
      this.setState({current: current});
      console.log("Step clicked");
      this.props.onClick(current);
  }

});

export {ResultSearch};
