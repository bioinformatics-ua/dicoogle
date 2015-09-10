import React from 'react';
import {Button} from 'react-bootstrap';

import {SearchStore} from '../../stores/searchStore';
import {ActionCreators} from '../../actions/searchActions';

import {PatientView} from './result/patientView';
import {StudyView} from './result/studyView';
import {SeriesView} from './result/serieView';
import {ImageView} from './result/imageView';
import {ExportView} from './exportView';

var ResultSearch = React.createClass({

  getInitialState: function() {
    return {data: [],
    status: "loading",
    showExport: false,
    showDangerousOptions: false,
    current: 0};
  },
  componentDidMount: function() {

  	this.initSearch(this.props.items);

  },

  componentWillMount: function() {
    // Subscribe to the store.
    SearchStore.listen(this._onChange);
  },

	initSearch: function(props){
    console.log("PARAM: ", props);
    ActionCreators.search(props);
	},
  
  onClickExport() {
    this.setState({showExport: true});
  },

  onHideExport() {
    this.setState({showExport: false});
  },

  render: function() {
    var self = this;

		if (this.state.status === "loading"){
		  //loading animation
      return (<div className="loader-inner ball-pulse">
      <div></div>
      <div></div>
      <div></div>
      </div>);
		}

    //Check if search fails
    if(this.state.success === false)
    {
      return (<div> Search error</div>);
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

    var arraylist = this.state.data.results;

    var resultNodes = (
      arraylist.map(function(item){
		      return (
				          <li className="list_item"> {item.uri}</li>
			           );
       })
	    );

    var toggleModalClassNames = this.state.showDangerousOptions ? "btn btn_dicoogle fa fa-toggle-on" : "btn btn_dicoogle fa fa-toggle-off";
    return (<div>
        <Step current={this.state.current} onClick={this.onStepClicked}/>
        <div id="step-container">
          {this.getCurrentView()}
        </div>
        <button className="btn btn_dicoogle fa fa-download" onClick={this.onClickExport}>Export</button>
        <ExportView show={this.state.showExport} onHide={this.onHideExport} query={this.props.items}/>
        <button className={toggleModalClassNames} onClick={this.toggleAdvOpt}> Advanced Options </button>
      </div>);
	},

  _onChange : function(data){
    console.log("onchange");
    console.log(data.success);
    console.log(data.status);
    if (this.isMounted())
    {
      this.setState({data:data.data,
      status:"stopped",
      current: this.state.current,
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
        view = ( <SeriesView  study={this.state.study}
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

  onStepClicked:function(stepComponent){
    console.log(stepComponent);
    this.setState({current: stepComponent});

//    var view;
//    if(stepComponent == 0)
//      view = ( <PatientView items={this.state.data} provider={this.props.items.provider} enableAdvancedSearch={this.state.data.advancedOptions} onItemClick={this.onPatientClicked}/>);
//    else if(stepComponent == 1)
//      view = ( <StudyView patient={this.state.patient} enableAdvancedSearch={this.state.data.advancedOptions} onItemClick={this.onStudyClicked}/>);
//    else if(stepComponent == 2)
//      view = ( <SeriesView study={this.state.study} enableAdvancedSearch={this.state.data.advancedOptions} onItemClick={this.onSeriesClicked}/>);
//    else if(stepComponent == 3)
//      view = ( <ImageView serie={this.state.serie} enableAdvancedSearch={this.state.data.advancedOptions}/>);
//
//    React.render(view, document.getElementById("step-container"));
  },

  onPatientClicked:function(patient){
    //console.log("patient id: ",id," Index: ",index);
    this.setState({current: 1, patient:patient});
//    React.render(<StudyView patient={patient} enableAdvancedSearch={this.state.showDangerousOptions} onItemClick={this.onStudyClicked}/>, document.getElementById("step-container"));
  },
  onStudyClicked:function(study){
    this.setState({current: 2, study: study});
//    React.render(<SeriesView study={study} enableAdvancedSearch={this.state.showDangerousOptions} onItemClick={this.onSeriesClicked} />, document.getElementById("step-container"));
  },
  onSeriesClicked:function(serie){
    this.setState({current: 3, serie: serie});
//    React.render(<ImageView serie={serie} enableAdvancedSearch={this.state.showDangerousOptions} />, document.getElementById("step-container"));
  },
  toggleAdvOpt: function(){
    this.setState({showDangerousOptions : !this.state.showDangerousOptions});
  }
});

var Step = React.createClass({
  getInitialState: function() {
    return {current: this.props.current};
  },
  componentWillReceiveProps: function(nextProps){
    this.setState({current:nextProps.current});
  },
  render: function() {

    return (
        <div className="row">
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current,0)} onClick={this.onStepClicked.bind(this, 0)}>Patient</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current,1)} onClick={this.onStepClicked.bind(this,1)}>Study</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current,2)} onClick={this.onStepClicked.bind(this,2)}>Series</div>
          </div>
          <div className="col-xs-3 stepa">
            <div className={this.getStep(this.state.current,3)} onClick={this.onStepClicked.bind(this,3)}>Image</div>
          </div>

        </div>
      );
  },
  getStep:function(current, step){
    var state1="step current";
    var state2="step done";
    var state3="step disabled";

    if(step == current)
      return state1;
    else if(step>current)
      return state3;
    else if(step < current)
      return state2;
  },
  onStepClicked:function(current)
  {
      if(this.state.current <= current)
        return;
      this.setState({current: current});
      console.log("Step clicked");
      this.props.onClick(current);
  }

});

export {ResultSearch};
