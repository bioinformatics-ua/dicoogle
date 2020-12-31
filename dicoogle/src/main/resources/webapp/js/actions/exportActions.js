import Reflux from "reflux";
const ExportActions = {
  getFieldList: Reflux.createAction(),
  exportCSV: Reflux.createAction(),
  getPresets: Reflux.createAction(),
  savePresets: Reflux.createAction()
};
export { ExportActions };
