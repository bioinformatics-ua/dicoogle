import React from 'react';
import * as UserActions from "../../actions/userActions";
import UserStore from "../../stores/userStore";

const LoginView = React.createClass({
  contextTypes: {
    router: React.PropTypes.object.isRequired
  },
  getInitialState: function() {
    return {data: {},
    status: "loading",
    failed: false,
    username: '',
    password: ''};
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
    if(!data.success)
    {
      this.setState({failed: true});
      return;
    }

    if(data.isLoggedIn) {
      router.replace('/search');
    }
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
                <input ref="user" type="text" name="username" placeholder="Username" className="loginInputUsername form-control"
                       value={this.state.username} onChange={this.handleUsernameChange} onKeyDown={this.handleKeyDown} />
                <input ref="pass" type="password" name="password" placeholder="Password" className="loginInputPassword form-control"
                       value={this.state.password} onChange={this.handlePasswordChange} onKeyDown={this.handleKeyDown} />
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

  handleKeyDown(e) {
    if (e.keyCode === 13) {
      this.onLoginClick();
    }
  },

  handleUsernameChange(e) {
    this.setState({username: e.target.value});
  },

  handlePasswordChange(e) {
    this.setState({password: e.target.value});
  },

  onLoginClick() {
    const {username, password} = this.state;
    UserActions.login(username, password);
  }

});


export default LoginView;
