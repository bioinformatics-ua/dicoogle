var Reflux = require('reflux');
var ExportActions = exports;
ExportActions.getFieldList = Reflux.createAction();
ExportActions.exportCSV = Reflux.createAction();

export { ExportActions };
