import React from 'react';
import {Button, Modal} from 'react-bootstrap';
import {SearchStore} from '../../../stores/searchStore';
import {ActionCreators} from '../../../actions/searchActions';
import {unindex} from '../../../handlers/requestHandler';
import ConfirmModal from './confirmModal';
import {Endpoints} from '../../../constants/endpoints';
import {DumpStore} from '../../../stores/dumpStore';
import {DumpActions} from '../../../actions/dumpActions';

var ImageView = React.createClass({
  	getInitialState: function() {
    	return {data: [],
        image: null,
        dump: null,
    	  status: "loading",
        unindexSelected: null,
        removeSelected: null
      };
  	},
    componentDidMount: function(){
       $('#image-table').dataTable({paging: true,searching: false,info:true});
     },
     componentDidUpdate: function(){

     },
    componentWillMount: function() {
      // Subscribe to the store.
      SearchStore.listen(this._onChange);
    },
	render: function() {
		let self = this;
		var resultArray = this.props.serie.images;
		var resultItems = resultArray.map(function(item, i) {
          let uid = item.sopInstanceUID;
          let thumbUrl;
          if (false) {
            thumbUrl = Endpoints.base + "/dic2png?thumbnail=true&SOPInstanceUID=" + uid;
          }
          return (
              <tr key={i}>
                <td> {item.filename}</td>
                <td> {item.sopInstanceUID}</td>
                <td> {thumbUrl ? <img src={thumbUrl} width="64px" /> : "NA"} </td>
                <td>
                  <button type="button" onClick={self.showDump.bind(self, uid)} className="btn btn_dicoogle">Dump Image</button>
                  <button type="button" onClick={self.showImage.bind(self, uid)} className="btn btn_dicoogle">Show Image</button>
                </td>
                {(self.props.enableAdvancedSearch) && (<td>
                  <button onClick={self.showUnindex.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-eraser"> Unindex</button>
                  <button onClick={self.showRemove.bind(null, item)} className="btn btn_dicoogle btn-xs fa fa-trash-o"> Remove</button>
                </td>)}
              </tr>);
        });
    var header = (self.props.enableAdvancedSearch) ? (
      <tr>
            <th>FileName</th>
            <th>SopInstanceUID</th>
            <th>Thumbnail</th>
            <th></th>
            <th>Options</th>
          </tr>
      ) : (
      <tr>
            <th>FileName</th>
            <th>SopInstanceUID</th>
            <th>Thumbnail</th>
            <th></th>
          </tr>
      );

	return (
			<div>
				<table id="image-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
            {header}
          </thead>
          <tbody>
            {resultItems}
          </tbody>
        </table>
        <ConfirmModal show={self.state.unindexSelected !== null}
                      onHide={self.hideUnindex}
                      onConfirm={self.onUnindexConfirm.bind(self, self.state.unindexSelected)}/>
        <ConfirmModal show={self.state.removeSelected !== null}
                      message="The following files will be unindexed and then deleted from their storage."
                      onHide={self.hideRemove}
                      onConfirm={self.onRemoveConfirm.bind(self, self.state.removeSelected)}/>
        <PopOverView uid={this.state.dump} onHide={this.onHideDump} />
        <PopOverImageViewer uid={this.state.image} onHide={this.onHideImage}/>
			</div>
		);
	},
  onHideDump() {
    this.setState({dump: null});
  },
  onHideImage() {
    this.setState({image: null});
  },
  showDump(uid) {
    this.setState({dump: uid, image: null, unindexSelected: null});
    DumpActions.get(uid);
  },
  showImage(uid) {
    this.setState({dump: null, image: uid, unindexSelected: null});
  },
  hideUnindex () {
    this.setState({
      unindexSelected: null
    });
  },
  showUnindex (item) {
    this.setState({
      unindexSelected: item, dump: null, image: null
    });
  },
  hideRemove () {
    this.setState({
      removeSelected: null
    });
  },
  showRemove (item) {
    this.setState({
      removeSelected: item, dump: null, image: null
    });
  },
  onUnindexConfirm (item){
    console.log(item)
    var uris = []; 
    uris.push(item.uri);
    let p = this.props.provider;
    ActionCreators.unindex(uris, p);
  },
  onRemoveConfirm: function(item){
    var uris = []; 
    uris.push(item.uri);
    ActionCreators.remove(uris);
  },
    _onChange : function(data){
      console.log("onchange", data.success, data.status);
      if (this.isMounted())
      {
        this.setState({data: data.data,
          status:"stopped",
          success: data.success
        });
      }
    }
});

var PopOverView = React.createClass({
	getInitialState: function() {
    return {data: null,
    	status: "loading",
    	current: 0
    };
  },
  componentDidMount: function() {
  },
  componentWillMount: function() {
    // Subscribe to the store.
    DumpStore.listen(this._onChange);
  },
  componentDidUpdate: function(){
    $('#dumptable').dataTable({
      paging: false,
      searching: false,
      info: false,
      responsive: false
    });
  },

  _onChange: function(data){
    if (this.isMounted()) {
      this.setState({data:data, status: "stopped"});
    }
  },
    
  onHide () {
    this.setState({data: null});
    this.props.onHide();
  },

	render: function(){
		if(this.state.data === null) {
			return (
				<Modal {...this.props} show={this.props.uid !== null} bsStyle='primary' title='Image Dump' animation={true}>
          <div className="loader-inner ball-pulse">
            <div/>
            <div/>
            <div/>
          </div>
        </Modal>);
    }

    var obj = this.state.data.data.results.fields;
    var rows = [];

    var fields = [];
    Object.keys(obj).forEach(function(key, i) {
          rows.push(<p key={i}><b>{key}:</b> {obj[key]}</p>);
          fields.push({att: key, field: obj[key]});
       });

    var fieldstable = fields.map(function(item, i){
      return (
        <tr key={i}>
          <td>
            <p>{item.att}</p>
          </td>
          <td>
            <p>{item.field}</p>
          </td>
        </tr>
      );
    });

		return (
			<Modal onHide={this.props.onHide} show={this.props.uid !== null} bsClass='modal' bsStyle='primary' dialogClassName='table-dump'animation={true}>
          <Modal.Header>
            <Modal.Title>Image Dump</Modal.Title>
          </Modal.Header>
		        <div className='modal-body'>
              <table id="dumptable" className="table-test table table-striped table-bordered responsive" cellspacing="0" width="100%">
                <thead>
                  <tr>
                     <th>Attribute</th>
                     <th>Field</th>
                   </tr>
                </thead>
                <tbody>
                    {fieldstable}
                </tbody>
              </table>
            </div>
		        <div className='modal-footer'>
		          <Button onClick={this.props.onHide}>Close</Button>
		        </div>
			</Modal>
			);
	}
});

var PopOverImageViewer = React.createClass({
	getInitialState: function() {
    return {};
	},

	render:function() {
	  let url = (this.props.uid !== null) && Endpoints.base + "/dic2png?SOPInstanceUID="+this.props.uid;
		return (
			<Modal onHide={this.props.onHide} show={this.props.uid !== null} bsStyle='primary' animation={true}>
          <Modal.Header>
            <Modal.Title>View Image</Modal.Title>
          </Modal.Header>
          <div className='modal-body'>
            <img id="image1" src={this.props.uid ? url : null} width="100%" />
          </div>
          <div className='modal-footer'>
            <Button onClick={this.props.onHide}>Close</Button>
          </div>
			</Modal>
			);
	}
});

export {ImageView};
