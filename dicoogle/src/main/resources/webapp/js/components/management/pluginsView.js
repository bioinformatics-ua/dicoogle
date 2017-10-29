import React from 'react';
import * as PluginActions from "../../actions/pluginActions";
import PluginStore from "../../stores/pluginStore";

const PluginsView = React.createClass({

  getInitialState () {
    return {
      plugins: {},
      currentlyLoading: 4,
      status: "loading"
    };
  },

  componentWillMount () {
    this.unsubscribe = PluginStore.listen(this._onPluginsChange);
  },

  componentDidMount: function(){
    const pluginTypes = ["query", "index", "storage", "servlet"];
    pluginTypes.map(type => PluginActions.get(type));
  },

  componentWillUnmount: function(){
    this.unsubscribe();
  },

  _onPluginsChange (data) {
    if (data.data.length !== 0) {
      let type = data.data[0].type;
      let plugins = this.state.plugins;

      // apply this structure: plugins[query]: [...] ; plugins[storage]: [...] ; ...
      plugins[type] = data.data;

      this.setState({
        plugins: plugins
      });
    }

    // decrease loading plugins by one unit. When all the plugins are loaded, the status will be "done"
    this.setState(function(previousState, currentProps) {
      let currentlyLoading = previousState.currentlyLoading - 1;
      let status = previousState.status;

      if (currentlyLoading === 0) {
        status = "done";
      }

      return {
        currentlyLoading: currentlyLoading,
        status: status
      };
    });
  },

  render () {
    if(this.state.status === "loading"){
      return (<div className="loader-inner ball-pulse">
        <div/><div/><div/>
        </div>);
    }

    let collapseId = 1;
    const ignoreFieldList = ["name", "type"];
    const pluginPanels = Object.keys(this.state.plugins).sort().map(type => {
      return (
        <div className="col-md-3 col-sm-6">
          <p>{type.charAt(0).toUpperCase() + type.slice(1)}</p>
          {this.state.plugins[type].map(plugin => (
            <div className="panel panel-default">
              <div className="panel-heading">
                <h4 className="panel-title">
                  <a data-toggle="collapse" data-parent="#accordion"
                     href={"#collapse" + collapseId}>{plugin.name}</a>
                </h4>
              </div>
              <div id={"collapse" + collapseId++} className="panel-collapse collapse">
                <div className="panel-body">
                  {
                    Object.keys(plugin).filter(field => ignoreFieldList.indexOf(field) < 0).map(field => (
                      <p><b>{field}:</b> {
                        plugin[field] !== null ? plugin[field].toString() : "undefined"
                      }</p>
                    ))
                  }
                </div>
              </div>
            </div>
          ))}
        </div>
      )
    });

    return (
      <div className="panel panel-primary topMargin">
        <div className="panel-heading">
          <h3 className="panel-title">Plugins</h3>
        </div>
        <div className="panel-body">
          <div className="row">
            {pluginPanels}
          </div>
        </div>
      </div>
    );
  }
});

export {PluginsView};
