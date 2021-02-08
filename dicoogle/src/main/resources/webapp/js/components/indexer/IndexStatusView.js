import React from "react";
import createReactClass from "create-react-class";
import { IndexStatusActions } from "../../actions/indexStatusAction";
import { IndexStatusStore } from "../../stores/indexStatusStore";
import * as PluginActions from "../../actions/pluginActions";
import { ProvidersActions } from "../../actions/providersActions";
import { ProvidersStore } from "../../stores/providersStore";
import TaskStatus from "./TaskStatus.jsx";

import Autosuggest from "react-autosuggest";
import Select from "react-select";
import PluginStore from "../../stores/pluginStore";

var refreshIntervalId;
const IndexStatusView = createReactClass({
  getInitialState: function() {
    return {
      data: {},
      status: "loading",
      providers: [],
      selectedProviders: [],
      schemeList: [],
      suggestions: [],
      directoryInput: ""
    };
  },
  componentDidMount: function() {
    IndexStatusActions.get();

    //Start refresh interval
    refreshIntervalId = setInterval(this.update, 3000);
    //$("#consolediv").scrollTop($("#consolediv")[0].scrollHeight);
    ProvidersActions.get();

    PluginActions.get("storage");
  },
  componentDidUpdate: function() {
    console.log("indexstatus update");
    //if(this.state.data.count !=0)
    //{
    //setInterval(IndexStatusActions.get(), 5000);
    //}
  },
  componentWillUnmount: function() {
    console.log("IndexStatusView unmounted");
    //Stop refresh interval
    clearInterval(refreshIntervalId);

    this.unsubscribeProviders();
    this.unsubscribeIndexStatus();
    this.unsubscribePlugin();

    //this.unsubscribe();
  },
  update: function() {
    IndexStatusActions.get();
  },
  componentWillMount: function() {
    // Subscribe to the store.
    console.log("subscribe listener");
    this.unsubscribeProviders = ProvidersStore.listen(this._onProvidersChange);
    this.unsubscribeIndexStatus = IndexStatusStore.listen(this._onChange);
    this.unsubscribePlugin = PluginStore.listen(this._onStoragePluginsChange);
  },
  _onChange: function(data) {
    this.setState({ data: data.data, status: "done" });
    //this.unsubscribe = IndexStatusStore.listen(this._onChange);
  },
  render: function() {
    if (this.state.status === "loading") {
      return (
        <div className="loader-inner ball-pulse">
          <div />
          <div />
          <div />
        </div>
      );
    }

    let items;
    if (this.state.data.tasks.length === 0) {
      items = <div>No tasks</div>;
    } else {
      items = this.state.data.tasks.map(item => (
        <TaskStatus
          key={item.taskUid}
          index={item.taskUid}
          item={item}
          onCloseStopClicked={this.onCloseStopClicked.bind(
            this,
            item.taskUid,
            item.complete,
            item.canceled
          )}
        />
      ));
    }

    let providersList = this.state.providers.map(item => ({
      value: item,
      label: item
    }));

    const inputProps = {
      placeholder: "e.g.: file:/path/to/directory",
      value: this.state.directoryInput,
      onChange: (event, { newValue, method }) => {
        this.setState({
          directoryInput: newValue
        });
      }
    };

    return (
      <div>
        <div className="panel panel-primary topMargin">
          <div className="panel-heading">
            <h3 className="panel-title">Start indexing</h3>
          </div>
          <div className="panel-body">
            <div className="row">
              <div className="col-xs-6 col-sm-2">Index directory:</div>
              <div className="col-xs-6 col-sm-10">
                <Autosuggest
                  suggestions={this.state.suggestions}
                  onSuggestionsFetchRequested={
                    this._onSuggestionsFetchRequested
                  }
                  onSuggestionsClearRequested={
                    this._onSuggestionsClearRequested
                  }
                  shouldRenderSuggestions={() => true}
                  getSuggestionValue={suggestion => suggestion}
                  renderSuggestion={suggestion => <div> {suggestion} </div>}
                  inputProps={inputProps}
                />
              </div>
            </div>
            <div className="row" style={{ marginTop: 15 }}>
              <div className="col-xs-6 col-sm-2">Index providers:</div>
              <div className="col-xs-6 col-sm-10">
                <Select
                  multi
                  id="providersList"
                  name="form-field-name"
                  value={this.state.selectedProviders}
                  options={providersList}
                  placeholder="All Providers"
                  onChange={this.handleProviderSelect}
                />
              </div>
            </div>
            <button className="btn btn_dicoogle" onClick={this.onStartClicked}>
              Start
            </button>
          </div>
        </div>
        <div className="panel panel-primary topMargin">
          <div className="panel-heading">
            <h3 className="panel-title">
              {this.state.data.count === 0
                ? "No tasks currently running"
                : "Indexing Status (" + this.state.data.count + " running)"}
            </h3>
          </div>
          <div className="panel-body">{items}</div>
        </div>
      </div>
    );
  },
  onStartClicked: function() {
    IndexStatusActions.start(
      this.state.directoryInput,
      this.state.selectedProviders
    );
  },

  onCloseStopClicked: function(uid, isComplete, isCanceled) {
    if (isComplete || isCanceled) {
      IndexStatusActions.close(uid);
    } else {
      IndexStatusActions.stop(uid);
    }
  },
  _onProvidersChange: function(data) {
    this.setState({ providers: data.data });
  },
  _onStoragePluginsChange: function(data) {
    let schemeList = data.data.map(plugin => plugin.scheme);

    // filter duplicates
    schemeList = schemeList.filter(function(elem, index, self) {
      return index === self.indexOf(elem);
    });

    this.setState({
      schemeList: schemeList
    });
  },
  _onSuggestionsFetchRequested: function({ value }) {
    const formattedSchemeList = this.state.schemeList.map(
      scheme => scheme + (scheme.includes(":") ? "" : ":")
    );

    function getSuggestions(value) {
      // https://developer.mozilla.org/en/docs/Web/JavaScript/Guide/Regular_Expressions#Using_Special_Characters
      const escapedValue = value.trim().replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

      if (escapedValue === "") {
        return formattedSchemeList;
      }

      const regex = new RegExp("^" + escapedValue, "i");
      return formattedSchemeList.filter(scheme => regex.test(scheme));
    }

    this.setState({
      suggestions: getSuggestions(value, formattedSchemeList)
    });
  },
  _onSuggestionsClearRequested: function() {
    this.setState({
      suggestions: []
    });
  },
  handleProviderSelect(providers) {
    this.setState({
      selectedProviders: providers.map(e => e.value)
    });
  }
});

export { IndexStatusView };
