import React from 'react';
import {Router} from 'react-router';
import {UserActions} from "../../actions/userActions";
import {UserStore} from "../../stores/userStore";

const LoadingView = React.createClass({
  mixins: [ Router.History ],
  getInitialState: function() {
    return {data: {},
    status: "loading"};
  },
  componentDidMount: function(){
    //LoggerActions.get();
    UserActions.isLoggedIn();
  },
  componentDidUpdate: function() {
  },
  componentWillMount: function() {
    UserStore.listen(this._onChange);

  },
  _onChange: function(data){
    console.log(data);
    if(data.isLoggedIn && this.isMounted())
    {
      this.history.replaceState(null, '/search');
    }
    else if(data.isLoggedIn === false){
      this.history.replaceState(null, '/login');
    }
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
