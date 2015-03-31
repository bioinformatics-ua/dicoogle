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
import {ActionCreators} from '../actions/searchActions';
import {Search} from '../components/search/searchView';
import {ResultSearch} from '../components/search/searchResultView';
import {AboutView} from './about/aboutView';


var App = React.createClass({
getInitialState: function () {
    return {
        loggedIn: false, selected: "search"
    };
},

clicked: function(index){
    this.setState({selected: index});
},


render: function() {
    console.log("APP RENDER");
    var self = this;
    var menuItems = ["search","management","about"];
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
                    Frederico Silva
                </div>
            </div>
            <div className="col-sm-2">
                <div className="user-name vertical_center">
                    <span className="glyphicon glyphicon-log-out"></span>
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
var HomePage = React.createClass({
render: function() {
    React.render(<div>Management Page</div>, document.getElementById("container"));
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


var Routing = function () {

var routes = (
    <Route handler={App} path="/">
        <Route name="search" addHandlerKey={true} handler={SearchPage}>
            <Route name="silo" path="/results" handler={ResultPage}/>
        </Route>
     <Route name="management" addHandlerKey={true} handler={HomePage} />
     <Route name="about" addHandlerKey={true} handler={AboutPage} />


     <DefaultRoute handler={SearchPage} />
     <NotFoundRoute handler={NotFound} />
    </Route>
);

Router.run(routes, function (Handler) {

    React.render(<Handler/>, document.getElementById("sidebar-wrapper"));

});
};

module.exports = Routing;
