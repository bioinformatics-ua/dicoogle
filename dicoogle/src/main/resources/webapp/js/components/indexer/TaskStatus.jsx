import React from "react";
import { toHumanReadable } from "../../utils/time";
import * as PropTypes from "prop-types";

class TaskStatus extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  static get propTypes() {
    return {
      item: PropTypes.shape({
        taskUid: PropTypes.string.isRequired,
        complete: PropTypes.bool,
        canceled: PropTypes.bool,
        taskProgress: PropTypes.number,
        elapsedTime: PropTypes.number,
        nIndexed: PropTypes.number,
        nErrors: PropTypes.number
      }).isRequired,
      onCloseStopClicked: PropTypes.func.isRequired
    };
  }

  render() {
    const { item, onCloseStopClicked } = this.props;
    const { complete, canceled } = item;
    const unknownPercentage =
      typeof item.taskProgress !== "number" || item.taskProgress < 0;
    const percentage =
      complete || canceled || unknownPercentage
        ? "100%"
        : Math.round(item.taskProgress * 100) + "%";

    let barstate = "indexprogress progress-bar progress-bar-striped";
    if (item.nErrors > 0 && item.nIndexed > 0) {
      barstate += " progress-bar-warning";
    } else if (item.nErrors > 0 && item.nIndexed === 0) {
      barstate += " progress-bar-danger";
    } else if (unknownPercentage && !complete) {
      barstate += " progress-bar-info active";
    } else {
      barstate += " progress-bar-success";
      if (!complete && !canceled) {
        barstate += " active";
      }
    }
    const barStyle = {
      width: percentage
    };
    if (canceled) {
      barStyle.backgroundColor = "#CCCCCC";
    }

    return (
      <div key={item.taskUid} className="well well-sm">
        <div className="row">
          <div className="col-sm-10">
            <div className="progress indexstatusprogress">
              <div
                style={barStyle}
                className={barstate}
                role="progressbar"
                aria-valuemin="0"
                aria-valuemax="100"
              >
                {canceled ? "canceled" : !unknownPercentage && percentage}
              </div>
            </div>
          </div>
          <div className="col-sm-2">
            <button className="btn btn-danger" onClick={onCloseStopClicked}>
              {complete || canceled ? "Close" : "Stop"}
            </button>
          </div>
        </div>
        <div>
          <p>
            <b>Uid: </b> {item.taskUid}
          </p>
          <p>
            <b>Name: </b> {item.taskName}
          </p>
          <div style={{ visibility: item.complete ? "" : "hidden" }}>
            {typeof item.elapsedTime === "number" && (
              <p>
                <b>Elapsed Time: </b> {toHumanReadable(item.elapsedTime)}
              </p>
            )}
            {typeof item.nIndexed === "number" && (
              <p>
                <b>Indexed: </b> {item.nIndexed}{" "}
              </p>
            )}
            {typeof item.nErrors === "number" && (
              <p>
                <b>Errors: </b> {item.nErrors}{" "}
              </p>
            )}
          </div>
        </div>
      </div>
    );
  }
}

export default TaskStatus;
