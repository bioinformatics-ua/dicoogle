import Reflux from 'reflux';
const ExportActions = exports;
ExportActions.getFieldList = Reflux.createAction();
ExportActions.exportCSV = Reflux.createAction();
ExportActions.getPresets = Reflux.createAction();
ExportActions.savePresets = Reflux.createAction();

export { ExportActions };
