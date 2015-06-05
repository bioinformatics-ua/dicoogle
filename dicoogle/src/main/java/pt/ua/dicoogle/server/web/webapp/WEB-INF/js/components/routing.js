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
              menuItems.map(function(index){
                var style = '';

                if(self.state.selected == index){
                  style = 'active';
                }
                if(index == "search")
                return <li><a className={style} onClick={self.clicked.bind(self, index)} href="#search">Search</a></li>;
                  else if(index == "management")
                  return <li><a className={style} onClick={self.clicked.bind(self, index)} href="#management">Management</a></li>;
                    else if(index == "indexer")
                    return <li><a className={style} onClick={self.clicked.bind(self, index)} href="#indexer">Indexer</a></li>;
                      else if(index == "about")
                      return <li><a className={style} onClick={self.clicked.bind(self, index)} href="#about">About</a></li>;
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
            render: function() {
              React.render(<Search/>, document.getElementById("container"));
              //React.render(<div>Search Page</div>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var ManagementPage = React.createClass({
            render: function() {
              React.render(<ManagementView/>, document.getElementById("container"));
              return (<div/>);
            }
          });

          var NotFound = React.createClass({
            render: function() {
              React.render(<div>Not found Page</div>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var ResultPage = React.createClass({
            render: function() {
              React.render(<ResultSearch/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var AboutPage = React.createClass({
            render: function() {
              React.render(<AboutView/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var IndexerPage = React.createClass({
            render: function() {
              React.render(<IndexStatusView/>, document.getElementById("container"));
              return (<div/>);
            }
          });
          var LoginPage = React.createClass({
            render: function() {
              React.render(<LoginView/>, document.getElementById("login_container"));
              return (<div/>);
            }
          });
          var LoadingPage = React.createClass({
            render: function() {
              React.render(<LoadingView/>, document.getElementById("login_container"));
              return (<div/>);
            }
          });

          var Routing = function () {

            var routes = (
              <Route handler={App} path="/">
                <Route name="search" addHandlerKey={true} handler={SearchPage}>
                  <Route name="silo" path="/results" handler={ResultPage}/>
                </Route>
                <Route name="management" addHandlerKey={true} handler={ManagementPage} />
                <Route name="indexer" addHandlerKey={true} handler={IndexerPage} />
                <Route name="about" addHandlerKey={true} handler={AboutPage} />
                <Route name="login" addHandlerKey={true} handler={LoginPage} />
                <Route name="loading" addHandlerKey={true} handler={LoadingPage} />


                <DefaultRoute handler={LoadingPage} />
                <NotFoundRoute handler={NotFound} />
              </Route>
            );

            Router.run(routes, function (Handler) {

              React.render(<Handler/>, document.getElementById("sidebar-wrapper"));
              //React.render(<div>BIlo</div>, document.body);
            });
          };

          module.exports = Routing;
