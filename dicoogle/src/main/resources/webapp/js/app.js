import $ from 'jquery';
import React, {PropTypes} from 'react';
import ReactDOM from 'react-dom';
import Sidebar from './components/sidebar';
import {Endpoints} from './constants/endpoints';
import dicoogleClient from 'dicoogle-client';
import Webcore from 'dicoogle-webcore';

import {Router, Route, IndexRoute} from 'react-router';

import {Search} from './components/search/searchView';
import {SearchResultView} from './components/search/searchResultView';
import {IndexStatusView} from './components/indexer/IndexStatusView';
import {ManagementView} from './components/management/managementView';
import {DirectImageView} from './components/direct/directImageView';
import {DirectDumpView} from './components/direct/directDumpView';
import PluginView from './components/plugin/pluginView';
import AboutView from './components/about/aboutView';
import LoadingView from './components/login/loadingView';
import LoginView from './components/login/loginView';
import { hashHistory /*, browserHistory*/ } from 'react-router'
import {UserActions} from './actions/userActions';
import {UserStore} from './stores/userStore';

require('core-js/shim');

require('jquery-ui');

window.jQuery = $; // Bootstrap won't work without this hack. browserify-shim didn't help either
require('bootstrap');

class App extends React.Component {
  static get contextTypes () {
    return {
			router: PropTypes.object.isRequired,
			location: React.PropTypes.object

		};
  }

	constructor(props) {
		super(props);
		this.pluginsFetched = false;
		this.state = {
			pluginMenuItems: []
		};
		this.logout = this.logout.bind(this);
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
					isPlugin: true,
					icon: 'fa fa-plug'
				})))
		});
	}

	componentWillMount()
	{
		UserStore.listen(this.fetchPlugins.bind(this));

		const Dicoogle = dicoogleClient(Endpoints.base);
		if (localStorage.token) {
			Dicoogle.setToken(localStorage.token);
		}
		if (this.props.location.pathname=='/')
		{
			localStorage.token = null;
			UserActions.logout();
		}
		Webcore.init(Endpoints.base);
	}

	componentDidMount(){
    UserStore.loadLocalStore();
		if (localStorage.token === undefined) {
			this.props.history.pushState(null, 'login');
    }
		if (this.props.location.pathname=='/')
		{
			this.props.history.pushState(null, 'login');
		}

    $("#menu-toggle").click(function (e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });
	}
	fetchPlugins(data) {
		if (this.pluginsFetched)
			return;
		let self = this;
		if (!data.success)
			return;
    this.setState(data);

		Webcore.addPluginLoadListener(function(plugin) {
      console.log("Plugin loaded to Dicoogle:", plugin);
		});
		Webcore.fetchPlugins('menu', (packages) => {
			self.onMenuPlugin(packages);
        Webcore.fetchModules(packages);
		});

    // pre-fetch modules of other plugin types
		Webcore.fetchPlugins(['search', 'result-options', 'query', 'result'], Webcore.fetchModules)
		this.pluginsFetched = true;
  }

  // TODO put this elsewhere
	logout() {
		const Dicoogle = dicoogleClient();
		Dicoogle.logout((error) => {
      if (error) {
        console.error(error);
      }

      this.setState({pluginMenuItems: []});
      this.pluginsFetched = false;
      UserActions.logout()

			this.context.router.push('login');
		});
	}

	render() {

		return (
		<div>
			<div className="topbar">
				<img className="btn_drawer" src="assets/drawer_menu.png" id="menu-toggle" />
				<a>Dicoogle</a>
        <div className="pull-right" bsStyle="padding:15px">

          <span className="user-name usernameLogin" bsStyle="padding-right:10px">
              {UserStore.getUsername()}
          </span>

          <span className="user-name buttonLogin">
              <span onClick={this.logout.bind(this)} className="glyphicon glyphicon-log-out" style={{cursor: 'pointer'}} />
          </span>

        </div>
      </div>

			<div id="wrapper">
				<div id="sidebar-wrapper">
					<Sidebar pluginMenuItems={this.state.pluginMenuItems} onLogout={this.logout.bind(this)}/>
				</div>
				<div id="container" style={{display: 'block'}}>
					{this.props.children}
				</div>
			</div>
		</div>);
	}
}

function NotFoundView() {
	return (<div>
    <h1>Not Found</h1>
	</div>);
}

ReactDOM.render((
  <Router history={hashHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={LoadingView} />
      <Route path="search" component={Search} />
      <Route path="management" component={ManagementView} />
      <Route path="results" component={SearchResultView} />
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
), document.getElementById('react-container'));
