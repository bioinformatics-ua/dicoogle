import React from "react";
import { TransferOptionsView } from "../management/transferOptionsView";
import { ServicesAndPluginsView } from "../management/servicesAndPluginsView";
import { LoggerView } from "../management/loggerView";
import { IndexerView } from "../management/indexerView";
import { StorageView } from "../management/storageView";

function getIndex(tab) {
  const states = ["index", "transfer", "services", "storage", "logs"];
  var index = 0;
  if (tab) {
    index = states.findIndex(s => s === tab);
    index = index === -1 ? 0 : index;
  }
  return index;
}

const ManagementView = React.createClass({
  getInitialState: function() {
    const index = getIndex(getIndex(this.props.params.tab));
    return { selectedtab: index };
  },
  componentWillReceiveProps(nextProps) {
    const index = getIndex(nextProps.params.tab);
    this.changeView(index, false);
  },
  render: function() {
    const index = getIndex(this.props.params.tab);

    var views = [
      <IndexerView />,
      <TransferOptionsView />,
      <ServicesAndPluginsView />,
      <StorageView />,
      <LoggerView />
    ];

    const tabs = [
      'Index Options',
      'Transfer Options',
      'Services and Plugins',
      'Storage Servers',
      'Logs'
    ]
    return (
      <div className="container-fluid content">
        <ul className="nav nav-pills">
          {tabs.map((tab, idx) => (
            <li className={index === idx ? "active" : ""} role="presentation">
              <a data-toggle="tab" onClick={this.changeView.bind(this, idx)}>
                {tab}
              </a>
            </li>
          ))}
        </ul>
        <div id="my-tab-content" className="tab-content">
          {views[this.state.selectedtab]}
        </div>
      </div>
    );
  },
  changeView: function(index, reRoute = true) {
    const states = ["index", "transfer", "services", "storage", "logs"];
    this.setState({ selectedtab: index });

    if (reRoute) {
      this.props.router.push(`/management/${states[index]}`);
    }
  }
});

export { ManagementView };
