import React from 'react';
import {Button, Modal} from 'react-bootstrap';
import ServiceAction from '../../actions/servicesAction';
import ServicesStore from '../../stores/servicesStore';

const QueryAdvancedOptionsModal = React.createClass({
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
  componentWillMount() {
    this.unsubscribe = ServicesStore.listen(this._onChange);
  },
  componentWillUnmount: function() {
    this.unsubscribe();
  },
  _onChange: function(data){
    const querySettings = data.querySettings;
    this.setState({
      connectionTimeout: querySettings.connectionTimeout,
      acceptTimeout: querySettings.acceptTimeout,
      idleTimeout: querySettings.idleTimeout,
      maxAssociations: querySettings.maxAssociations,
      maxPduReceive: querySettings.maxPduReceive,
      maxPduSend: querySettings.maxPduSend,
      responseTimeout: querySettings.responseTimeout,
      status: "done"
    });
   },
  render: function() {
    return (<Modal {...this.props} bsStyle='primary' title='Query Retrieve - Advanced Settings' animation>
      <div className='modal-body'>
        <div className="container-fluid">
          <div className="row">
            <div className="col-md-4">Response timeout:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_response_t"
                     value={this.state.responseTimeout} onChange={this.handleResponseTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Connection timeout:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_connection_t"
                     value={this.state.connectionTimeout} onChange={this.handleConnectionTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Idle timeout:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_idle_t"
                     value={this.state.idleTimeout} onChange={this.handleIdleTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Accept timeout:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_accept_t"
                     value={this.state.acceptTimeout} onChange={this.handleAcceptTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Max PDU Send:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_max_pdu_send"
                     value={this.state.maxPduSend} onChange={this.handleMaxPduSendTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Max Associations:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_max_associations"
                     value={this.state.maxAssociations} onChange={this.handleMaxAssociationsTimeoutChange}/>
            </div>
          </div>
          <br></br>
          <div className="row">
            <div className="col-md-4">Max PDU Receive:</div>
            <div className="col-md-8">
              <input className="form-control" id="input_max_pdu_receive"
                     value={this.state.maxPduReceive} onChange={this.handleMaxPduReceiveTimeoutChange}/>
            </div>
          </div>
        </div>
      </div>
      <div className='modal-footer'>
        <Button onClick={this.onSave}>Save</Button>
      </div>
    </Modal>);
  },
  handleResponseTimeoutChange: function(event){
    this.setState({responseTimeout: event.target.value});
  },
  handleConnectionTimeoutChange: function(event){
    this.setState({connectionTimeout: event.target.value});
  },
  handleIdleTimeoutChange: function(event){
    this.setState({idleTimeout: event.target.value});
  },
  handleAcceptTimeoutChange: function(event){
    this.setState({acceptTimeout: event.target.value});
  },
  handleMaxPduSendTimeoutChange: function(event){
    this.setState({maxPduSend: event.target.value});
  },
  handleMaxPduReceiveTimeoutChange: function(event){
    this.setState({maxPduReceive: event.target.value});
  },
  handleMaxAssociationsTimeoutChange: function(event){
    this.setState({maxAssociations: event.target.value});
  },
  onSave() {
    console.log("onSave clicked");
    ServiceAction.saveQuerySettings(
        this.state.connectionTimeout,
        this.state.acceptTimeout,
        this.state.idleTimeout,
        this.state.maxAssociations,
        this.state.maxPduReceive,
        this.state.maxPduSend,
        this.state.responseTimeout);
    this.props.onHide();
  }
});

export default QueryAdvancedOptionsModal;
