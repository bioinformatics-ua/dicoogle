import React from "react";
import * as PropTypes from "prop-types";
import { FormGroup, FormControl } from "react-bootstrap";

export default class ServiceForm extends React.Component {
  static get propTypes() {
    return {
      caption: PropTypes.string.isRequired,
      running: PropTypes.bool.isRequired,
      dirtyPort: PropTypes.bool.isRequired, // port has unsaved changes
      onhold: PropTypes.bool,
      port: PropTypes.oneOfType([
        PropTypes.string.isRequired,
        PropTypes.number.isRequired
      ]).isRequired,
      extraSettings: PropTypes.node,
      onStartService: PropTypes.func.isRequired,
      onStopService: PropTypes.func.isRequired,
      onChangePort: PropTypes.func.isRequired,
      onToggleAutostart: PropTypes.func.isRequired,
      onSubmitPort: PropTypes.func.isRequired
    };
  }

  constructor(props) {
    super(props);
    this.handlePortChange = this.handlePortChange.bind(this);
    this.handlePortKeyPress = this.handlePortKeyPress.bind(this);
  }

  isPortValid() {
    return (
      /\d+/.test(this.props.port) &&
      +this.props.port > 0 &&
      +this.props.port < 65536
    );
  }

  handlePortChange(e) {
    this.props.onChangePort(e.target.value);
  }

  handlePortKeyPress(e) {
    if (e.keyCode === 13) {
      if (this.isPortValid()) {
        this.props.onSubmitPort(this.props.port);
      }
    }
  }

  statusStyle() {
    return {
      display: "block",
      width: "28px",
      height: "28px",
      borderRadius: "50%",
      border: "1px solid #333",
      margin: "4px",
      backgroundColor: this.props.running ? "#0a0" : "red",
    };
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-4">
          <p>{this.props.caption}</p>
          <div style={this.statusStyle()} />
        </div>
        <div className="col-xs-4">
          <div className="data-table">
            <div className="inline_block">Port</div>
            <div className="inline_block" style={{ marginLeft: "1em" }}>
              <FormGroup
                validationState={this.isPortValid() ? "success" : "error"}
              >
                <FormControl
                  type="text"
                  value={this.props.port}
                  placeholder="Enter a valid port"
                  disabled={this.props.disabledPort && "disabled"}
                  onChange={this.handlePortChange}
                  onKeyDown={this.handlePortKeyPress}
                />
                {this.props.dirtyPort && <FormControl.Feedback />}
              </FormGroup>
            </div>
            <div className="checkbox">
              <label>
                <input
                  type="checkbox"
                  checked={this.props.autostart}
                  onChange={this.props.onToggleAutostart}
                  disabled={this.props.disabledAutostart && "disabled"}
                />{" "}
                Auto Start
              </label>
            </div>
          </div>
        </div>
        <div className="col-xs-4">
          <div className="data-table">
            <div className="inline_block">
              {this.props.running ? (
                <button
                  type="button"
                  className="btn btn-danger"
                  style={{ marginTop: 20 }}
                  onClick={this.props.onStopService}
                >
                  Stop
                </button>
              ) : (
                <button
                  type="button"
                  className="btn btn-success"
                  style={{ marginTop: 20 }}
                  onClick={this.props.onStartService}
                >
                  Start
                </button>
              )}
            </div>
            {this.props.extraSettings}
            <div
              className="loader-inner ball-pulse"
              style={{ visibility: this.props.onhold ? "visible" : "hidden" }}
            >
              <div />
              <div />
              <div />
            </div>
          </div>
        </div>
      </div>
    );
  }
}
