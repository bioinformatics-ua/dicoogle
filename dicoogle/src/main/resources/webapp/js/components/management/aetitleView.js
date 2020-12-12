import React from "react";
import createReactClass from "create-react-class";

import AETitleForm from "./aetitleForm";
import * as AETitleActions from "../../actions/aetitleActions";
import AETitleStore from "../../stores/aetitleStore";

const AETitleView = createReactClass({
  getInitialState() {
    return {
      aetitleText: "",
      dirtyValue: false, // aetitle value has unsaved changes
      status: "loading"
    };
  },

  componentWillMount() {
    this.unsubscribe = AETitleStore.listen(this._onChange);
  },

  componentWillUnmount() {
    this.unsubscribe();
  },

  componentDidMount() {
    AETitleActions.getAETitle();
  },

  _onChange(data) {
    if (data.success && this.state.status === "done") {
      this.props.showToastMessage("success", { title: "Saved" });
    } else if (data.success) {
      this.setState({
        aetitleText: data.message,
        status: "done"
      });
    } else {
      this.setState({
        dirtyValue: true
      });

      this.props.showToastMessage("error", {
        title: "Error",
        body: data.message
      });
    }
  },

  render() {
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
          <h3 className="panel-title">AETitle</h3>
        </div>

        <div className="panel-body">
          <AETitleForm
            aetitleText={this.state.aetitleText}
            onChangeAETitle={this.handleAETitleChange}
            onSubmitAETitle={this.handleSubmitAETitle}
            dirtyValue={this.state.dirtyValue}
          />
        </div>
      </div>
    );
  },

  handleAETitleChange(aetitle) {
    this.setState({
      aetitleText: aetitle,
      dirtyValue: true
    });
  },

  handleSubmitAETitle() {
    this.setState({
      dirtyValue: false
    });

    AETitleActions.setAETitle(this.state.aetitleText);
  }
});

export { AETitleView };
