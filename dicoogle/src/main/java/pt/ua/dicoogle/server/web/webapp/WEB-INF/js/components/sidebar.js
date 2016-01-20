
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
        {value: "search", caption: "Search"},
        {value: "management", caption: "Management"},
        {value: "indexer", caption: "Indexer"},
        {value: "about", caption: "About"}
      ].concat(this.props.pluginMenuItems);

      let sidebarInstance = (
        <div>
          <ul className="sidebar-nav">
            {
              menuItems.map(function(e, i) {
                const to = (e.isPlugin ? '/ext/' : '/') + e.value;
                return (<li key={i}>
                  <Link activeClassName="active" to={to}>{e.caption}</Link>
                </li>);
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
                  <span onClick={this.props.onLogout} className="glyphicon glyphicon-log-out" style={{cursor: 'pointer'}} />
                </div>
              </div>
            </div>
          </div>
        );
        return sidebarInstance;
      }
    });

export default Sidebar;
