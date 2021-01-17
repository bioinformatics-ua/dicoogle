import React from "react";
import * as PropTypes from "prop-types";

class LoadingView extends React.Component {
  static get contextTypes() {
    return {
      router: PropTypes.object.isRequired
    };
  }

  constructor(props) {
    super(props);
    this.state = {
      data: {},
      status: "loading"
    };
  }

  render() {
    return (
      <div
        id="loginwrapper"
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          zIndex: 10000
        }}
      >
        <div className="loginbody">
          <div>
            <img className="loginlogo" src="/assets/logo.png" />
          </div>
          <div className="loginloader">
            <div className="loader-inner line-spin-fade-loader">
              <div />
              <div />
              <div />
              <div />
              <div />
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

export default LoadingView;
