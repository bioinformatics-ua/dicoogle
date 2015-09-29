/*jshint esnext: true*/

/*
 * @author Frederico Silva<fredericosilva@ua.pt>
 * @author Edaurdo Pinho <eduardopinho@ua.pt>
 */

import React from 'react';
import Bootstrap from 'bootstrap';
import {Nav, Button, NavItemLink, ButtonLink, Navbar, NavItem, DropdownButton, MenuItem} from 'react-bootstrap';

import Webcore from 'dicoogle-webcore';
import {Link} from 'react-router';

import {SearchStore} from '../stores/searchStore';
import {UserStore} from '../stores/userStore';
import {ActionCreators} from '../actions/searchActions';
import {UserMixin} from './mixins/userMixin';
import {Endpoints} from '../constants/endpoints';

var Sidebar = React.createClass({

  propTypes: {
    pluginMenuItems: React.PropTypes.array.isRequired,
    onLogout: React.PropTypes.func.isRequired
  },

  getInitialState() {
    return {
      selected: "search"
    };
  },

  clicked(index){
    this.setState({selected: index});
  },

  render() {
      console.log("APP RENDER");
      let self = this;
      let menuItems = [
        {value: "search", caption: "Search"},
        {value: "management", caption: "Management"},
        {value: "indexer", caption: "Indexer"},
        {value: "about", caption: "About"}
      ].concat(this.props.pluginMenuItems);
      
      let sidebarInstance  = (
        <div>
          <ul className="sidebar-nav">
            {
              menuItems.map(function(e, i) {
                let style = '';

                if(self.state.selected === e.value) {
                  style = 'active';
                }
                const to = (e.isPlugin ?'/ext/':'/') + e.value;
                return (<li key={i}>
                  <Link className={style} to={to} onClick={self.clicked.bind(self, e.value)}>{e.caption}</Link>
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
                  <span onClick={this.props.onLogout} className="glyphicon glyphicon-log-out"></span>
                </div>

              </div>
            </div>
          </div>
        );
        return sidebarInstance;
      }
    });

export default Sidebar;
