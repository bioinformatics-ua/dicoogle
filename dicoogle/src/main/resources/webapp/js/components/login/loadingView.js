import React, {PropTypes} from 'react';

const LoadingView = React.createClass({
  contextTypes: {
    router: PropTypes.object.isRequired
  },
  getInitialState: function() {
    return {data: {},
    status: "loading"};
  },
  render: function() {
    return (
      <div id="loginwrapper" style={{position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', zIndex: 10000}}>
        <div className="loginbody">
          <div>
            <img className="loginlogo" src="/assets/logo.png"></img>
          </div>
          <div className="loginloader">
            <div className="loader-inner line-spin-fade-loader">
              <div/>
              <div/>
              <div/>
              <div/>
              <div/>
              <div/>
              <div/>
              <div/>
            </div>
          </div>
        </div>
      </div>
    );
  }
});


export default LoadingView;
