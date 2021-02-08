/*
 * @author Frederico Silva<fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */

import React from "react";
import createReactClass from "create-react-class";
import * as PropTypes from "prop-types";
import { Link } from "react-router";
import UserStore from "../stores/userStore";

const Sidebar = createReactClass({
  propTypes: {
    pluginMenuItems: PropTypes.array.isRequired,
    onLogout: PropTypes.func
  },

  render() {
    console.log("APP RENDER");
    let menuItems = [
      {
        value: "search",
        caption: "Search",
        admin: false,
        icon: "fa fa-search"
      },
      {
        value: "management",
        caption: "Management",
        admin: true,
        icon: "fa fa-cogs"
      },
      {
        value: "indexer",
        caption: "Indexer",
        admin: true,
        icon: "fa fa-file-archive-o"
      },
      { value: "about", caption: "About", admin: false, icon: "fa fa-info" }
    ].concat(this.props.pluginMenuItems);
    let isAdmin = UserStore.isAdmin();
    console.log("Is admin: " + isAdmin);

    let sidebarInstance = (
      <div>
        <ul className="sidebar-nav">
          {menuItems.map(function(e, i) {
            const to = (e.isPlugin ? "/ext/" : "/") + e.value;
            if (!e.admin || isAdmin)
              return (
                <li key={e.value}>
                  <Link activeClassName="active" to={to}>
                    <i className={e.icon} /> &nbsp; {e.caption}
                  </Link>
                </li>
              );
          })}
        </ul>
      </div>
    );
    return sidebarInstance;
  }
});

export default Sidebar;
