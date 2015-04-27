var React = require('react');



var ServicesView = React.createClass({
      render: function() {

        return(<div>Services</div>);
        return (
          <div className="tab-pane" id="services">

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
                                                              <canvas id="myCanvas" width="30" height="30"></canvas>
                                                          </div>
                                                          <div className="col-xs-4">
                                                              <div id="GlobalTransferStorage" className="data-table">
                                                                  <div className="inline_block">
                                                                      Port
                                                                  </div>
                                                                  <div className="inline_block">
                                                                      <input type="text" className="form-control" placeholder="" value="8000"/>
                                                                  </div>
                                                                  <div className="checkbox">
                                                                      <label>
                                                                          <input type="checkbox"/>Auto Start
                                                                      </label>
                                                                  </div>
                                                              </div>
                                                          </div>
                                                          <div className="col-xs-4">
                                                              <div id="GlobalTransferStorage" className="data-table">
                                                                 <div className="inline_block">
                                                                  <button type="button" className="btn btn-danger" >Stop</button>
                                                                      </div>
                                                              </div>
                                                          </div>
                                                      </div>
                                                  </li>
          <li className="list-group-item list-group-item-management">
                                                      <div className="row">
                                                          <div className="col-xs-4">
                                                              <p>Query Retrieve</p>
                                                              <canvas id="myCanvas2" width="30" height="30"></canvas>
                                                          </div>
                                                          <div className="col-xs-4">
                                                              <div id="GlobalTransferStorage" className="data-table">
                                                                  <div className="inline_block">
                                                                      Port
                                                                  </div>
                                                                  <div className="inline_block">
                                                                      <input type="text" className="form-control"  placeholder="" value="8001"/>
                                                                  </div>
                                                                  <div className="checkbox">
                                                                      <label>
                                                                          <input type="checkbox"/>Auto Start
                                                                      </label>
                                                                  </div>
                                                              </div>
                                                          </div>
                                                          <div className="col-xs-4">
                                                              <div id="GlobalTransferStorage" className="data-table">
                                                                 <div className="inline_block">
                                                                  <button type="button" className="btn btn-success">Start</button>
                                                                      </div>
                                                                  <button type="button" className="btn btn-default">
            <span className="glyphicon glyphicon-cog"></span>
          </button>
                                                              </div>
                                                          </div>
                                                      </div>
                                                  </li>

                                              </ul>
                                          </div>
                                      </div>

                                  </div>

        );
      },
      });

export {
  ServicesView
}
