import React from 'react';
import ServiceForm from './serviceForm.jsx';
import ServiceAction from '../../actions/servicesAction';
import ServicesStore from '../../stores/servicesStore';
import QueryAdvancedOptionsModal from './queryadvoptions';
import Webcore from 'dicoogle-webcore';
import PluginView from '../plugin/pluginView';

const ServicesView = React.createClass({

    getInitialState () {
        return {
          storageRunning: false,
          storagePort: '0',
          storageDirtyPort: false,
          storageAutostart: false,
          queryRunning: false,
          queryPort: '0',
          queryDirtyPort: false,
          queryAutostart: false,
          status: "loading",
          storageLoading: true,
          queryLoading: true,
          showingAdvanced: false,
          plugins: []
        };
    },

    componentWillMount () {
      ServicesStore.listen(this._onChange);
      Webcore.fetchPlugins('settings', (packages) => {
        Webcore.fetchModules(packages);
        this.setState({plugins: packages.map(pkg => ({
          name: pkg.name,
          caption: pkg.dicoogle.caption || pkg.name
        }))});
      });
    },

    componentDidMount () {
      ServiceAction.getStorage();
      ServiceAction.getQuery();
    },

    _onChange (data) {
      console.log(data);
      if(this.isMounted()) {
        this.setState({
        storageRunning: data.storageRunning,
        storagePort: data.storagePort,
        storageAutostart: data.storageAutostart,
        queryRunning: data.queryRunning,
        queryPort: data.queryPort,
        queryAutostart: data.queryAutostart,
        status: "done",
        storageLoading: false,
        queryLoading: false
        });

        console.log("Service data update: ", data);
      }
    },

    render () {
      if(this.state.status === "loading"){
        return (<div className="loader-inner ball-pulse">
          <div/><div/><div/>
          </div>);
      }
      const pluginElements = this.state.plugins.map(p =>(
        <li key={'plugin/' + p.name} className="list-group-item list-group-item-management">
          <div>
            <div className="row">
              <div className="col-xs-4">
                <p>{p.caption}</p>
              </div>
              <div className="col-xs-8">
                <PluginView plugin={p.name} slotId="settings" />
              </div>
            </div>
          </div>
        </li>
      ));

      const extraQRSettings = (
        <button type="button" className="btn btn-default" style={{marginTop: 20, float: 'right'}} onClick={this.showAdvanced}>
          <span className="glyphicon glyphicon-cog" />
        </button>);

      return (
      <div className="panel panel-primary topMargin">
        <div className="panel-heading">
          <h3 className="panel-title">Services</h3>
        </div>
        <div className="panel-body">
          <ul className="list-group">
            <li key="storage" className="list-group-item list-group-item-management">
              <ServiceForm caption="Storage" running={this.state.storageRunning} autostart={this.state.storageAutostart}
                           dirtyPort={this.state.storageDirtyPort} port={this.state.storagePort}
                           onhold={this.state.storageLoading} extraSettings={null}
                           onStartService={this.startStorage} onStopService={this.stopStorage}
                           onChangePort={this.handleStoragePortChange}
                           onToggleAutostart={this.handleToggleStorageAutostart}
                           onSubmitPort={this.handleSubmitStoragePort} />
            </li>
            <li key="query" className="list-group-item list-group-item-management">
              <ServiceForm caption="Query Retrieve" running={this.state.queryRunning} autostart={this.state.queryAutostart}
                           dirtyPort={this.state.queryDirtyPort} port={this.state.queryPort}
                           onhold={this.state.queryLoading} extraSettings={extraQRSettings}
                           onStartService={this.startQuery} onStopService={this.stopQuery}
                           onChangePort={this.handleQueryPortChange}
                           onToggleAutostart={this.handleToggleQueryAutostart}
                           onSubmitPort={this.handleSubmitQueryPort} />
            </li>
            {pluginElements}
          </ul>
          <QueryAdvancedOptionsModal show={this.state.showingAdvanced} onHide={this.onHideAdvanced} />
        </div>
      </div>
      );
    },
    showAdvanced() {
      this.setState({showingAdvanced: true});
      ServiceAction.getQuerySettings();
    },
    onHideAdvanced() {
      this.setState({showingAdvanced: false});
    },
    handleQueryPortChange(portNumber) {
      this.setState({
        queryPort: portNumber,
        queryDirtyPort: true
      });
    },
    handleStoragePortChange (portNumber) {
      this.setState({
        storagePort: portNumber,
        storageDirtyPort: true
      });
    },
    handleStorageRunning(enable) {
      if (enable) {
        this.startStorage();
      } else {
        this.stopStorage();
      }
    },
    handleQueryRunning(enable) {
      if (enable) {
        this.startQuery();
      } else {
        this.stopQuery();
      }
    },
    startStorage () {
      ServiceAction.setStorage(true);
    },
    stopStorage () {
      ServiceAction.setStorage(false);
    },
    handleToggleStorageAutostart () {
      const newAutostart = !this.state.storageAutostart;
      this.setState({storageAutostart: newAutostart, storageLoading: true});
      ServiceAction.setStorageAutostart(newAutostart);
    },
    handleToggleQueryAutostart () {
      const newAutostart = !this.state.queryAutostart;
      this.setState({queryAutostart: newAutostart, queryLoading: true});
      ServiceAction.setQueryAutostart(newAutostart);
    },
    handleSubmitStoragePort(port) {
      this.setState({storageDirtyPort: false, storageLoading: true});
      ServiceAction.setStoragePort(port);
    },
    handleSubmitQueryPort(port) {
      this.setState({queryDirtyPort: false, queryLoading: true});
      ServiceAction.setQueryPort(port);
    },
    startQuery () {
      ServiceAction.setQuery(true);
    },
    stopQuery () {
      ServiceAction.setQuery(false);
    }
});

export {ServicesView};
