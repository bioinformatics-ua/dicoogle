import React from "react";
import createReactClass from "create-react-class";
import { TransferOptionsView } from "../management/transferOptionsView";
import { ServicesAndPluginsView } from "../management/servicesAndPluginsView";
import { LoggerView } from "../management/loggerView";
import { IndexerView } from "../management/indexerView";
import { StorageView } from "../management/storageView";
import { ToastView } from "../mixins/toastView";

const ManagementView = createReactClass({
  getInitialState: function() {
    return {
      selectedtab: 0,
      showToast: false,
      toastType: "default",
      toastMessage: {}
    };
  },

  showToastMessage: function(toastType, toastMessage) {
    this.setState(
      {
        showToast: true,
        toastType,
        toastMessage
      },
      () => setTimeout(() => this.setState({ showToast: false }), 3000)
    );
  },

  render: function() {
    var views = [
      <IndexerView showToastMessage={this.showToastMessage} />,
      <TransferOptionsView showToastMessage={this.showToastMessage} />,
      <ServicesAndPluginsView showToastMessage={this.showToastMessage} />,
      <StorageView showToastMessage={this.showToastMessage} />,
      <LoggerView />
    ];

    const { showToast, toastType, toastMessage } = this.state;

    return (
      <div className="container-fluid content">
        <ul className="nav nav-pills">
          <li className="active" role="presentation">
            <a
              href="#indexer"
              data-toggle="tab"
              onClick={this.onTabClicked.bind(this, 0)}
            >
              Index Options
            </a>
          </li>
          <li role="presentation">
            <a
              href="#transfer"
              data-toggle="tab"
              onClick={this.onTabClicked.bind(this, 1)}
            >
              Transfer Options
            </a>
          </li>
          <li role="presentation">
            <a
              href="#services"
              data-toggle="tab"
              onClick={this.onTabClicked.bind(this, 2)}
            >
              Services and Plugins
            </a>
          </li>
          <li role="presentation">
            <a
              href="#storage"
              data-toggle="tab"
              onClick={this.onTabClicked.bind(this, 3)}
            >
              Storage Servers
            </a>
          </li>
          <li role="presentation">
            <a
              href="#logs"
              data-toggle="tab"
              onClick={this.onTabClicked.bind(this, 4)}
            >
              Logs
            </a>
          </li>
        </ul>
        <div id="my-tab-content" className="tab-content">
          {views[this.state.selectedtab]}
        </div>

        <ToastView
          show={showToast}
          message={toastMessage}
          toastType={toastType}
        />
      </div>
    );
  },
  onTabClicked: function(index) {
    this.setState({ selectedtab: index });
  }
});

export { ManagementView };
