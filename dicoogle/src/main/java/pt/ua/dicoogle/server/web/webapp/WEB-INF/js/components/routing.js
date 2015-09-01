/*jshint esnext: true*/

/*
author: Frederico Silva<fredericosilva@ua.pt>
*/

var React = require('react');
var Router = require('react-router');
var Bootstrap = require('bootstrap');
var ReactBootstrap = require('react-bootstrap');

var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;
var Routes = Router.Routes;

var Link = Router.Link;
var NotFoundRoute = Router.NotFoundRoute;
var  RouteHandler = Router.RouteHandler;

var Nav = ReactBootstrap.Nav;

var Button = ReactBootstrap.Button;
var NavItemLink = ReactBootstrap.NavItemLink;
var ButtonLink = ReactBootstrap.ButtonLink;

var Navbar = ReactBootstrap.Navbar;
var NavItem = ReactBootstrap.NavItem;
var DropdownButton = ReactBootstrap.DropdownButton;
var MenuItem = ReactBootstrap.MenuItem;

import {SearchStore} from '../stores/searchStore';
import {UserStore} from '../stores/userStore';
import {ActionCreators} from '../actions/searchActions';
import {Search} from '../components/search/searchView';
import {ResultSearch} from '../components/search/searchResultView';
import {AboutView} from './about/aboutView';
import {IndexStatusView} from '../components/indexer/IndexStatusView';
import {ManagementView} from './management/managementView';

import {LoginView} from './login/loginView';
import {LoadingView} from './login/loadingView';
import {UserMixin} from './mixins/userMixin';

import {DirectImageView} from '../components/direct/directImageView';
import {DirectDumpView} from '../components/direct/directDumpView';

import {Endpoints} from '../constants/endpoints';


var App = React.createClass({
  mixins : [Router.Navigation],
  getInitialState: function () {
    return {
      loggedIn: false, selected: "search", username:""
    };
  },
  componentWillMount: function() {
    var self = this;
    //UserStore.listen(this._onChange);

  },
  _onChange:function(){

    if (this.isMounted())
    this.setState({username:"bilo"});

  },
  clicked: function(index){
    this.setState({selected: index});
  },

  logout: function(){
    var self = this;
    $.get(Endpoints.base + "/logout",
      function(data, status){
        //Response
        console.log("Data: " + data + "\nStatus: " + status);
        self.transitionTo('login');
      });
    },

    render: function() {

      console.log("APP RENDER");
      var self = this;
      var menuItems = ["search","management","indexer","about"];
      var sidebarInstance  = (
        <div>
          <ul className="sidebar-nav">
            {
              menuItems.map(function(value, i) {
                var style = '';

                if(self.state.selected === value) {
                  style = 'active';
                }
                if (value === "search")
                  return <li key={i}><a className={style} onClick={self.clicked.bind(self, value)} href="#search">Search</a></li>;
                else if (value === "management")
                  return <li key={i}><a className={style} onClick={self.clicked.bind(self, value)} href="#management">Management</a></li>;
                else if (value === "indexer")
                  return <li key={i}><a className={style} onClick={self.clicked.bind(self, value)} href="#indexer">Indexer</a></li>;
                else if (value === "about")
                  return <li key={i}><a className={style} onClick={self.clicked.bind(self, value)} href="#about">About</a></li>;
                else
                  return "";
                })
            }

          </ul>
            <div className="user-wrapper">
              <div className="col-sm-10">
                <div className="user-name vertical_center">
                  {UserStore.getUsername()}
                </div>
              </div>
              <div className="col-sm-2">
                <div className="user-name vertical_center">
                  <span onClick={this.logout} className="glyphicon glyphicon-log-out"></span>
                </div>

              </div>
            </div>
            <RouteHandler/>
          </div>
        );

        return sidebarInstance;
      }
    });

          var SearchPage = React.createClass({
            render: function() { // FIXME
              React.render(<Search/>, document.getElementById("container"));
              //React.render(<div>Search Page</div>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var ManagementPage = React.createClass({
            render: function() { // FIXME
              React.render(<ManagementView/>, document.getElementById("container"));
              return (<div/>);
            }
          });

          var NotFound = React.createClass({
            render: function() { // FIXME
              React.render(<div>Not found Page</div>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var ResultPage = React.createClass({
            render: function() { // FIXME
              React.render(<ResultSearch/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var AboutPage = React.createClass({
            render: function() { // FIXME
              React.render(<AboutView/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var IndexerPage = React.createClass({
            render: function() { // FIXME
              React.render(<IndexStatusView/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var LoginPage = React.createClass({
            render: function() { // FIXME
              React.render(<LoginView/>, document.getElementById("login_container"));
              return (<div/>);
            }
          });
          var LoadingPage = React.createClass({
            render: function() { // FIXME
              React.render(<LoadingView/>, document.getElementById("login_container"));
              return (<div/>);
            }
          });

          var DirectImagePage = React.createClass({
            render: function() { // FIXME
              React.render(<DirectImageView/>, document.getElementById("container"));
              return (<div/>);
            }
          });

          var DirectDumpPage = React.createClass({
            render: function() { // FIXME
              React.render(<DirectDumpView/>, document.getElementById("container"));
              return (<div/>);
            }
          });

          var Routing = function () {

            var routes = (
              <Route handler={App} >
                <Route key={0} path="search" addHandlerKey={true} handler={SearchPage} >
                </Route>
                <Route key={1} name="management" addHandlerKey={true} handler={ManagementPage} />
                <Route key={2} name="results" addHandlerKey={true} handler={ResultPage} />
                <Route key={3} name="indexer" addHandlerKey={true} handler={IndexerPage} />
                <Route key={4} name="about" addHandlerKey={true} handler={AboutPage} />
                <Route key={5} name="login" addHandlerKey={true} handler={LoginPage} />
                <Route key={6} name="loading" addHandlerKey={true} handler={LoadingPage} />
                <Route key={7} name="image" addHandlerKey={true} handler={DirectImagePage} />
                <Route key={8} name="dump" addHandlerKey={true} handler={DirectDumpPage} />

                <DefaultRoute handler={LoadingPage} />
                <NotFoundRoute handler={NotFound} />
              </Route>
            );

            Router.run(routes, function (Handler) {

            React.render(<Handler/>, document.getElementById("sidebar-wrapper"));
            //React.render(<div>BIlo</div>, document.body);
          });
        };

export default Routing;