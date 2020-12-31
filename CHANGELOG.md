# CHANGELOG
This document intends to keep track of the changes performed on the various releases of Dicoogle.

## 3.0

### 3.0.0  (2020-12-31)

* New: configuration format (config.xml now lives in confs/server.xml)
* New: [SDK] expansion to better support DIM (DICOM Information Modal).
* New: docker support
* New: API documentation
* New: [UI] supports list of loaded plugins
* New: [UI] supports to search and index in multiple plugins with possibility to choose them.
* Fix: When getting defaults, the number of admins was not incremented in the UsersStruct, voiding the deletion of admin users
* Fix: problem with SOP Class Transfer Syntaxes to be loaded in DICOM C-STORE SCP.
* Fix: [UI] logins/logout and user session handling
* Enhancement: [SDK] better error handling
* Enhancement: [SDK] Webcore update structure and dependency of dicoogle-client-js
* Enhancement: [UI] new look and support for small screen devices
* Enhancement: User credentials (hash and encryption improvements)
* Enhancement: Change list of providers to multiselect
* Enhancement: CFindServiceSCP performs log string manipulation only when required.
* Enhancement: Update core libraries (server and UI) and use of webpack.
* Enhancement: Removed deprecated code from oldest RMI GUI.
