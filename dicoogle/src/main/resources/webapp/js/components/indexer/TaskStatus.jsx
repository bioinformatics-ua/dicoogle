import React from "react";
import { dateTimeToHumanReadable, toHumanReadable } from "../../utils/time";
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
        taskTimeCreated: PropTypes.string,
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
    const timeCreated = new Date(item.taskTimeCreated);

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
      <div key={item.taskUid} className="well well-sm task-status">
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
        <ul>
          <li>
            <b>Uid: </b> {item.taskUid}
          </li>
          <li>
            <b>Name: </b> {item.taskName}
          </li>
          <li>
            <b>Time created: </b> {dateTimeToHumanReadable(timeCreated)}
          </li>
          <li className="task-status-complete" style={{ visibility: item.complete ? "" : "hidden" }}>
            {typeof item.elapsedTime === "number" && (
              <span>
                <b>Elapsed Time: </b> {toHumanReadable(item.elapsedTime)}
              </span>
            )}
            {typeof item.nIndexed === "number" && (
              <span>
                <b>Indexed: </b> {item.nIndexed}
              </span>
            )}
            {typeof item.nErrors === "number" && (
              <span>
                <b>Errors: </b> {item.nErrors}
              </span>
            )}
          </li>
        </ul>
      </div>
    );
  }
}

export default TaskStatus;
