import React from 'react';
import {UserActions} from "../../actions/userActions";
import {UserStore} from "../../stores/userStore";
import $ from 'jquery';

const LoginView = React.createClass({
  contextTypes: {
    router: React.PropTypes.object.isRequired
  },
  getInitialState: function() {
    return {data: {},
    status: "loading",
    failed: false};
  },
  componentDidMount: function(){
    //LoggerActions.get();
    this.enableEnterKey();
  },
  componentDidUpdate: function(){
    this.enableEnterKey();
  },
  componentWillMount: function() {
    this.unsubscribe = UserStore.listen(this._onChange);
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  _onChange: function(data){
    console.log(data);
    const {router} = this.context;
    if(data.failed === true)
    {
      this.setState({failed: true});
      return;
    }

    if(data.isLoggedIn) {
      router.replace('/search');
      //React.unmountComponentAtNode(document.getElementById('login_container'));
    }
  },
  enableEnterKey() {
    var self = this;
    var fh = function(e) {
      if (e.keyCode === 13) {
        self.onLoginClick();
      }
    };
    $("#username").keypress(fh);
    $("#password").keypress(fh);
  },

  render: function() {
    return (
      <div id="loginwrapper" style={{position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', zIndex: 10000}}>
        <div className="loginbody">

          <section className="container row-fluid loginbox logincontainer">

            <img className="loginlogo" src="assets/logo.png" alt="Smiley face"/>

            <div text-align="center" >

              <h4 style={{textAlign: 'center'}}>
                Improve your knowledge from your medical imaging repository.
              </h4>

            </div>

            <div className="loginA">

              <form className="form-horizontal">

                <p className="loginTextA">Sign In</p>
                <input ref="user" type="text" id="username" name="username" placeholder="Username" className="loginInputUsername form-control"/>
                <input ref="pass" type="password" id="password" name="password" placeholder="Password" className="loginInputPassword form-control" />
                  {this.state.failed ? (<p style={{color: 'red'}}> Login Failed. Please try again. </p>) : ''}
                <button type="button" className="btn submit btn_dicoogle" onClick={this.onLoginClick}>Login</button>
              </form>

            </div>

          </section>

          <footer id="footer">
            <div style={{width: '100%', textAlign: 'center'}} className="footercontainer">
              <div style={{display: 'inline-block', width: '100%'}}>
                <a href="http://bioinformatics.ua.pt"><img src="assets/logos/logobio.png" style={{height: 40, margin: 5}} /></a>
                <a href="http://bmd-software.com/"><img src="assets/logos/logo.png" style={{height: 40, padding: 5, margin: 5}} /></a>
                <a href="http://www.ieeta.pt/"><img src="assets/logos/logo-ieeta.png" style={{height: 60, margin: 5}} /></a>
                <a href="http://www.ua.pt/"><img src="assets/logos/logo-ua.png" style={{height: 60, margin: 5}} /></a>
              </div>
              <div style={{display: 'inline-block'}}>
                <a><img src="assets/logos/logoFCT.png" style={{height: 30, margin: 5}} /></a>
              </div>

            </div>
          </footer>

        </div>
      </div>
    );
  },

  onLoginClick: function(){
    const user = document.getElementById("username").value;
    const pass = document.getElementById("password").value;
    //console.log("login clicked", user ,pass );
    UserActions.login(user, pass);
  }

});


export default LoginView;
