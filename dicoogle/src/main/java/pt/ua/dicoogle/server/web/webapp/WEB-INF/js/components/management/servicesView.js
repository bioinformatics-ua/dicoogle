var React = require('react');



var ServicesView = React.createClass({
      componentDidMount: function(){
        //TransferenceActions.get();
       },
       componentDidUpdate: function(){
         this.drawCanvas();
        },
      render: function() {

        //return(<div>Services</div>);
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
                      <input type="text" className="form-control" style={{}} placeholder defaultValue={8000} />
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
                      <button type="button" className="btn btn-danger" style={{marginTop: 20}}>Stop</button>
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
                      <input type="text" className="form-control" style={{}} placeholder defaultValue={8001} />
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
                      <button type="button" className="btn btn-success" style={{marginTop: 20}}>Start</button>
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

      drawCanvas:function(){

            var canvas1 = document.getElementById("myCanvas");
             var canvas2 = document.getElementById("myCanvas2");
            draw(canvas1,1);
            draw(canvas2,0);
            function draw(e, status){
                 var canvas = e;
            var context = canvas.getContext('2d');
            var centerX = canvas.width / 2;
            var centerY = canvas.height / 2;
            var radius = 13;

            context.beginPath();
            context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
                if(Boolean(status))
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
