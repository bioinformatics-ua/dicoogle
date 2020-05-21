import React from "react";
import { LoggerActions } from "../../actions/loggerActions";
import { LoggerStore } from "../../stores/loggerStore";

const LoggerView = React.createClass({
  getInitialState: function() {
    return {
      data: {},
      status: "loading"
    };
  },
  componentDidMount: function() {
    LoggerActions.get();
  },
  componentDidUpdate: function() {
    console.log("logger update");
    let consoleDiv = document.getElementById("consolediv");
    consoleDiv.scrollTo(0, consoleDiv.scrollHeight);
  },
  componentWillMount: function() {
    // Subscribe to the store.
    console.log("subscribe listener");
    this.unsubscribe = LoggerStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  _onChange: function(data) {
    this.setState({ data: data.data, status: "done" });
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
    return (
      <div className="panel panel-primary topMargin">
        <div className="panel-heading">
          <h3 className="panel-title">Server Log</h3>
        </div>
        <div id="consolediv" className="panel-body scrolldiv">
          {this.state.data}
        </div>
      </div>
    );
  }
});

export { LoggerView };
