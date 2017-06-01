import React from 'react';

import {StorageActions} from '../../actions/storageActions';
import {StorageStore} from '../../stores/storageStore';
import {Button, Modal, FormGroup, FormControl, ControlLabel, Checkbox} from 'react-bootstrap';

const AddStorageModal = React.createClass({
    getInitialState() {
      return {
        aetitle: '',
        ip: '',
        port: '',
        public: false
      };
    },

  render: function() {
    return(<Modal {...this.props} bsStyle='primary' animation>
      <Modal.Header>
        <Modal.Title>Add Storage Server</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div>
          <FormGroup validationState={this.validateAETitle()}>
            <ControlLabel>AE Title</ControlLabel>
            <FormControl style={{width: '100%'}} type="text"
                    placeholder="AE Title" onChange={this.handleChangeAETitle}
                    value={this.state.aetitle} />
          </FormGroup>
          <FormGroup validationState={this.validateIPAddress()}>
            <ControlLabel>IP Address</ControlLabel>
            <FormControl style={{width: '100%'}} type="text"
                  placeholder="IP Address" onChange={this.handleChangeIPAddress} />
          </FormGroup>
          <FormGroup validationState={this.validatePort()}>
            <ControlLabel>Port</ControlLabel>
            <FormControl style={{width: '100%'}} type="text"
                  placeholder="Port" onChange={this.handleChangePort}
                  onKeyDown={this.handleFieldKeyDown} />
          </FormGroup>
          <FormGroup>
            <ControlLabel>Description</ControlLabel>
            <FormControl style={{width: '100%'}} type="text"
                  onChange={this.handleChangeDescription} onKeyDown={this.handleFieldKeyDown} />
          </FormGroup>
          <FormGroup>
            <Checkbox inline value={this.state.public} onChange={this.handleChangePublic}>
              Public
            </Checkbox>
          </FormGroup>
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button bsStyle="primary" disabled={!this.validateAll()} onClick={this.handleAdd}>Add</Button>
        <Button onClick={this.handleAdd}>Cancel</Button>
      </Modal.Footer>
    </Modal>);
  },
  
  handleFieldKeyDown(e) {
    if (e.keyCode === 13) {
      if (this.validateAll()) {
        this.handleAdd();
      }
    }
  },

  handleChangeAETitle(e) {
    this.setState({aetitle: e.target.value.toUpperCase()});
  },
  handleChangeIPAddress(e) {
    this.setState({ip: e.target.value});
  },
  handleChangePort(e) {
    this.setState({port: e.target.value});
  },
  handleChangeDescription(e) {
    this.setState({description: e.target.value});
  },
  handleChangePublic(e) {
    this.setState({public: !this.state.public});
  },

  validateAETitle(input) {
    input = (input || this.state.aetitle).trim();
    if (input === '') return undefined;
    return input.length <= 16 ? 'success' : 'error';
  },
  validateIPAddress(input) {
    input = (input || this.state.ip).trim();
    return input.length > 0 ? 'success' : undefined;
  },
  validatePort(input) {
    input = (input || this.state.port).trim();
    if (input === '') return undefined;
    const v = +input;
    return (v > 0 && v < 65536) ? 'success' : 'error';
  },
  validateAll() {
    return this.validateAETitle(this.state.aetitle) === 'success'
        && this.validateIPAddress(this.state.ip) === 'success'
        && this.validatePort(this.state.port) === 'success';
  },

  handleAdd () {
    const {aetitle, ip, port, description} = this.state;
    StorageActions.add(aetitle, ip, port, description, this.state.public);
    this.props.onHide();
  }
});

const StorageView = React.createClass({
    getInitialState: function() {
      return {
        data: [],
        showAdd: false,
        status: "loading",
        selectedIndex: null
      };
    },
    componentDidMount() {
      StorageActions.get();
    },
    componentWillMount() {
      this.unsubscribe = StorageStore.listen(this._onChange);
    },
    componentWillUnmount() {
      this.unsubscribe();
    },
    _onChange: function(data){
      this.setState({data: data.data});
    },

    render: function() {
      const moves = this.state.data.map((item, index) => (
            <option key={index} value={index} onClick={this.handleSelect}>
              {item.aetitle + " @ " + item.ip + ":" + item.port}
              {item.description && (" - " + item.description)}
              {item.public && ' (public)'}
            </option>));

      return (

        <div className="panel panel-primary topMargin">
          <div className="panel-heading">
            Storage Servers
          </div>
          <div className="panel-body">
            <select defaultValue={0} className="form-control" size={6} style={{width: '100%'}}>
              {moves}
            </select>
            <div style={{textAlign: 'left', marginTop: '8px'}}>
              <button className="btn btn_dicoogle" onClick={this.onAdd}>Add New</button>
              <button className="btn btn_dicoogle" disabled={typeof this.state.selectedIndex !== 'number'}
                      onClick={this.onRemove}>Remove</button>
            </div>
          </div>
          <AddStorageModal show={this.state.showAdd} onHide={this.onHideAdd} />
        </div>
      );
    },
    handleSelect(e) {
      this.setState({selectedIndex: +e.target.value});
    },
    onAdd() {
      this.setState({showAdd: true});
    },
    onHideAdd() {
      this.setState({showAdd: false});
    },
    onRemove() {
      const index = this.state.selectedIndex;
      StorageActions.remove(index);
    }

});

export {StorageView};
