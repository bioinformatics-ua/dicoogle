# CHANGELOG
This document intends to keep track of the changes performed on the various releases of Dicoogle.

## 3.0

### 3.0.5 (2021-12-14)

- Chore: Bump log4j-core to 2.16.0 in (#546)

### 3.0.4 (2021-12-06)

- Fix Queries using the platform for query plugins that implements QueryInterface (#541)
- Fix: Check whether confs dir exists before creating (#538)
- Enhancement: improve web UI manager asset fetching (#540)

### 3.0.3 (2021-11-29)

- Fix: Make WebUI plugins visible to admin (#497)
- Fix: DICOM storage priority improvement (#468)
- Fix: Added missing SOPClasses UIDs for VLWholeSlideMicroscopyImageStorage, BreastTomosynthesisImageStorage and XRayRadiationDoseSRStorage (#474)
- Fix: Add missing StudyInstanceUID in DIMGeneric JSON output (#507)
- Fix: Async DICOM Storage SCP indexing + independent task pool for queries (#503)
- Fix: Add encrypt-users-file to server settings (#504)
- Fix: Fix zip settings property and remove it from UI (#515)
- Fix: Fix server error on /webui without user session (#517)
- Fix: Consider all properties of move destination in web API (#526)
- Fix: Add missing plugin info properties in /plugins (#529)
- Fix: Replace service status signal implementation  (#521)
- Fix: Save move destination properties as XML attributes (#525)
- Fix: Check roles before loading plugins, warn about missing roles (#518)
- Fix: Remove sdk-ext (#493)
- Fix: Fix the image loader when the thumbnail is a larger image (#514)
- Chore: Use HTTPS URLs to Maven repositories (#461)
- Chore: Update important dependencies (dicoogle-client, devdeps) (#523)
- Chore: Server code cleanup (#500)

### 3.0.2 (2021-04-03)

- Fix: exportToCSV modal on webapp (#450)
- Fix: StackOverflowError and users settings folder issues (#451)
- Enhancement: DICOM services - bring patches from 2.5.X releases to main stream (#453)

### 3.0.1 (2021-02-08)

* Fix: Webcore plugins was not able to load 
* Fix: WebUI Plugins: emit events for result-batch slot plugins
* Enhancement: [UI] small improvements in button UI for search result slot. (#447)

### 3.0.0 (2020-12-31)

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
