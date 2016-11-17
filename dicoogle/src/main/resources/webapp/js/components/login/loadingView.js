import React, {PropTypes} from 'react';
import {UserActions} from "../../actions/userActions";
import {UserStore} from "../../stores/userStore";

const LoadingView = React.createClass({
  contextTypes: {
    router: PropTypes.object.isRequired
  },
  getInitialState: function() {
    return {data: {},
    status: "loading"};
  },
  componentDidMount: function(){
    //LoggerActions.get();
    UserActions.isLoggedIn();
  },
  componentWillMount: function() {
    this.unsubscribe = UserStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  _onChange: function(data){
    const {router} = this.context;
    console.log(data);
    if(data.isLoggedIn) {
      router.replace('/search');
    }
    else if(data.isLoggedIn === false){
      router.replace('/login');
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
