var React = require('react');

import {TransferenceOptionsView} from '../management/tranferOptionsView';
import {ServicesView} from '../management/servicesView';

var ManagementView = React.createClass({
      render: function() {
        return (
          <div className="container-fluid content">
              <ul className="nav nav-pills">
                  <li className="active" role="presentation"><a href="#transfer" data-toggle="tab">Transference Options</a>
                  </li>
                  <li role="presentation"><a href="#services" data-toggle="tab">Services and Plugins</a>
                  </li>
                  <li role="presentation"><a href="#storage" data-toggle="tab">Storage Servers</a>
                  </li>
              </ul>
              <div id="my-tab-content" className="tab-content">
                  <div id="transfer" className="tab-pane active">
                      <TransferenceOptionsView/>
                  </div>
                  <div id="services" className="tab-pane">
                      <ServicesView/>
                  </div>
                  <div id="storage" className="tab-pane">
                  storage
                  </div>
              </div>
          </div>
        );
        }
      });

export {
  ManagementView
}
