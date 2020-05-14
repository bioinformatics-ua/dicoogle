import "../sass/dicoogle.scss";
import React, { PropTypes } from "react";
import ReactDOM from "react-dom";
import Sidebar from "./components/sidebar";
import { Endpoints } from "./constants/endpoints";
import dicoogleClient from "dicoogle-client";
import Webcore from "dicoogle-webcore";

import { Router, Route, IndexRoute } from "react-router";

import { Search } from "./components/search/searchView";
import { SearchResultView } from "./components/search/searchResultView";
import { IndexStatusView } from "./components/indexer/IndexStatusView";
import { ManagementView } from "./components/management/managementView";
import { DirectImageView } from "./components/direct/directImageView";
import { DirectDumpView } from "./components/direct/directDumpView";
import PluginView from "./components/plugin/pluginView";
import AboutView from "./components/about/aboutView";
import LoadingView from "./components/login/loadingView";
import LoginView from "./components/login/loginView";
import { hashHistory /*, browserHistory*/ } from "react-router";
import * as UserActions from "./actions/userActions";
import UserStore from "./stores/userStore";

import "@ungap/custom-elements-builtin";
import "core-js/shim";
import "bootstrap";

class App extends React.Component {
  static get contextTypes() {
    return {
      router: PropTypes.object.isRequired,
      location: React.PropTypes.object
    };
  }

  constructor(props) {
    super(props);
    this.needsPluginUpdate = true;
    this.state = {
      pluginMenuItems: [],
      lastLocation: "search"
    };
    this.dicoogle = dicoogleClient(Endpoints.base);
    this.logout = this.logout.bind(this);
    this.handleUserStoreUpdate = this.handleUserStoreUpdate.bind(this);
  }

  /**
   * @param {packageJSON|packageJSON[]} plugins
   */
  onMenuPlugin(packages) {
    const { pluginMenuItems } = this.state;

    this.setState({
      pluginMenuItems: pluginMenuItems.concat(
        packages.map(pkg => ({
          value: pkg.name,
          caption: pkg.dicoogle.caption || pkg.name,
          isPlugin: true,
          icon: "fa fa-plug"
        }))
      )
    });
  }

  componentWillMount() {
    UserStore.listen(this.handleUserStoreUpdate);

    let lastLocation = this.props.location.pathname.slice(1);
    if (
      lastLocation !== "" &&
      lastLocation !== "login" &&
      lastLocation !== "loading"
    ) {
      this.setState({
        lastLocation: lastLocation
      });
    }
  }

  componentDidMount() {
    if (process.env.GUEST_USERNAME && !localStorage.getItem("token")) {
      console.log(
        "Using guest credentials: ",
        process.env.GUEST_USERNAME,
        "; password:",
        process.env.GUEST_PASSWORD
      );
      UserActions.login(process.env.GUEST_USERNAME, process.env.GUEST_PASSWORD);
    } else {
      UserStore.loadLocalStore();
    }

    if (this.props.location.pathname === "/") {
      this.props.history.pushState(null, "login");
    }
  }

  handleUserStoreUpdate(data) {
    this.needsPluginUpdate = true;
    this.fetchPlugins(data);
    if (data.username) {
      this.setState(data);
    }

    if (!data.isLoggedIn) {
      if (!process.env.GUEST_USERNAME) {
        this.props.router.push("login");
      } else {
        if (!data.loginFailed) {
          this.props.router.push("loading");
        } else {
          this.props.router.push("login");
        }
      }
    } else {
      this.props.router.replace(this.state.lastLocation);
    }
  }

  fetchPlugins(data) {
    if (!this.needsPluginUpdate) {
      console.log("Plugin fetch not required, ignoring plugin fetch request");
      return;
    }
    if (!data.isLoggedIn) {
      console.log("Not logged in, ignoring plugin fetch request");
      return;
    }
    if (!data.success) {
      console.log("Unsuccessfull operation, ignoring plugin fetch request");
      return;
    }

    let k = 2;
    Webcore.fetchPlugins("menu", packages => {
      this.onMenuPlugin(packages);
      Webcore.fetchModules(packages);
      k -= 1;
      if (k === 0) {
        this.needsPluginUpdate = false;
      }
    });

    // pre-fetch modules of other plugin types
    Webcore.fetchPlugins(
      ["search", "result-options", "query", "result"],
      pkgs => {
        Webcore.fetchModules(pkgs);
        k -= 1;
        if (k === 0) {
          this.needsPluginUpdate = false;
        }
      }
    );
  }

  onClickToggle(e) {
    e.preventDefault();
    document.getElementById("wrapper").classList.toggle("toggled");
  }

  logout() {
    UserActions.logout();
    this.setState({
      pluginMenuItems: [],
      lastLocation: "search"
    });
    this.needsPluginUpdate = true;
    this.context.router.push("login");
  }

  render() {
    return (
      <div>
        <div className="topbar">
          <img
            className="btn_drawer"
            src="assets/drawer_menu_light_blue.png"
            id="menu-toggle"
            onClick={this.onClickToggle}
          />
          <img
            className="logo-image"
            src="assets/logo-light-blue.png"
            id="webapp-logo-light"
          />
          <div className="pull-right" bsStyle="padding:15px">
            <span
              className="user-name usernameLogin"
              bsStyle="padding-right:10px"
            >
              {UserStore.getUsername()}
            </span>

            <span className="user-name buttonLogin">
              <span
                onClick={this.logout}
                className="glyphicon glyphicon-log-out"
                style={{ cursor: "pointer" }}
              />
            </span>
          </div>
        </div>

        <div id="wrapper">
          <div id="sidebar-wrapper">
            <Sidebar
              pluginMenuItems={this.state.pluginMenuItems}
              onLogout={this.logout}
            />
          </div>
          <div id="container" style={{ display: "block" }}>
            {this.props.children}
          </div>
        </div>
      </div>
    );
  }
}

function NotFoundView() {
  return (
    <div>
      <h1>Not Found</h1>
    </div>
  );
}

Webcore.init(Endpoints.base);

ReactDOM.render(
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
  </Router>,
  document.getElementById("react-container")
);
