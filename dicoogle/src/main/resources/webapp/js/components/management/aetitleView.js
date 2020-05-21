import React from "react";
import AETitleForm from "./aetitleForm";
import * as AETitleActions from "../../actions/aetitleActions";
import AETitleStore from "../../stores/aetitleStore";
import $ from "jquery";

const AETitleView = React.createClass({
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
    let toastMessage = data.success ? "Saved." : data.message;

    if (this.state.status === "done") {
      $(".toast")
        .stop()
        .text(toastMessage)
        .fadeIn(400)
        .delay(3000)
        .fadeOut(400); // fade out after 3 seconds
    }

    if (!data.success) {
      this.setState({
        dirtyValue: true
      });
    } else {
      this.setState({
        aetitleText: data.message,
        status: "done"
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
        <div className="toast">Saved</div>
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
