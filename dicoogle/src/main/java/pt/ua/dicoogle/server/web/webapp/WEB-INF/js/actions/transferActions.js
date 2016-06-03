var Reflux = require('reflux');
var TransferActions = exports;
TransferActions.get = Reflux.createAction();
TransferActions.set = Reflux.createAction();
TransferActions.selectAll = Reflux.createAction();
TransferActions.unSelectAll = Reflux.createAction();

export { TransferActions };
