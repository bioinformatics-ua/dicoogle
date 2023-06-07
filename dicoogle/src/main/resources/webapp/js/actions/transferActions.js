import Reflux from "reflux";

export const get = Reflux.createAction();
export const set = Reflux.createAction();
export const selectAll = Reflux.createAction();
export const unSelectAll = Reflux.createAction();
export const selectAllSOP = Reflux.createAction();
export const unSelectAllSOP = Reflux.createAction();

export const TransferActions = {
  get,
  set,
  selectAll,
  unSelectAll,
  selectAllSOP,
  unSelectAllSOP,
};
