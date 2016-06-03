
/*
 * @author Frederico Silva<fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */

import React from 'react';
import {Link} from 'react-router';
import {UserStore} from '../stores/userStore';

const Sidebar = React.createClass({

  propTypes: {
    pluginMenuItems: React.PropTypes.array.isRequired,
    onLogout: React.PropTypes.func.isRequired
  },

  render() {
      console.log("APP RENDER");
      let menuItems = [
        {value: "search", caption: "Search", admin: false},
        {value: "management", caption: "Management", admin: true},
        {value: "indexer", caption: "Indexer", admin: true},
        {value: "about", caption: "About", admin: false}
      ].concat(this.props.pluginMenuItems);
      let isAdmin = UserStore.isAdmin();
      console.log("Is admin: " + isAdmin)

      let sidebarInstance = (
        <div>
          <ul className="sidebar-nav">
            {
              menuItems.map(function(e, i) {
                const to = (e.isPlugin ? '/ext/' : '/') + e.value;
                  if (!e.admin || isAdmin)
                    return (<li key={e.value}>
                      <Link activeClassName="active" to={to}>{e.caption}</Link>
                    </li>);
              })
            }
          </ul>
        </div>
        );
        return sidebarInstance;
      }
    });

export default Sidebar;
