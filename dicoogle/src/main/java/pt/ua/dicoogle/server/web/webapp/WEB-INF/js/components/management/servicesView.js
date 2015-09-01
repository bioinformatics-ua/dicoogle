var React = require('react');

import ServiceAction from '../../actions/servicesAction';
import ServicesStore from '../../stores/servicesStore';

var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;
var ModalTrigger = ReactBootstrap.ModalTrigger;
var Modal = ReactBootstrap.Modal;

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
          queryLoading: true
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
        if(this.isMounted())
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

      //  console.log(this.state.data.storagePort);
      },
      render () {
        var self = this;
        //return(<div>Services</div>);
        if(this.state.status == "loading"){
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
                      <input type="text" className="form-control" style={{}} value={self.state.storagePort} placeholder="#port" onChange={self.handleStoragePortChange} />
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
                      <input id="queryport" type="text" className="form-control" style={{}} placeholder="#port" value={self.state.queryPort} onChange={self.handleQueryPortChange}/>
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
                    <ModalTrigger modal={<QueryAdvancedOptions/>}>
                      <button type="button" className="btn btn-default" style={{marginTop: 20, float: 'right'}}>
                        <span className="glyphicon glyphicon-cog" />
                      </button>
                    </ModalTrigger>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

        );
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


      var QueryAdvancedOptions = React.createClass({
        getInitialState: function() {
          return {

            acceptTimeout: "...",
            connectionTimeout: "...",
            idleTimeout: "...",
            maxAssociations: "...",
            maxPduReceive: "...",
            maxPduSend: "...",
            responseTimeout: "...",
          status: "loading"
          };
        },
        componentWillMount: function(){
          ServicesStore.listen(this._onChange);
         },
        componentDidMount: function(){
          ServiceAction.getQuerySettings();

         },
         _onChange: function(data){
          if (this.isMounted())
          this.setState({
            connectionTimeout: data.connectionTimeout,
            acceptTimeout: data.acceptTimeout,
            idleTimeout: data.idleTimeout,
            maxAssociations: data.maxAssociations,
            maxPduReceive: data.maxPduReceive,
            maxPduSend: data.maxPduSend,
            responseTimeout: data.responseTimeout
            });
         },
        render: function() {
          var self = this;
          return(<Modal  {...this.props} bsStyle='primary' title='Query Retrieve - Advanced Settings' animation={true}>

            <div className='modal-body'>

              <div className="container-fluid">
                <div className="row">
                  <div className="col-md-4">Response timeout:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_response_t" value={this.state.responseTimeout} onChange={self.handleResponseTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Connection timeout:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_connection_t" value={this.state.connectionTimeout} onChange={self.handleConnectionTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Idle timeout:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_idle_t" value={this.state.idleTimeout} onChange={self.handleIdleTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Accept timeout:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_accept_t" value={this.state.acceptTimeout} onChange={self.handleAcceptTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Mas PDU Send:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_max_pdu_send" value={this.state.maxPduSend} onChange={self.handleMaxPduSendTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Max Associations:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_max_associations" value={this.state.maxAssociations} onChange={self.handleMaxAssociationsTimeoutChange}/>
                  </div>
                </div>
                <br></br>
                <div className="row">
                  <div className="col-md-4">Mas PDU Receive:</div>
                  <div className="col-md-8">
                    <input className="form-control" id="input_max_pdu_receive" value={this.state.maxPduReceive} onChange={self.handleMaxPduReceiveTimeoutChange}/>
                  </div>
                </div>
              </div>

            </div>
            <div className='modal-footer'>
              <Button onClick={this.onSave}>Save</Button>
            </div>
          </Modal>);
        },
        handleResponseTimeoutChange : function(event){
          this.setState({responseTimeout: event.target.value});
        },
        handleConnectionTimeoutChange : function(event){
          this.setState({connectionTimeout: event.target.value});
        },
        handleIdleTimeoutChange : function(event){
          this.setState({idleTimeout: event.target.value});
        },
        handleAcceptTimeoutChange : function(event){
          this.setState({acceptTimeout: event.target.value});
        },
        handleMaxPduSendTimeoutChange : function(event){
          this.setState({maxPduSend: event.target.value});
        },
        handleMaxPduReceiveTimeoutChange : function(event){
          this.setState({maxPduReceive: event.target.value});
        },
        handleMaxAssociationsTimeoutChange : function(event){
          this.setState({maxAssociations: event.target.value});
        },
        onSave:function(){
          console.log("Onadd clicked");
          ServiceAction.saveQuerySettings(
            document.getElementById("input_connection_t").value,
            document.getElementById("input_accept_t").value,
            document.getElementById("input_idle_t").value,
            document.getElementById("input_max_associations").value,
            document.getElementById("input_max_pdu_receive").value,
            document.getElementById("input_max_pdu_send").value,
            document.getElementById("input_response_t").value);

            this.props.onRequestHide();
          }
        });

export {
  ServicesView
}
