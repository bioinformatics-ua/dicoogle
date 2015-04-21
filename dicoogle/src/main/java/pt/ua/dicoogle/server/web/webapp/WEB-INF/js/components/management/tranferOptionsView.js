var React = require('react');



var TransferenceOptionsView = React.createClass({
      render: function() {
        return (
          <div>
          <div className="tab-pane" id="transfer">
                         <div className="panel panel-primary topMargin">
                             <div className="panel-heading">
                                 <h3 className="panel-title">SOP Class Global Transfer Storage Options</h3>
                             </div>
                             <div className="panel-body">
                                 <ul className="list-group">
                                     <li className="list-group-item list-group-item-management">
                                         <div className="row">
                                             <div className="col-xs-6 col-sm-4">
                                                 Accept All
                                             </div>
                                             <div className="col-xs-6 col-sm-8">
                                                 <div className="btn-group">
                                                     <button type="button" className="btn dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                                         Select one <span className="caret"></span>
                                                     </button>
                                                     <ul className="dropdown-menu" role="menu">
                                                         <li><a href="#">Allow all</a>
                                                         </li>
                                                         <li><a href="#">Deny all</a>
                                                         </li>

                                                     </ul>
                                                 </div>
                                             </div>
                                         </div>
                                     </li>
                                     <li className="list-group-item list-group-item-management">
                                         <div className="row">
                                             <div className="col-xs-6 col-sm-4">
                                                 Global Transfer Storage
                                             </div>
                                             <div className="col-xs-6 col-sm-8">
                                                 <div id="GlobalTransferStorage" className="data-table">
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.1.99">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage0" name="GlobalTransferStorageTransferStorage0" checked="checked"/>DeflatedExplicitVRLittleEndian</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.100">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage1" name="GlobalTransferStorageTransferStorage1" checked="checked"/>MPEG2</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.57">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage2" name="GlobalTransferStorageTransferStorage2" checked="checked"/>JPEG Lossless, Non-Hierarchical (Process 14)</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.5">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage3" name="GlobalTransferStorageTransferStorage3" checked="checked"/>RLE Lossless</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.2">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage4" name="GlobalTransferStorageTransferStorage4" checked="checked"/>ExplicitVRBigEndian</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.91">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage5" name="GlobalTransferStorageTransferStorage5" checked="checked"/>JPEG2000</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.1">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage6" name="GlobalTransferStorageTransferStorage6" checked="checked"/>ExplicitVRLittleEndian</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.90">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage7" name="GlobalTransferStorageTransferStorage7" checked="checked"/>JPEG2000 Lossless Only</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.80">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage8" name="GlobalTransferStorageTransferStorage8" checked="checked"/>JPEG Lossless LS</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.50">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage9" name="GlobalTransferStorageTransferStorage9" checked="checked"/>JPEG Baseline 1</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage10" name="GlobalTransferStorageTransferStorage10" checked="true"/>ImplicitVRLittleEndian</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.51">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage11" name="GlobalTransferStorageTransferStorage11"/>JPEG Extended (Process 2 &amp; 4)</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.70">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage12" name="GlobalTransferStorageTransferStorage12"/>JPEG Lossless</label>
                                                     </div>
                                                     <div className="data-table-row">
                                                         <label className="checkbox" title="1.2.840.10008.1.2.4.81">
                                                             <input type="checkbox" id="GlobalTransferStorageTransferStorage13" name="GlobalTransferStorageTransferStorage13"/>JPEG LS Lossy Near Lossless</label>
                                                     </div>
                                                 </div>
                                             </div>
                                         </div>
                                     </li>


                                 </ul>
                             </div>
                         </div>

                     </div>

          </div>
        );
        }
      });

export {
  TransferenceOptionsView
}
