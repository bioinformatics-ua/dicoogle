var React = require('react');

import {ServiceAction} from '../../actions/servicesAction';
import {ServicesStore} from '../../stores/servicesStore';

var ServicesView = React.createClass({
      getInitialState: function() {
        return {

          storageRunning: false,
          storagePort: 0,
          queryRunning: false,
          queryPort: 0,

        status: "loading"};
      },
      componentWillMount: function(){
        ServicesStore.listen(this._onChange);
       },
      componentDidMount: function(){
        ServiceAction.getStorage();
        ServiceAction.getQuery();
       },
       componentDidUpdate: function(){
         this.drawCanvas();
        },
      _onChange:function(data){
        console.log(data);
        if(this.isMounted())
        this.setState({
          storageRunning: data.storageRunning,
          storagePort: data.storagePort,
          queryRunning: data.queryeRunning,
          queryPort: data.queryPort,
          status: "done"
          });

        console.log(this.state.data.storagePort);
      },
      render: function() {
        var self = this;
        //return(<div>Services</div>);
        if(this.state.status == "loading"){
          return (<div>loading</div>);
        }
        return (
      <div className="panel panel-primary topMargin">
        <div className="panel-heading">
          <h3 className="panel-title">Services and Plugins</h3>
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
                        <input type="checkbox" />Auto Start
                      </label>
                    </div>
                  </div>
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      {this.state.storageRunning=="true" ?
                        (  <button type="button" className="btn btn-danger" style={{marginTop: 20}}>Stop</button>) :
                        (  <button type="button" className="btn btn-success" style={{marginTop: 20}}>Start</button>)

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
                        <input type="checkbox" />Auto Start
                      </label>
                    </div>
                  </div>
                </div>
                <div className="col-xs-4">
                  <div id="GlobalTransferStorage" className="data-table">
                    <div className="inline_block">
                      {this.state.queryRunning=="true" ?
                        (  <button type="button" className="btn btn-danger" style={{marginTop: 20}}>Stop</button>) :
                        (  <button type="button" className="btn btn-success" style={{marginTop: 20}}>Start</button>)

                      }
                    </div>
                    <button type="button" className="btn btn-default" style={{marginTop: 20, float: 'right'}}>
                      <span className="glyphicon glyphicon-cog" />
                    </button>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>



        );
      },
      handleQueryPortChange :function(event){
        this.setState({queryPort: event.target.value});
      },
      handleStoragePortChange : function(event){
        this.setState({storagePort: event.target.value});
      },
      drawCanvas:function(){

            var canvas1 = document.getElementById("myCanvas");
             var canvas2 = document.getElementById("myCanvas2");
            draw(canvas1,(this.state.storageRunning == "true"));
            draw(canvas2,(this.state.queryRunning == "true"));
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

export {
  ServicesView
}
