import React from "react";

import { VersionStore } from "../../stores/versionStore";
import { VersionActions } from "../../actions/versionAction";
import { Panel, Grid, Row, Col } from "react-bootstrap";

const AboutView = React.createClass({
  getInitialState: function() {
    return { version: "" };
  },
  componentWillMount: function() {},
  componentDidMount: function() {
    // Subscribe to the store.
    this.unsubscribe = VersionStore.listen(this.handleGetVersion);
    VersionActions.get();
  },
  handleGetVersion: function(data) {
    this.setState({ version: data.data.version });
  },
  componentWillUnmount() {
    this.unsubscribe();
  },
  render: function() {
    let versionNumber = this.state.version;
    var title = <h3>Dicoogle PACS, version: {versionNumber}</h3>;
    var licenses = (
      <Grid className="">
        <Row className="show-grid">
          <Col className="gridAbout" xs={2} md={2}>
            <b>dcm4che2</b>
            <br />License: GPL
          </Col>
          <Col className="gridAbout" xs={2} md={2}>
            <b>react.js+reflux</b>
            <br />License: BSD
          </Col>
          <Col className="gridAbout" xs={2} md={2}>
            <b>Jetty</b>
            <br />License: GPL
          </Col>
        </Row>
      </Grid>
    );

    var panelsInstance = (
      <div className="about">
        <Panel header={title} bsStyle="primary">
          Dicoogle is an open source medical imaging repository with an
          extensible indexing system and distributed mechanisms. Our solution
          can be used as a PACS archive, or as a client for reading your PACS
          archive file system, thus allowing you to do PACS mining. Moreover, it
          can be easily extended with your own pluggable components. At present,
          we have already indexed around 22 million DICOM images, and this
          number tends to increase. There are several researchers working to
          evaluate and improve the quality of medical records, and Dicoogle has
          contributed to many of such case studies.
          <br />
        </Panel>

        <Panel header="Main third party components" bsStyle="primary">
          {licenses}
          Note: Although these are not the only components used, these are
          considered the main ones.
        </Panel>

        <Panel header="Disclaimer" bsStyle="primary">
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
        </Panel>
        <Panel header="Developers" bsStyle="primary">
          As an open source software, Dicoogle can accept contributions from
          developers around the world. Dicoogle OSS is led and supported by
          Bioinformatics UA and BMD Software. Please check{" "}
          <a target="_new" href="http://www.dicoogle.com">
            the Dicoogle website
          </a>{" "}
          or our{" "}
          <a
            target="_new"
            href="http://www.github.com/bioinformatics-ua/dicoogle"
          >
            GitHub repository
          </a>{" "}
          for more information.
          <div style={{ display: "inline-block", width: "100%" }}>
            <a href="http://bioinformatics.ua.pt">
              <img
                src="assets/logos/logobio.png"
                style={{ height: 40, margin: 5 }}
              />
            </a>
            <a href="http://bmd-software.com/">
              <img
                src="assets/logos/logo.png"
                style={{ height: 40, padding: 5, margin: 5 }}
              />
            </a>
            <a href="http://www.ieeta.pt/">
              <img
                src="assets/logos/logo-ieeta.png"
                style={{ height: 60, margin: 5 }}
              />
            </a>
            <a href="http://www.ua.pt/">
              <img
                src="assets/logos/logo-ua.png"
                style={{ height: 60, margin: 5 }}
              />
            </a>
          </div>
          <div style={{ display: "inline-block" }}>
            <a>
              <img
                src="assets/logos/logoFCT.png"
                style={{ height: 30, margin: 5 }}
              />
            </a>
          </div>
        </Panel>
      </div>
    );
    return panelsInstance;
  }
});

export default AboutView;
