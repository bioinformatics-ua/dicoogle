import React from "react";
import { ServicesView } from "./servicesView";
import { PluginsView } from "./pluginsView";
import { AETitleView } from "./aetitleView";

const ServicesAndPluginsView = React.createClass({
  render() {
    return (
      <div>
        <AETitleView />
        <ServicesView />
        <PluginsView />
      </div>
    );
  }
});

export { ServicesAndPluginsView };
