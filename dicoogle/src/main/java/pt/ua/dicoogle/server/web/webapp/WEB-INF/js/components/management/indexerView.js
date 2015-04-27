var React = require('react');

import {IndexerStore} from '../../stores/indexerStore';
import {IndexerActions} from '../../actions/indexerActions';

import {setWatcher,setSaveT,setZip,saveIndexOptions} from '../../handlers/requestHandler';

var IndexerView = React.createClass({

        getInitialState: function() {
          return {data: {path:"",zip:false,effort:0,thumbnail:false,thumbnailSize:0,watcher:false},
          status: "loading"};
        },
        componentDidMount: function(){
          console.log("componentdidmount: get");

          IndexerActions.get();
         },
        componentWillMount: function() {
         	// Subscribe to the store.
           console.log("subscribe listener");
           IndexerStore.listen(this._onChange);
       	},
        _onChange: function(data){
          if (this.isMounted()){
            console.log(data);

            this.setState({data:data.data,status: "done"});
          }
        },
      render: function() {
        var self = this;
        if(this.state.status == "loading"){
          return (<div>loading</div>);
        }
        return (
          <div className="tab-content">

            <div className="panel panel-primary topMargin">
                              <div className="panel-heading">
                                  <h3 className="panel-title">Indexing Options</h3>
                              </div>
                              <div className="panel-body">
                                  <ul className="list-group">
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Enable Dicoogle Directory Watcher
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input id="watcher" type="checkbox" aria-label="..." defaultChecked={this.state.data.watcher} />
                                              </div>
                                            </div>
                                      </li>
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Dicoogle Directory Monitorization
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input id="mon_path" type="text" className="form-control" value={this.state.data.path} placeholder="/path/to/directory"/>
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Index Zip Files
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input id="zip" type="checkbox" aria-label="..." defaultChecked={this.state.data.zip} onChange={self.onZipClicked.bind(this,"zip")}/>
                                              </div>
                                          </div>
                                      </li>
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Indexing effort(0-100)
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input  className="bar" type="range" id="effort_range" defaultValue={this.state.data.effort} onChange={self.onEffortChanged.bind(this,"effort_range")} />
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Save Thumbnail
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input id="save" type="checkbox" aria-label="..." defaultChecked={this.state.data.thumbnail} onChange={self.onSaveTClicked.bind(this,"save")}/>
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Thumbnails Size
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input id="tsize" type="text" className="form-control" placeholder="/path/to/directory" defaultValue={this.state.data.thumbnailSize}/>
                                              </div>
                                          </div>
                                      </li>

                                  </ul>
                                  <button className="btn btn_dicoogle" onClick={self.onSaveClicked}>Save</button>
                                  </div>
                              </div>



                          </div>



        );
      },

      onWatcherClicked:function(id){
        //setWatcher(document.getElementById(id).checked);
      },
      onZipClicked:function(id){
        //setZip(document.getElementById(id).checked);
      },
      onSaveTClicked:function(id){
        //setSaveT(document.getElementById(id).checked);
      },
      onEffortChanged:function(id){
        //console.log(document.getElementById(id).value);
      },
      onSaveClicked:function(){
        console.log("onSaveClicked");
        saveIndexOptions(
          document.getElementById("mon_path").value,
          document.getElementById("watcher").checked,
          document.getElementById("zip").checked,
          document.getElementById("save").checked,
          document.getElementById("effort_range").value,
          document.getElementById("tsize").value

        );
      }
    });

export {
  IndexerView
}
