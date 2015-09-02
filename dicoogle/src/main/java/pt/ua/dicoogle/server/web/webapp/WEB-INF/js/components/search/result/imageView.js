var React = require('react');

import {Button, Modal} from 'react-bootstrap';
import {Endpoints} from '../../../constants/endpoints';
import {DumpStore} from '../../../stores/dumpStore';
import {DumpActions} from '../../../actions/dumpActions';

var ImageView = React.createClass({
  	getInitialState: function() {
    	return {data: [],
        image: null,
        dump: null,
    	  status: "loading"};
  	},
    componentDidMount: function() {
      console.log("componentDidMount : Paginating...");
      $('#image-table').dataTable({paging: true, searching: false, info: true});
    },
    componentDidUpdate: function() {
//        $('#image-table').dataTable({paging: true, searching: false, info: true});
    },
	render: function() {
		let self = this;
		var resultArray = this.props.serie.images;
		var resultItems = resultArray.map(function(item, i) {
          let uid = item.sopInstanceUID;
          let thumbUrl;
          if (false)
            thumbUrl = Endpoints.base + "/dic2png?thumbnail=true&SOPInstanceUID=" + uid;
              
          return (
              <tr key={i}>
                <td> {item.filename}</td>
                <td> {item.sopInstanceUID}</td>
                <td> {thumbUrl ? <img src={thumbUrl} width="64px" /> : "NA"} </td>
                <td>
                  <button type="button" onClick={self.showDump.bind(self, uid)} className="btn btn_dicoogle">Dump Image</button>
                  <button type="button" onClick={self.showImage.bind(self, uid)} className="btn btn_dicoogle">Show Image</button>
                </td>
              </tr>
              );
        });

	  return (
			<div>
				<table id="image-table" className="table table-striped table-bordered" cellspacing="0" width="100%">
					<thead>
           				<tr>
                			<th>FileName</th>
                			<th>SopInstanceUID</th>
                			<th>Thumbnail</th>
                      <th></th>
                    </tr>
        			</thead>
        			 <tbody>
           				{resultItems}
            		</tbody>
    			</table>
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
    this.setState({dump: uid, image: null});
    DumpActions.get(uid);
  },
  showImage(uid) {
    this.setState({dump: null, image: uid});
  }

});

var PopOverView = React.createClass({
	getInitialState: function() {
    return {data: null};
  },
  componentDidMount: function() {
    //$("#image1").imgViewer();
  },
  componentWillMount: function() {
    // Subscribe to the store.
    DumpStore.listen(this._onChange);
  },
  componentDidUpdate: function() {
    //$('#dumptable').dataTable({paging: false, searching: false, info: false, responsive: false});
  },

  _onChange: function(data){
    if (this.isMounted()) {
      this.setState({data:data});
    }
  },
    
  onHide () {
    this.setState({data: null});
    this.props.onHide();
  },

	render: function(){
		if(this.state.data === null)
			return (
				<Modal {...this.props} show={this.props.uid !== null} bsStyle='primary' title='Image Dump' animation={true}>
          <div className="loader-inner ball-pulse">
            <div/>
            <div/>
            <div/>
          </div>
        </Modal>);

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
			<Modal onHide={this.props.onHide} show={this.props.uid !== null} bsStyle='primary' title='Image Dump' animation={true}>
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
			<Modal onHide={this.props.onHide} show={this.props.uid !== null} bsStyle='primary' title='Image Dump' animation={true}>
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
