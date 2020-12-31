import React, { PropTypes } from "react";
import { FormGroup, FormControl } from "react-bootstrap";

export default class AETitleForm extends React.Component {
  static get propTypes() {
    return {
      aetitleText: PropTypes.string,
      dirtyValue: PropTypes.bool.isRequired, // aetitle value has unsaved changes
      onChangeAETitle: PropTypes.func.isRequired,
      onSubmitAETitle: PropTypes.func.isRequired
    };
  }

  constructor(props) {
    super(props);
    this.handleAETitleChange = this.handleAETitleChange.bind(this);
    this.handleAETitleKeyPress = this.handleAETitleKeyPress.bind(this);
  }

  componentDidUpdate() {}

  isAETitleValid() {
    let regex = /^[ A-Za-z0-9_.+-]*$/;

    return regex.test(this.props.aetitleText);
  }

  handleAETitleChange(e) {
    this.props.onChangeAETitle(e.target.value);
  }

  handleAETitleKeyPress(e) {
    if (e.keyCode === 13) {
      if (this.isAETitleValid()) {
        this.props.onSubmitAETitle(this.props.aetitleText);
      }
    }
  }
  render() {
    return (
      <div className="row">
        <div className="col-xs-8">
          <div className="data-table">
            <FormGroup
              validationState={this.isAETitleValid() ? "success" : "error"}
            >
              <FormControl
                type="text"
                value={this.props.aetitleText}
                placeholder="Enter a valid AETitle"
                onChange={this.handleAETitleChange}
                onKeyDown={this.handleAETitleKeyPress}
              />
              {this.props.dirtyValue && <FormControl.Feedback />}
            </FormGroup>
          </div>
        </div>
        <div className="col-xs-4">
          <button
            type="button"
            className="btn btn-success"
            onClick={this.props.onSubmitAETitle}
          >
            Save
          </button>
          <div
            className="loader-inner ball-pulse"
            style={{ visibility: this.props.onhold ? "visible" : "hidden" }}
          />
        </div>
      </div>
    );
  }
}
