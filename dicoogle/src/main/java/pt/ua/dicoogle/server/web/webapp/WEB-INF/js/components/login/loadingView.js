var React = require('react');
var Router = require('react-router');

import {UserActions} from "../../actions/userActions";
import {UserStore} from "../../stores/userStore";

var LoadingView = React.createClass({
  mixins : [Router.Navigation],
  getInitialState: function() {
    return {data: {},
    status: "loading"};
  },
  componentDidMount: function(){

    //LoggerActions.get();
    UserActions.isLoggedIn();
    document.getElementById('container').style.display = 'none';

  },
  componentDidUpdate:function(){

  },
  componentWillMount: function() {
    UserStore.listen(this._onChange);

  },
  _onChange: function(data){
    console.log(data);
    if(data.isLoggedIn && this.isMounted())
    {
      React.unmountComponentAtNode(document.getElementById('login_container'));
      this.replaceWith('/search');
    }
    else if(data.isLoggedIn == false){
      this.replaceWith('/login');
    }
  }
  ,
  render: function() {
    return (
      <div id="loginwrapper">
        <div className="loginbody">
        <div>
        <img className="loginlogo" src="/assets/logo.png"></img>
        </div>
    <div className="loginloader">

    <div className="loader-inner line-spin-fade-loader"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>
    </div>
    </div>
    </div>
    );



  },


});


export {
  LoadingView
}
