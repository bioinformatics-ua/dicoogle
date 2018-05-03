import React from 'react';
import {ServicesView} from "./servicesView";
import {PluginsView} from "./pluginsView";

const ServicesAndPluginsView = React.createClass({
  render() {
    return (
      <div>
        <ServicesView/>
        <PluginsView/>
      </div>
    );
  }
});

export {ServicesAndPluginsView};
