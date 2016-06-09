import React, {PropTypes} from 'react';

import {PatientView} from './result/patientView';
import {StudyView} from './result/studyView';
import {SeriesView} from './result/serieView';
import {ImageView} from './result/imageView';
import {ExportView} from './exportView';
import Webcore from 'dicoogle-webcore';
import PluginForm from '../plugin/pluginForm.jsx';
import {DefaultOptions} from '../../constants/defaultOptions';

const SearchResult = React.createClass({

  propTypes: {
    requestedQuery: PropTypes.shape({
      text: PropTypes.string,
      keyword: PropTypes.bool,
      other: PropTypes.bool,
      provider: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.arrayOf(PropTypes.string)
      ])
    }).isRequired, // requested query
    searchOutcome: PropTypes.shape({
      data: PropTypes.shape({
        numResults: PropTypes.number,
        results: PropTypes.array
      }),
      error: PropTypes.any
    }).isRequired,
    onReturn: PropTypes.func
  },

  getDefaultProps() {
    return {
      onReturn: null
    }
  },

  getInitialState: function() {
    return {
      showExport: false,
      showDangerousOptions: DefaultOptions.showSearchOptions,
      current: 0,
      batchPlugins: [],
      currentPlugin: null
    };
  },

  componentWillMount: function() {
    Webcore.fetchPlugins('result-batch', (packages) => {
      Webcore.fetchModules(packages);
      this.setState({batchPlugins: packages.map(pkg => ({
        name: pkg.name,
        caption: pkg.dicoogle.caption || pkg.name
      }))});
    });
  },

  componentWillUpdate(nextProps) {
    if (this.props.requestedQuery.queryText !== nextProps.requestedQuery.queryText) {
      //init StepView
      if(!this.state.current)
        this.onStepClicked(0);
    }
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

  isLoading() {
    return !this.props.searchOutcome;
  },

  getError() {
    return this.props.searchOutcome.error;
  },

  render: function() {
    const {searchOutcome} = this.props;

		if (this.isLoading()){
      //loading animation
      return (<div className="loader-inner ball-pulse">
        <div/>
        <div/>
        <div/>
      </div>);
		}


    //Check if search failed
    if(this.getError()) {
      return (
        <div>
          <p>{this.getError()}</p>
        </div>
        );
    }
    //Check if search return no results
    if(searchOutcome.data && searchOutcome.data.numResults === 0)
    {
      return (
        <div>
          <p>No results for that query</p>
        </div>
        );
    }

    const pluginButtons = this.state.batchPlugins.map(plugin =>(
              <button key={plugin.name} className="btn btn_dicoogle fa dicoogle-webcore-result-batch-button"
                      onClick={this.handleClickBatchPluginButton.bind(this, plugin)}>
                {plugin.caption}
              </button>)
    );

    let toggleModalClassNames = this.state.showDangerousOptions ? "fa fa-toggle-on" : "fa fa-toggle-off";
    return (<div>
        <Step current={this.state.current} onClick={this.onStepClicked}/>
        <div id="step-container">
          {this.getCurrentView()}
        </div>
        <button className="btn btn_dicoogle" onClick={this.handleClickExport}><i className="fa fa-download"/>Export</button>
        <button className="btn btn_dicoogle" onClick={this.toggleAdvOpt}><i className={toggleModalClassNames}/> Advanced Options </button>
        {pluginButtons}
        <ExportView show={this.state.showExport} onHide={this.handleHideExport} query={this.props.requestedQuery}/>
        <PluginForm show={!!this.state.currentPlugin} slotId="result-batch"
                    plugin={this.state.currentPlugin} onHide={this.handleHideBatchForm}
                    data={{results: this.props.searchOutcome.data.results}} />
      </div>);
	},

  getCurrentView() {
    let view;
    switch (this.state.current) {
      case 0:
        view = ( <PatientView items={this.props.searchOutcome.data}
                              provider={this.props.requestedQuery.provider}
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

const Step = React.createClass({
  getInitialState: function() {
    return {current: this.props.current};
  },
  componentWillReceiveProps: function(nextProps){
    this.setState({current: nextProps.current});
  },
  render: function() {  

    return (
        <div className="wizardbar ">
          <div  className={this.getStep(this.state.current, 0)}>
            <div onClick={this.onStepClicked.bind(this, 0)}>Patient</div>
          </div>
          <div  className={this.getStep(this.state.current, 1)}>
            <div onClick={this.onStepClicked.bind(this, 1)}>Study</div>
          </div>
          <div  className={this.getStep(this.state.current, 2)}>
            <div  onClick={this.onStepClicked.bind(this, 2)}>Series</div>
          </div>
          <div className={this.getStep(this.state.current, 3)}>
            <div   onClick={this.onStepClicked.bind(this, 3)}>Image</div>
          </div>

        </div>  
      );
  },
  getStep: function(current, step) {
    var state1 = "wizardbar-item current";
    var state2 = "wizardbar-item completed";
    var state3 = "wizardbar-item disabled";

    if(step === current)
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

export {SearchResult};
