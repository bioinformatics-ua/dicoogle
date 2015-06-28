var React = require('react');
var Router = require('react-router');

import {UserActions} from "../../actions/userActions";
import {UserStore} from "../../stores/userStore";

var LoginView = React.createClass({
  mixins : [Router.Navigation],
  getInitialState: function() {
    return {data: {},
    status: "loading",
    failed: false};
  },
  componentDidMount: function(){
    //LoggerActions.get();
    document.getElementById('container').style.display = 'none';
    this.enableEnterKey();
  },
  componentDidUpdate:function(){
    this.enableEnterKey();
  },
  componentWillMount: function() {
    UserStore.listen(this._onChange);

  },
  _onChange: function(data){
    console.log(data);
    if(data.failed == true)
    {
      this.setState({failed: true});
      return;
    }

    if(data.isLoggedIn && this.isMounted())
    {
      React.unmountComponentAtNode(document.getElementById('login_container'));
      this.replaceWith('/search');
    }
  },
  enableEnterKey() {
    var self = this;
    var fh = function(e) {
      if (e.keyCode === 13) {
        self.onLoginClick();
      }
    };
    jQuery("#username").keypress(fh);
    jQuery("#password").keypress(fh);
  },

  render: function() {
    return (
      <div id="loginwrapper">
        <div className="loginbody">


          <section className="container row-fluid loginbox logincontainer">

            <img className="loginlogo" src="assets/logo.png" alt="Smiley face"/>


            <div text-align="center" >

              <h4 style={{textAlign: 'center'}}>
                Medical Imaging Repositories using Indexing System and P2P mechanisms
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
            <div style={{width: '100%', textAlign: 'center'}} classname="footercontainer">
              <div className="row" style={{display: 'flex', 'align-items':'center',  width: '100%', 'margin-left': '10px'}}>
                <div className="col-xs-3">
                <a href="http://bioinformatics.ua.pt"><img src="http://www.dicoogle.com/wp-content/themes/dicoogle/images/logobio.png" style={{maxWidth: '100%'}} /></a>

                </div>
                <div className="col-xs-3">
                    <a href="http://bmd-software.com/"><img src="http://www.bmd-software.com/wp-content/themes/BMD-code/images/logo.png"  style={{maxWidth: '100%'}} /></a>
                </div>
                <div className="col-xs-3">
                  <a href="http://www.ieeta.pt/"><img src="http://www.dicoogle.com/wp-content/themes/dicoogle/images/logo-ieeta.png" style={{maxWidth: '100%'}} /></a>
                </div>
                <div className="col-xs-3">
                    <a href="http://www.ua.pt/"><img src="http://www.dicoogle.com/wp-content/themes/dicoogle/images/logo-ua.png"   style={{maxWidth: '100%'}}/></a>
                </div>




              </div>
              <div style={{display: 'inline-block'}}>
                <a><img src="http://www.dicoogle.com/wp-content/themes/dicoogle/images/logoFCT.png" style={{maxHeight: 60, maxWidth: '100%', margin:5}} /></a>
              </div>

            </div>
          </footer>

        </div>
      </div>
    );

  },

  onLoginClick : function(){
    var user = document.getElementById("username").value;
    var pass = document.getElementById("password").value;
    //console.log("login clicked", user ,pass );
    UserActions.login(user,pass);
  }

});


export {
  LoginView
}
