var React = require('react');

import {IndexStatusActions} from "../../actions/indexStatusAction";
import {IndexStatusStore} from "../../stores/indexStatusStore";

var refreshIntervalId;
var IndexStatusView = React.createClass({
      getInitialState: function() {
        return {data: {},
        status: "loading"};
      },
      componentDidMount: function(){
        IndexStatusActions.get();

        //Start refresh interval
        refreshIntervalId = setInterval(this.update, 3000);
        //$("#consolediv").scrollTop($("#consolediv")[0].scrollHeight);
       },
       componentDidUpdate:function(){
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
         IndexStatusStore.listen(this._onChange);
       },
      _onChange: function(data){
        if (this.isMounted()){

          this.setState({data:data.data,status: "done"});
        }
      },
      render: function() {
        var self = this;
        if(this.state.status === "loading"){
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }

        let items;
        if (this.state.data.results.length === 0) {
          items = (<div>No tasks</div>);
        } else {
          items = this.state.data.results.map(function(item){
            let complete = item.complete;
            let percentage = complete ? '100%'
              : (item.taskProgress >= 0) ? (item.taskProgress * 100 + '%') : '0%';

            let barstate = "indexprogress progress-bar progress-bar-striped progress-bar-success";
            if(item.nErrors > 0 && item.nIndexed > 0){
              barstate = "indexprogress progress-bar progress-bar-striped progress-bar-warning";
            }
            else if((item.nErrors > 0) && (item.nIndexed == 0))
            {
              barstate = "indexprogress progress-bar progress-bar-striped progress-bar-danger";
            }
            else {
              barstate = "indexprogress progress-bar progress-bar-striped progress-bar-success";
            }
            return (
              <div className="well well-sm">
                <div className="row">
                    <div className="col-sm-10">
                      <div className="progress indexstatusprogress">
                          <div style={{width : percentage}} className={barstate} role="progressbar"  aria-valuemin="0" aria-valuemax="100">

                          </div>
                      </div>
                    </div>
                    <div className="col-sm-2">
                      <button className="btn btn-danger" onClick={self.onCloseStopClicked.bind(this,complete, item.taskUid)}> {complete?"Close":"Stop"} </button>
                    </div>
                </div>

                <div>
                    <p><b>Uid: </b> {item.taskUid}</p>
                    <p><b>Name: </b> {item.taskName}</p>
                    {item.complete && <p><b>Elapsed Time(ms): </b> {item.elapsedTime} <b>Indexed: </b>{item.nIndexed} <b>Errors: </b>{item.nErrors}</p>}
                </div>

              </div>
            );
          });
        }
        return (
          <div className="container">
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
                <button className="btn btn_dicoogle" onClick={this.onStartClicked}>Start</button>
              </div>
            </div>
            <div className="panel panel-primary topMargin">
              <div className="panel-heading">
                  <h3 className="panel-title">{this.state.data.count === 0 ? "No tasks currently running" :
                    ("Indexing Status ("+this.state.data.count+" running)")}</h3>
              </div>
              <div className="panel-body">
                  {items}
              </div>
            </div>
          </div>
        );
      },
      onStartClicked : function(){
        IndexStatusActions.start(document.getElementById("path").value);
      },
      onCloseStopClicked : function(type, uid){
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
