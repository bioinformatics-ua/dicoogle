import React, { PropTypes } from "react";
import { FormGroup, FormControl } from "react-bootstrap";

export default class AETitleForm extends React.Component {
  static get propTypes() {
    return {
      aetitleText: PropTypes.string.isRequired,
      dirtyValue: PropTypes.bool.isRequired,  // aetitle value has unsaved changes
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
    console.log(e.target.value);
    this.props.onChangeAETitle(e.target.value);
  }

  handleAETitleKeyPress(e) {
    console.log("handleAETitleKeyPress", e.target.value);
    if (e.keyCode === 13) {
      if (this.isAETitleValid()) {
        this.props.onSubmitAETitle(this.props.aetitleText);
      }
    }
  }
 render() {
    return (
      <div className="row">
        <div className="col-xs-4">
          <p>AETitle</p>
        </div>
        <div className="col-xs-4">
          <div className="data-table">
            <div className="inline_block" style={{ marginLeft: "1em" }}>
              <FormGroup
                validationState={this.isAETitleValid() ? "success" : "error"}
              >
                <FormControl
                  type="text"
                  value={this.props.aetitleText}
                  placeholder="Enter a valid AETitle"
                  disabled={this.props.disabledAETitle && "disabled"}
                  onChange={this.handleAETitleChange}
                  onKeyDown={this.handleAETitleKeyPress}
                />
                {this.props.dirtyValue && <FormControl.Feedback />}
              </FormGroup>
            </div>
          </div>
        </div>
        <div className="col-xs-4">
          <div className="data-table">
            <button
              type="button"
              className="btn btn-danger"
              style={{ marginTop: 20 }}
              onClick={this.props.onSubmitAETitle}
            >
              Change
            </button>
          </div>
          <div
            className="loader-inner ball-pulse"
            style={{ visibility: this.props.onhold ? "visible" : "hidden" }}
          ></div>
        </div>
      </div>
    );
  }
}
