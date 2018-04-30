import React from 'react';
import $ from 'jquery';

import {IndexerStore} from '../../stores/indexerStore';
import {IndexerActions} from '../../actions/indexerActions';
import {saveIndexOptions} from '../../handlers/requestHandler';

const ConfigurationEntry = React.createClass({
  render() {
    return <li className="list-group-item list-group-item-management">
        <div className="row">
            <div className="col-xs-6 col-sm-4">
                {this.props.description}
            </div>
            <div className="col-xs-6 col-sm-8">
                {this.props.children}
            </div>
        </div>
    </li>
  }
});

const IndexerView = React.createClass({

      getInitialState: function() {
        return {
          data: {
            path: "",
            zip: false,
            effort: 0,
            thumbnail: false,
            thumbnailSize: 0,
            watcher: false
          },
          status: "loading",
          currentWatch: false
        };
      },
      componentDidMount: function() {
        console.log("componentdidmount: get");

        IndexerActions.get();
       },
      componentWillMount: function() {
        // Subscribe to the store
         console.log("subscribe listener");
         this.unsubscribe = IndexerStore.listen(this._onChange);
      },
      componentWillUnmount() {
        this.unsubscribe();
      },
      _onChange: function(data){
        console.log(data);
        var nState = {data: data.data, status: "done"};
        if (data.data.watcher) {
          nState.currentWatch = data.data.watcher;
        }
        this.setState(nState);
      },
      onToggleWatcher() {
        this.setState({currentWatch: !this.state.currentWatch});
      },
      render: function() {
        if(this.state.status === "loading"){
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }
        return (
          <div className="tab-content">

            <div className="panel panel-primary topMargin">
                              <div className="panel-heading">
                                  <h3 className="panel-title">Indexing Options</h3>
                              </div>
                              <div className="panel-body">

                                  <ul className="list-group">
                                      <ConfigurationEntry description="Enable Dicoogle Directory Watcher">
                                        <input id="watcher" type="checkbox" aria-label="..." checked={this.state.currentWatch} onChange={this.onToggleWatcher} />
                                      </ConfigurationEntry>
                                      <ConfigurationEntry description="Dicoogle Watcher Directory">
                                        <input id="mon_path" type="text" className="form-control" disabled={!this.state.currentWatch} defaultValue={this.state.data.path} placeholder="/path/to/directory"/>
                                      </ConfigurationEntry>
                                      <ConfigurationEntry description="Index Zip Files">
                                        <input id="zip" type="checkbox" aria-label="..." defaultChecked={this.state.data.zip} onChange={this.onZipClicked}/>
                                      </ConfigurationEntry>
                                      <ConfigurationEntry description="Indexation Effort">
                                        <input className="bar" type="range" id="effort_range" defaultValue={this.state.data.effort} onChange={this.onEffortChanged} />
                                      </ConfigurationEntry>
                                      <ConfigurationEntry description="Save Thumbnail">
                                        <input id="save" type="checkbox" aria-label="..." defaultChecked={this.state.data.thumbnail} onChange={this.onSaveTClicked}/>
                                      </ConfigurationEntry>
                                      <ConfigurationEntry description="Thumbnail Size">
                                        <input id="tsize" type="text" className="form-control" placeholder="Insert thumbnail size in pixels" defaultValue={this.state.data.thumbnailSize}/>
                                      </ConfigurationEntry>
                                  </ul>
                                  <button className="btn btn_dicoogle" onClick={this.onSaveClicked}>
                                    Save
                                  </button>
                                  <div className="toast">Saved</div>
                                </div>
                              </div>
                          </div>
        );
      },

      onWatcherClicked(e) {
        //setWatcher(document.getElementById(id).checked);
      },
      onZipClicked(e) {
        //setZip(document.getElementById(id).checked);
      },
      onSaveTClicked(e) {
        //setSaveT(document.getElementById(id).checked);
      },
      onEffortChanged(e) {
        //console.log(document.getElementById(id).value);
      },
      onSaveClicked() {
        $('.toast').stop().fadeIn(400).delay(3000).fadeOut(400); //fade out after 3 seconds
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

//<input className="bar" type="range" id="effort_range" defaultValue={this.props.value} onChange={this.props.onChange} />

export {
  IndexerView
}
