import React from 'react';
import Sidebar from './components/sidebar';
import {Endpoints} from './constants/endpoints';

import DicoogleClient from 'dicoogle-client';
import Webcore from 'dicoogle-webcore';

import {default as Router, Route, IndexRoute, Routes} from 'react-router';
import createBrowserHistory from 'history/lib/createBrowserHistory';

import {Search} from './components/search/searchView';
import {ResultSearch} from './components/search/searchResultView';
import {IndexStatusView} from './components/indexer/IndexStatusView';
import {ManagementView} from './components/management/managementView';
import {UserMixin} from './components/mixins/userMixin';
import {DirectImageView} from './components/direct/directImageView';
import {DirectDumpView} from './components/direct/directDumpView';
import PluginView from './components/plugin/pluginView.jsx';
import AboutView from './components/about/aboutView';
import LoadingView from './components/login/loadingView';
import LoginView from './components/login/loginView';
import ReactDOM from 'react-dom';
import { hashHistory } from 'react-router'


import $ from 'jquery';
require('bootstrap');
require('jquery-ui');

class App extends React.Component {
	
	constructor (props,) {
		super(props);
		this.state = {
			pluginMenuItems: []
		};
		this.logout = this.logout.bind(this);
	}

	componentWillMount () {
	  DicoogleClient(Endpoints.base);
		Webcore.init(Endpoints.base);
		Webcore.addPluginLoadListener(function(plugin) {
		  console.log("Plugin loaded to Dicoogle:", plugin);
		});
		Webcore.fetchPlugins('menu', (packages) => {
		  this.onMenuPlugin(packages);
      Webcore.fetchModules(packages);
		});
    
    // pre-fetch modules of other plugin types
		Webcore.fetchPlugins(['search', 'result-options', 'query', 'result'], Webcore.fetchModules);
  }


  /**
   * @param {packageJSON|packageJSON[]} plugins
   */
	onMenuPlugin(packages) {
		const {pluginMenuItems} = this.state;
    
		this.setState({
			pluginMenuItems: pluginMenuItems.concat(packages.map(pkg => ({
				value: pkg.name,
				caption: pkg.dicoogle.caption || pkg.name,
				isPlugin: true
			})))
		});
	}

	logout() {
		$.get(Endpoints.base + "/logout", (data, status) => {
			//Response
			console.log("Data: " + data + "\nStatus: " + status);
			
			//self.transitionTo('login');
			// Works with recent version of react + react-router
			this.props.history.pushState(null, 'login');
		});
	}

	render() {
		return (
		<div>
			<div className="topbar">
				<img className="btn_drawer" src="assets/drawer_menu.png" id="menu-toggle" />
				<a>Dicoogle</a>
			</div>
			<div id="wrapper">
				<div id="sidebar-wrapper">
					<Sidebar pluginMenuItems={this.state.pluginMenuItems} onLogout={this.logout}/>
				</div>
				<div id="container" style={{display:'block'}}>
					{this.props.children}
				</div>
			</div>
		</div>);
	}
}

class NotFoundView extends React.Component {
	render() {
		return <div>
		  <h1>Not Found</h1>
		</div>;
	}
}

$("#menu-toggle").click(function (e) {
	e.preventDefault();
	$("#wrapper").toggleClass("toggled");
});

ReactDOM.render((
  <Router  history={hashHistory}>
    <Route path="/" component={App}>
	  <IndexRoute component={LoadingView} />
	  <Route path="search" component={Search} />
      <Route path="management" component={ManagementView} />
      <Route path="results" component={ResultSearch} />
      <Route path="indexer" component={IndexStatusView} />
      <Route path="about" component={AboutView} />
      <Route path="login" component={LoginView} />
      <Route path="loading" component={LoadingView} />
      <Route path="image/:uid" component={DirectImageView} />
      <Route path="dump/:uid" component={DirectDumpView} />
	  <Route path="ext/:plugin" component={PluginView} />
	  <Route path="*" component={NotFoundView} />
    </Route>
  </Router>
), document.body);

