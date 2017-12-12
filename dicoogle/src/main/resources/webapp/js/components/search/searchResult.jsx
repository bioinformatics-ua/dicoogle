import React, {PropTypes} from 'react';

import {PatientView} from './result/patientView';
import {StudyView} from './result/studyView';
import {SeriesView} from './result/serieView';
import {ImageView} from './result/imageView';
import {ExportView} from './exportView';
import Webcore from 'dicoogle-webcore';
import PluginForm from '../plugin/pluginForm.jsx';
import {DefaultOptions} from '../../constants/defaultOptions';
import {SearchStore} from '../../stores/searchStore';

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
    SearchStore.listen(this._onSearchResult);
  },

  componentWillUpdate(nextProps) {

    if (this.props.requestedQuery.text !== nextProps.requestedQuery.text) {
      //init StepView
      if(!this.state.current)
        this.onStepClicked(0);
    }
  },
_onSearchResult: function(outcome) {
      if (this.isMounted())
      {
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

    let counters = [];
    if (this.props.searchOutcome.data) {
      counters.push(this.props.searchOutcome.data.results.length);
    }
    if (this.state.current >= 1 && this.state.patient) {
      counters.push(this.state.patient.nStudies);
    }
    if (this.state.current >= 2 && this.state.study) {
      counters.push(this.state.study.series.length);
    }
    if (this.state.current >= 3 && this.state.serie) {
      counters.push(this.state.serie.images.length);
    }

    return (<div className="container-fluid">
        <div className="row">
          <Step current={this.state.current} counters={counters} onClick={this.onStepClicked}/>
        </div>
        <div id="step-container">
          {this.getCurrentView()}
        </div>
        <button className="btn btn_dicoogle" onClick={this.handleClickExport}><i className="fa fa-download"/>Export</button>
        <button className="btn btn_dicoogle" onClick={this.toggleAdvOpt}><i className={toggleModalClassNames}/> Advanced Options </button>
        {pluginButtons}
        <ExportView show={this.state.showExport} onHide={this.handleHideExport} query={this.props.requestedQuery}/>
        <PluginForm show={!!this.state.currentPlugin} slotId="result-batch"
                    plugin={this.state.currentPlugin} onHide={this.handleHideBatchForm}
                    data={{
                      query: this.props.requestedQuery.text,
                      queryProvider: this.props.requestedQuery.provider,
                      results: this.props.searchOutcome.data.results}} />
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
    this.setState({current: 1, patient, study: null, serie: null});
  },
  onStudyClicked: function(study){
    this.setState({current: 2, study, serie: null});
  },
  onSeriesClicked: function(serie){
    this.setState({current: 3, serie});
  },
  toggleAdvOpt: function(){
    this.setState({showDangerousOptions: !this.state.showDangerousOptions});
  }
});

const Step = React.createClass({
  propTypes: {
    current: PropTypes.number,
    counters: PropTypes.arrayOf(PropTypes.number).isRequired
  },
  render: function() {
    const {current, counters: [nPatients, nStudies, nSeries, nImages]} = this.props;

    return (
        <div className="wizardbar">
          <div onClick={this.onStepClicked.bind(this, 0)} className={this.getStep(current, 0)}>
            <div>
              {nPatients !== undefined && <span className="label label-pill label-primary label-as-badge label-border">{nPatients}</span>}
              &nbsp; Patient
            </div>
          </div>
          <div onClick={this.onStepClicked.bind(this, 1)} className={this.getStep(current, 1)}>
            <div>
              {nStudies !== undefined && <span className="label label-pill label-primary label-as-badge label-border">{nStudies}</span>}
              &nbsp; Study
            </div>
          </div>
          <div onClick={this.onStepClicked.bind(this, 2)} className={this.getStep(current, 2)}>
            <div>
              {nSeries !== undefined && <span className="label label-pill label-primary label-as-badge label-border">{nSeries}</span>}
              &nbsp; Series
            </div>
          </div>
          <div onClick={this.onStepClicked.bind(this, 3)} className={this.getStep(current, 3)}>
            <div>
              {nImages !== undefined && <span className="label label-pill label-primary label-as-badge label-border">{nImages}</span>}
              &nbsp; Image
            </div>
          </div>
        </div>
      );
  },
  getStep: function(current, step) {
    if(step === current)
      return "col-xs-3 wizardbar-item current";
    else if(step > current)
      return "col-xs-3 wizardbar-item disabled";
    else if(step < current)
      return "col-xs-3 wizardbar-item completed";
  },
  onStepClicked: function(current) {
      this.props.onClick(current);
  }

});

export {SearchResult};
