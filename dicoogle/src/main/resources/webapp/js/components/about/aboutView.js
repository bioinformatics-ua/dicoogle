import React from "react";
import createReactClass from "create-react-class";

import { VersionStore } from "../../stores/versionStore";
import { VersionActions } from "../../actions/versionAction";
import { Panel, Grid, Row, Col } from "react-bootstrap";

const AboutView = createReactClass({
  getInitialState: function () {
    return { version: "" };
  },
  componentWillMount: function () { },
  componentDidMount: function () {
    // Subscribe to the store.
    this.unsubscribe = VersionStore.listen(this.handleGetVersion);
    VersionActions.get();
  },
  handleGetVersion: function (data) {
    this.setState({ version: data.data.version });
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  render: function () {
    const versionNumber = this.state.version;

    var panelsInstance = (
      <div className="about">
        <Panel bsStyle="primary">
          <Panel.Heading>
            <h4>Dicoogle version {versionNumber}</h4>
          </Panel.Heading>
          <div className="aboutBoxes">
            Dicoogle is an open-source PACS archive software that replaces the
            traditional database model with an extensible indexing and retrieval
            framework and provides easy expansion of functionalities through the
            use of plug-ins. It was designed to accommodate automatic information
            extraction, indexing, and storage of all meta-data detected in medical
            images, without re-engineering or reconfiguration requirements, thus
            overcoming the limitations of traditional DICOM query services. By
            presenting the technical assets for plugin development such as a
            Software Development Kit (SDK), developers are free to expand the
            archive independently and non-exclusively, without changes to the core
            platform. This extensible architecture of Dicoogle has enabled its use
            in research and the healthcare industry, as many use cases can be
            fulfilled in the same deployment.
          </div>
        </Panel>
        <Panel bsStyle="primary">
          <Panel.Heading>
            <h4>Main third party components</h4>
          </Panel.Heading>
          <div className="aboutBoxes">
            <Grid>
              <Row className="show-grid">
                <Col className="gridAbout" xs={2} md={2}>
                  <b>dcm4che2</b>
                  <br />License: GPL
                </Col>
                <Col className="gridAbout" xs={2} md={2}>
                  <b>React</b>
                  <br />License: MIT
                </Col>
                <Col className="gridAbout" xs={2} md={2}>
                  <b>Jetty</b>
                  <br />License: GPL
                </Col>
              </Row>
            </Grid>
            Note: Although these are not the only components used, these are
            considered the main ones.
          </div>
        </Panel>

        <Panel bsStyle="primary">
          <Panel.Heading>
            <h4>Disclaimer</h4>
          </Panel.Heading>
          <div className="aboutBoxes">
            This software is provided by the copyright holders and contributors
            "as is" and any express or implied warranties, including, but not
            limited to, the implied warranties of merchantability and fitness for
            a particular purpose are disclaimed. In no event shall the copyright
            owner or contributors be liable for any direct, indirect, incidental,
            special, exemplary, or consequential damages (including, but not
            limited to, procurement of substitute goods or services; loss of use,
            data, or profits; or business interruption) however caused and on any
            theory of liability, whether in contract, strict liability, or tort
            (including negligence or otherwise) arising in any way out of the use
            of this software, even if advised of the possibility of such damage.
          </div>
        </Panel>
        <Panel bsStyle="primary">
          <Panel.Heading>
            <h4>Developers</h4>
          </Panel.Heading>
          <div className="aboutBoxes">
            As an open source software, Dicoogle can accept contributions from
            developers around the world. Dicoogle OSS is led and supported by
            Bioinformatics UA and{" "}
            <a target="_new" href="https://www.bmd-software.com">
              BMD Software
            </a>. Please check{" "}
            <a target="_new" href="https://www.dicoogle.com">
              the Dicoogle website
            </a>{" "}
            or our{" "}
            <a
              target="_new"
              href="https://www.github.com/bioinformatics-ua/dicoogle"
            >
              GitHub repository
            </a>{" "}
            for more information.
            <div>
              <a href="https://bioinformatics.ua.pt">
                <img
                  src="assets/logos/logobio.png"
                  style={{ height: "3em", margin: 5 }}
                />
              </a>
              <a href="https://bmd-software.com/">
                <img
                  src="assets/logos/logo.png"
                  style={{ height: "3em", margin: 5 }}
                />
              </a>
              <a href="https://www.ieeta.pt/">
                <img
                  src="assets/logos/logo-ieeta.png"
                  style={{ height: "5em", margin: 5 }}
                />
              </a>
              <a href="https://www.ua.pt/">
                <img
                  src="assets/logos/logo-ua.png"
                  style={{ height: "5em", margin: 5 }}
                />
              </a>
            </div>
            <div style={{ display: "inline-block" }}>
              <a>
                <img
                  src="assets/logos/logoFCT.png"
                  style={{ height: "4em", margin: 5 }}
                />
              </a>
            </div>
          </div>
        </Panel>
      </div>
    );
    return panelsInstance;
  }
});

export default AboutView;
