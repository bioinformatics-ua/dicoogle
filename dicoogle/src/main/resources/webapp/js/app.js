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
		this.dicoogle = dicoogleClient(Endpoints.base);
		this.logout = this.logout.bind(this);
		this.handleUserStoreUpdate = this.handleUserStoreUpdate.bind(this);
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
		UserStore.listen(this.handleUserStoreUpdate);

		if (localStorage.token) {
			this.dicoogle.setToken(localStorage.token);
		}
		if (this.props.location.pathname === '/')
		{
			localStorage.token = null;
			UserActions.logout();
		}
		Webcore.init(Endpoints.base);
	}

	componentDidMount(){
    UserStore.loadLocalStore();
		if (!this.dicoogle.getToken() && this.props.location.pathname === '/') {
			if (process.env.GUEST_USERNAME) {
				console.log("Using guest credentials: ", process.env.GUEST_USERNAME, "; password:", process.env.GUEST_PASSWORD);
				const unsubscribe = UserStore.listen((outcome) => {
					if (outcome.isLoggedIn) {
						this.props.history.replace('search');
					} else {
						this.props.history.replace(null, 'login');
					}
					unsubscribe();
				})
				UserActions.login(process.env.GUEST_USERNAME, process.env.GUEST_PASSWORD);
				this.props.history.pushState(null, 'loading');
			} else {
				this.props.history.pushState(null, 'login');
			}
    }

    $("#menu-toggle").click(function (e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });
	}

	handleUserStoreUpdate(data) {
		this.fetchPlugins(data);
		if (data.username) {
			this.setState(data);
		}
	}

	fetchPlugins(data) {
    if (this.pluginsFetched || !data.isLoggedIn)
      return;
		if (!data.success)
			return;
    this.setState(data);

		Webcore.addPluginLoadListener(function(plugin) {
      console.log("Plugin loaded to Dicoogle:", plugin);
		});
		Webcore.fetchPlugins('menu', (packages) => {
			this.onMenuPlugin(packages);
      Webcore.fetchModules(packages);
		});

    // pre-fetch modules of other plugin types
		Webcore.fetchPlugins(['search', 'result-options', 'query', 'result'], Webcore.fetchModules)
		this.pluginsFetched = true;
  }

	logout() {
		const Dicoogle = dicoogleClient();
		Dicoogle.request('POST', 'logout', {}, (error) => {
      if (error) {
        console.error(error);
      }

      this.setState({pluginMenuItems: []});
      this.pluginsFetched = false;
      UserActions.logout();
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
              <span onClick={this.logout} className="glyphicon glyphicon-log-out" style={{cursor: 'pointer'}} />
          </span>

        </div>
      </div>

			<div id="wrapper">
				<div id="sidebar-wrapper">
					<Sidebar pluginMenuItems={this.state.pluginMenuItems} onLogout={this.logout}/>
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
