var React = require('react');

import ServiceAction from '../../actions/servicesAction';
import ServicesStore from '../../stores/servicesStore';
import QueryAdvancedOptionsModal from './queryadvoptions';

var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;

var ServicesView = React.createClass({

    getInitialState () {
        return {
          storageRunning: false,
          storagePort: 0,
          storageAutostart: false,
          queryRunning: false,
          queryPort: 0,
          queryAutostart: false,
          status: "loading",
          storageLoading: true,
          queryLoading: true,
          showingAdvanced: false
        };
    },
      
      componentWillMount () {
        ServicesStore.listen(this._onChange);
      },
      
      componentDidMount () {
        ServiceAction.getStorage();
        ServiceAction.getQuery();
      },
      
      componentDidUpdate () {
        this.drawCanvas();
      },
      
      _onChange (data) {
        console.log(data);
        if(this.isMounted()) {
          this.setState({
          storageRunning: data.storageRunning,
          storagePort: data.storagePort,
          storageAutostart: data.storageAutostart,
          queryRunning: data.queryRunning,
          queryPort: data.queryPort,
          queryAutostart: data.queryAutostart,
          status: "done",
          storageLoading: false,
          queryLoading: false
          });

          console.log("Service data update: ", data);
        }
      },
      render () {
        var self = this;
        //return(<div>Services</div>);
        if(this.state.status === "loading"){
          return (<div className="loader-inner ball-pulse">
            <div/><div/><div/>
           </div>);
        }
        return (
      <div className="panel panel-primary topMargin">
        <div className="panel-heading">
          <h3 className="panel-title">Services</h3>
        </div>
        <div className="panel-body">
          <ul className="list-group">
            <li className="list-group-item list-group-item-management">
              <div className="row">
                <div className="col-xs-4">
                  <p>Storage</p>
                  <canvas id="myCanvas" width={30} height={30} />
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      Port
                    </div>
                    <div className="inline_block">
                      <input type="text" className="form-control" style={{}}
                             value={self.state.storagePort} placeholder="#port" onChange={self.handleStoragePortChange} />
                    </div>
                    <div className="checkbox">
                      <label>
                        <input type="checkbox" checked={self.state.storageAutostart}
                               onChange={this.toggleStorageAutostart}
                               disabled={this.state.storageLoading && "disabled"} /> Auto Start
                      </label>
                    </div>
                  </div>
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      {this.state.storageRunning ?
                        (  <button type="button" className="btn btn-danger" style={{marginTop: 20}} onClick={this.stopStorage}>Stop</button>) :
                        (  <button type="button" className="btn btn-success" style={{marginTop: 20}} onClick={this.startStorage}>Start</button>)
                      }
                    </div>
                  </div>
                </div>
              </div>
            </li>
            <li className="list-group-item list-group-item-management">
              <div className="row">
                <div className="col-xs-4">
                  <p>Query Retrieve</p>
                  <canvas id="myCanvas2" width={30} height={30} />
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      Port
                    </div>
                    <div className="inline_block">
                      <input id="queryport" type="text" className="form-control" style={{}}
                             placeholder="#port" value={self.state.queryPort} onChange={self.handleQueryPortChange}/>
                    </div>
                    <div className="checkbox">
                      <label>
                        <input type="checkbox" checked={self.state.queryAutostart}
                               onChange={this.toggleQueryAutostart}
                               disabled={this.state.queryLoading && "disabled"} /> Auto Start
                      </label>
                    </div>
                  </div>
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      {this.state.queryRunning ?
                        (  <button type="button" className="btn btn-danger" style={{marginTop: 20}}onClick={this.stopQuery}>Stop</button>) :
                        (  <button type="button" className="btn btn-success" style={{marginTop: 20}} onClick={this.startQuery}>Start</button>)
                      }
                    </div>
                    <button type="button" className="btn btn-default" style={{marginTop: 20, float: 'right'}} onClick={this.showAdvanced}>
                      <span className="glyphicon glyphicon-cog" />
                    </button>
                  </div>
                </div>
              </div>
            </li>
          </ul>
          <QueryAdvancedOptionsModal show={this.state.showingAdvanced} onHide={this.onHideAdvanced} />
        </div>
      </div>
        );
      },
      showAdvanced() {
        this.setState({showingAdvanced: true});
        ServiceAction.getQuerySettings();
      },
      onHideAdvanced() {
        this.setState({showingAdvanced: false});
      },
      handleQueryPortChange (event) {
        this.setState({queryPort: event.target.value});
      },
      handleStoragePortChange (event) {
        this.setState({storagePort: event.target.value});
      },
      startStorage () {
        console.log("start storage");
        ServiceAction.setStorage(true);
      },
      stopStorage () {
        ServiceAction.setStorage(false);
      },
      toggleStorageAutostart () {
        const newAutostart = !this.state.storageAutostart;
        this.setState({storageAutostart: newAutostart, storageLoading: true});
        ServiceAction.setStorageAutostart(newAutostart);
      },
      toggleQueryAutostart () {
        const newAutostart = !this.state.queryAutostart;
        this.setState({queryAutostart: newAutostart, queryLoading: true});
        ServiceAction.setQueryAutostart(newAutostart);
      },
      startQuery () {
        ServiceAction.setQuery(true);
      },
      stopQuery () {
        ServiceAction.setQuery(false);
      },
      
      drawCanvas () {
        var canvas1 = document.getElementById("myCanvas");
        var canvas2 = document.getElementById("myCanvas2");
        draw(canvas1,(this.state.storageRunning));
        draw(canvas2,(this.state.queryRunning));
        function draw(e, status){
          var canvas = e;
          var context = canvas.getContext('2d');
          var centerX = canvas.width / 2;
          var centerY = canvas.height / 2;
          var radius = 13;

          context.beginPath();
          context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
          if(status)
            context.fillStyle = 'green';
          else
            context.fillStyle = 'red';
          context.fill();
          context.lineWidth = 1;
          context.strokeStyle = '#003300';
          context.stroke();
        }
      }
});

export {ServicesView};