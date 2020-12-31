import React from "react";
import { ServicesView } from "./servicesView";
import { PluginsView } from "./pluginsView";
import { AETitleView } from "./aetitleView";

const ServicesAndPluginsView = React.createClass({
  render() {
    const { showToastMessage } = this.props;

    return (
      <div>
        <AETitleView showToastMessage={showToastMessage} />
        <ServicesView showToastMessage={showToastMessage} />
        <PluginsView showToastMessage={showToastMessage} />
      </div>
    );
  }
});

export { ServicesAndPluginsView };
