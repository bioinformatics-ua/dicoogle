var React = require('react');

import {StorageActions} from '../../actions/storageActions';
import {StorageStore} from '../../stores/storageStore';


var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;
var ModalTrigger = ReactBootstrap.ModalTrigger;
var Modal = ReactBootstrap.Modal;


var StorageView = React.createClass({
  getInitialState: function() {
    return {data: [],
      status: "loading"};
    },
    componentDidMount: function(){

      StorageActions.get();
      //$("#consolediv").scrollTop($("#consolediv")[0].scrollHeight);
    },
    componentWillMount: function() {
      StorageStore.listen(this._onChange);
    },
    _onChange:function(data){
      if (this.isMounted())
      this.setState({data:data.data});
    },
    render: function() {
      //if(this.state.data.length == 0)
      //return (<div>bil</div>);
      var moves = this.state.data.map(function(item,index){
        if(index == 0)
        return (<option selected value={index}>{item.AETitle}@{item.ipAddrs}:{item.port}</option>);
        else
        return (<option value={index}>{item.AETitle}@{item.ipAddrs}:{item.port}</option>);
      });


      return (

        <div className="panel panel-primary topMargin">
          <div className="panel-heading">
            Storage Servers
          </div>
          <div className="panel-body">
            <select className="form-control" id="moves" size={6} style={{width: '100%'}}>
              {moves}
            </select>
            <div style={{textAlign: 'left'}}>
              <ModalTrigger modal={<AddStorageView query={this.props.items}/>}>
                <button className="btn btn_dicoogle" OnClick={this.onAdd}>Add new</button>
              </ModalTrigger>

              <button  className="btn btn_dicoogle"  onClick={this.onRemove}>Remove</button>
            </div>


          </div>
        </div>
      );
    },
    onAdd:function(){

    },
    onRemove: function(){

      var index =document.getElementById("moves").selectedIndex;
      StorageActions.remove(index);
    }

  });


  var AddStorageView = React.createClass({
    render: function() {
      return(<Modal  {...this.props} bsStyle='primary' title='Add storage server' animation={true}>

        <div className='modal-body'>
          <div>
            AETitle
            <input id="storage_aetitle" className="form-control" style={{width: '100%'}} type="text" placeholder="aetitle" />
            <br></br>
            Ip Address
            <input id="storage_ip" className="form-control" style={{width: '100%'}} type="text" placeholder="ip address" />
            <br></br>
            Port
            <input id="storage_port" className="form-control" style={{width: '100%'}} type="text" placeholder="port" />
          </div>

        </div>
        <div className='modal-footer'>
          <Button onClick={this.onAdd}>Add</Button>
        </div>
      </Modal>);
    },

    onAdd:function(){
      console.log("Onadd clicked");
      StorageActions.add(
        document.getElementById("storage_aetitle").value,
        document.getElementById("storage_ip").value,
        document.getElementById("storage_port").value);

        this.props.onRequestHide();
      }
    });
    export {
      StorageView
    }
