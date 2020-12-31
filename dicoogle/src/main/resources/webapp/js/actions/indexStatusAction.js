import Reflux from "reflux";
const IndexStatusActions = {
  get: Reflux.createAction(),
  start: Reflux.createAction(),
  stop: Reflux.createAction(),
  close: Reflux.createAction()
};
export { IndexStatusActions };
