import React from 'react';
import {IndexStatusActions} from "../../actions/indexStatusAction";
import {IndexStatusStore} from "../../stores/indexStatusStore";
import TaskStatus from "./TaskStatus";

var refreshIntervalId;
const IndexStatusView = React.createClass({
      getInitialState: function() {
        return {data: {},
        status: "loading"};
      },
      componentDidMount: function(){
        IndexStatusActions.get();

        //Start refresh interval
        refreshIntervalId = setInterval(this.update, 2500);
       },
       componentDidUpdate: function(){
         console.log("indexstatus update");
       },
       componentWillUnmount: function(){
         console.log("IndexStatusView unmounted");
         //Stop refresh interval
         clearInterval(refreshIntervalId);
         this.unsubscribe();
       },
      update: function(){
        IndexStatusActions.get();
      },
      componentWillMount: function() {
         // Subscribe to the store.
         console.log("subscribe listener");
         this.unsubscribe = IndexStatusStore.listen(this._onChange);
       },
      _onChange: function(contents){
          this.setState({
            data: contents.data,
            status: "done"
          });
      },
      render: function() {
        const {data} = this.state;
        if(this.state.status === "loading"){
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }

        let items;
        if (data.tasks.length === 0) {
          items = (<div>No tasks</div>);
        } else {
          items = data.tasks.map(item => (
            <TaskStatus key={item.taskUid} index={item.taskUid} item={item} onCloseStopClicked={this.onCloseStopClicked.bind(this, item.taskUid, item.complete)} />
          ));
        }
        return (
          <div>
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
                    <input id="path" type="text" className="form-control" value={data.path} placeholder="/path/to/directory"/>
                  </div>
                </div>
                <button className="btn btn_dicoogle" onClick={this.onStartClicked}>Start</button>
              </div>
            </div>
            <div className="panel panel-primary topMargin">
              <div className="panel-heading">
                  <h3 className="panel-title">{data.count === 0 ? "No tasks currently running" :
                    ("Indexing Status (" + data.count + " running)")}</h3>
              </div>
              <div className="panel-body">
                  {items}
              </div>
            </div>
          </div>
        );
      },
      onStartClicked: function(){
        IndexStatusActions.start(document.getElementById("path").value);
      },
      onCloseStopClicked: function(uid, type){
        if(type){
          IndexStatusActions.close(uid);
        }
        else{
          IndexStatusActions.stop(uid);
        }
      }
});

export {
  IndexStatusView
};
