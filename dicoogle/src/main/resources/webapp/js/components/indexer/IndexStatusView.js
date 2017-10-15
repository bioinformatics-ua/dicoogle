import React from 'react';
import {IndexStatusActions} from "../../actions/indexStatusAction";
import {IndexStatusStore} from "../../stores/indexStatusStore";
import {ProvidersActions} from '../../actions/providersActions';
import {ProvidersStore} from '../../stores/providersStore';
import TaskStatus from "./TaskStatus.jsx";

import Select from 'react-select';

var refreshIntervalId;
const IndexStatusView = React.createClass({
      getInitialState: function() {
        return {
          data: {},
          status: "loading",
          providers: [],
          selectedProviders: []
        };
      },
      componentDidMount: function(){
        IndexStatusActions.get();

        //Start refresh interval
        refreshIntervalId = setInterval(this.update, 3000);
        //$("#consolediv").scrollTop($("#consolediv")[0].scrollHeight);
        ProvidersActions.get();
       },
       componentDidUpdate: function(){
         console.log("indexstatus update");
         //if(this.state.data.count !=0)
         //{
           //setInterval(IndexStatusActions.get(), 5000);
         //}
       },
       componentWillUnmount: function(){
         console.log("IndexStatusView unmounted");
         //Stop refresh interval
         clearInterval(refreshIntervalId);

       },
      update: function(){
        IndexStatusActions.get();
      },
      componentWillMount: function() {
         // Subscribe to the store.
         console.log("subscribe listener");
         ProvidersStore.listen(this._onProvidersChange);
         IndexStatusStore.listen(this._onChange);
       },
      _onChange: function(data){
        if (this.isMounted()){

          this.setState({data: data.data, status: "done"});
        }
      },
      render: function() {
        if(this.state.status === "loading"){
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }

        let items;
        if (this.state.data.results.length === 0) {
          items = (<div>No tasks</div>);
        } else {
          items = this.state.data.results.map(item => (
            <TaskStatus key={item.taskUid} index={item.taskUid} item={item} onCloseStopClicked={this.onCloseStopClicked.bind(this, item.taskUid, item.complete)} />
          ));
        }

        let providersList = this.state.providers.map(item => ({value: item, label: item}));

        return (
          <div className="">
            <div className="panel panel-primary topMargin">
              <div className="panel-heading">
                <h3 className="panel-title">Start indexing</h3>
              </div>
              <div className="panel-body">
                <div className="row">
                  <div className="col-xs-6 col-sm-2">
                    Index directory:
                  </div>
                  <div className="col-xs-6 col-sm-10">
                    <input id="path" type="text" className="form-control" value={this.state.data.path} placeholder="/path/to/directory"/>
                  </div>
                </div>
                <div className="row" style={{marginTop: 15}}>
                  <div className="col-xs-6 col-sm-2">
                    Index providers:
                  </div>
                  <div className="col-xs-6 col-sm-10">
                    <Select multi
                      id="providersList"
                      name="form-field-name"
                      value={this.state.selectedProviders}
                      options={providersList}
                      placeholder="All Providers"
                      onChange={this.handleProviderSelect}
                    />
                  </div>
                </div>
                <button className="btn btn_dicoogle" onClick={this.onStartClicked}>Start</button>
              </div>
            </div>
            <div className="panel panel-primary topMargin">
              <div className="panel-heading">
                  <h3 className="panel-title">{this.state.data.count === 0 ? "No tasks currently running" :
                    ("Indexing Status (" + this.state.data.count + " running)")}</h3>
              </div>
              <div className="panel-body">
                  {items}
              </div>
            </div>
          </div>
        );
      },
      onStartClicked: function(){
        IndexStatusActions.start(document.getElementById("path").value, this.state.selectedProviders);
      },
      onCloseStopClicked: function(uid, type){
        if(type){
          IndexStatusActions.close(uid);
        }
        else{
          IndexStatusActions.stop(uid);
        }
      },
      _onProvidersChange: function(data) {
        if (this.isMounted())
          this.setState({providers: data.data});
      },
      handleProviderSelect(providers) {
        this.setState({
          selectedProviders: providers.map((e) => e.value)
        });
      }
      });

export {
  IndexStatusView
};
