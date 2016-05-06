import React from 'react';
const {PropTypes} = React;

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
          taskProgress: PropTypes.number,
          elapsedTime: PropTypes.number,
          nIndexed: PropTypes.number,
          nErrors: PropTypes.number
        }).isRequired,
      onCloseStopClicked: PropTypes.func.isRequired
    };
  }

  render() {
    const {item, onCloseStopClicked} = this.props;
    const {complete} = item;
    const unknownPercentage = (typeof item.taskProgress !== 'number' || item.taskProgress < 0);
    const percentage = (complete || unknownPercentage) ? '100%'
      : (Math.round(item.taskProgress * 100) + '%');

    let barstate = "indexprogress progress-bar progress-bar-striped";
    if (item.nErrors > 0 && item.nIndexed > 0) {
      barstate += " progress-bar-warning";
    } else if((item.nErrors > 0) && (item.nIndexed == 0)) {
      barstate += " progress-bar-danger";
    } else if (unknownPercentage && !complete) {
      barstate += " progress-bar-info active";
    } else {
      barstate += " progress-bar-success";
      if (!complete) {
        barstate += " active";
      }
    }

  return (
     <div key={item.taskUid} className="well well-sm">
       <div className="row">
      <div className="col-sm-10">
        <div className="progress indexstatusprogress">
          <div style={{width: percentage}} className={barstate} role="progressbar"  aria-valuemin="0" aria-valuemax="100">
            {!unknownPercentage && percentage}
          </div>
        </div>
      </div>
      <div className="col-sm-2">
        <button className="btn btn-danger" onClick={onCloseStopClicked}>
          {complete ? "Close" : "Stop"}
        </button>
      </div>
    </div>
    <div>
      <p><b>Uid: </b> {item.taskUid}</p>
      <p><b>Name: </b> {item.taskName}</p>
      <p style={{visibility : item.complete ? '' : 'hidden'}}>
          {(typeof item.elapsedTime === 'number') && (
            <p><b>Elapsed Time: </b> {item.elapsedTime} ms</p>)}
          {(typeof item.nIndexed === 'number') && (
            <p><b>Indexed: </b> {item.nIndexed} </p>)}
          {(typeof item.nErrors === 'number') && (
            <p><b>Errors: </b> {item.nErrors} </p>)}
        </p>
      </div>
    </div>);
	}
}

export default TaskStatus;
