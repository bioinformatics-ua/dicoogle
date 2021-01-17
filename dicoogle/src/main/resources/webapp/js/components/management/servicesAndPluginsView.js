import React from "react";
import createReactClass from "create-react-class";
import { ServicesView } from "./servicesView";
import { PluginsView } from "./pluginsView";
import { AETitleView } from "./aetitleView";

const ServicesAndPluginsView = createReactClass({
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
