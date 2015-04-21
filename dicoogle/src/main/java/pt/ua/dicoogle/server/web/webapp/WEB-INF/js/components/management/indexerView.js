var React = require('react');
var IndexerView = React.createClass({
      render: function() {
        return (
          <div className="tab-content">
              <div className="panel panel-primary topMargin">
                                <div className="panel-heading">
                                    <h3 className="panel-title">Indexing Status</h3>
                                </div>
                                <div className="panel-body">
                                    <div className="progress">
                                        <div className="indexprogress progress-bar progress-bar-success progress-bar-striped" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100">
                                            <span className="sr-only">40% Complete (success)</span>
                                        </div>
                                    </div>

                                </div>
            </div>

            <div className="panel panel-primary">
                              <div className="panel-heading">
                                  <h3 className="panel-title">Indexing Options</h3>
                              </div>
                              <div className="panel-body">
                                  <ul className="list-group">
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Enable Dicoogle Directory Watcher
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input type="checkbox" aria-label="..." checked/>
                                              </div>
                                            </div>
                                      </li>
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Dicoogle Directory Monitorization
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input type="text" className="form-control" placeholder="/path/to/directory"/>
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Index Zip Files
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input type="checkbox" aria-label="..." checked/>
                                              </div>
                                          </div>
                                      </li>
                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Indexing effort(0-100)
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input className="bar" type="range" id="rangeinput" value="50" onchange="rangevalue.value=value" />
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Save Thumbnail
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input type="checkbox" aria-label="..." checked/>
                                              </div>
                                          </div>
                                      </li>

                                      <li className="list-group-item list-group-item-management">
                                          <div className="row">
                                              <div className="col-xs-6 col-sm-4">
                                                  Thumbnails Size
                                              </div>
                                              <div className="col-xs-6 col-sm-8">
                                                  <input type="text" className="form-control" placeholder="/path/to/directory" value="64"/>
                                              </div>
                                          </div>
                                      </li>

                                  </ul>
                                  </div>
                              </div>



                          </div>


      
        );
      }
    });

export {
  IndexerView
}
