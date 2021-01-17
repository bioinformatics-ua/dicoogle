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
    this.drawStatusCircle = this.drawStatusCircle.bind(this);
    this.captureStatusCanvas = this.captureStatusCanvas.bind(this);
  }

  componentDidUpdate() {
    this.drawStatusCircle();
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

  captureStatusCanvas(input) {
    this._canvas = input;
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-4">
          <p>{this.props.caption}</p>
          <canvas ref={this.captureStatusCanvas} width={30} height={30} />
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

  drawStatusCircle() {
    const context = this._canvas.getContext("2d");
    const centerX = this._canvas.width / 2;
    const centerY = this._canvas.height / 2;
    const radius = 13;

    context.beginPath();
    context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
    context.fillStyle = this.props.running ? "green" : "red";
    context.fill();
    context.lineWidth = 1;
    context.strokeStyle = "#003300";
    context.stroke();
  }
}
