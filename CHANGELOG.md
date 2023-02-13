# CHANGELOG
This document intends to keep track of the changes performed on the various releases of Dicoogle.

## 3.2

## 3.2.2  (2023-02-13)

- Fix: [C-MOVE] Fix problem related with infinite loop if skip IS returns zero

### 3.2.1 (2023-02-08)

- Enhancement: DicomStorage - add guards on unexpected exceptions (#637)

### 3.2.0 (2022-11-23)

- New: Add opt-in `call-shutdown` setting to call plugin shutdown routines on Dicoogle shutdown (#606)
- Enhancement: Replace DcmSend with custom DICOM sender (#604)
- Enhancement: Fix and update OpenAPI specification (#591)
- Enhancement: [webcore] Update example webplugins (#585)
- Enhancement: [webapp] Added capability to export dump of metadata to CSV  (#593)
- Enhancement: [webapp] Show URI on image meta-data dump  (#592)
- Enhancement: [webapp] Improve aligments in the about page (#616)
- Enhancement: Remove duplicate SOP classes in transfer options (#607)
- [openapi] Fix user management endpoint API specification (#603)
- Chore: Update npm dependencies (#608, #570, #612, #613, #614, #596)
- Chore: Update README/remove not used files (#611, #615)


## 3.1

### 3.1.0 (2022-06-24)

- Enhancement: Bring back depth and paging to searchDIM (#509)
- Enhancement: Add Instance Number to search results (#584)
- Enhancement: Add creation time to running tasks (#511)
- Enhancement: Encode text files and server text responses in UTF-8 (#583)
- Enhancement: Change DICOM logging and deprecate XML log (#577)
- Enhancement: Update commons-collections4 to 4.4 (#561)
- Fix: Set key in dicoogle-slot element (#587)
- Chore: Improve CI Java Maven workflow (#588)

## 3.0

### 3.0.7 (2022-03-17)

- Fix: [UI] Prevent default on export modal button click (#568)
- Chore: Update webapp dependencies (#569)
- Chore: Update contributors in README (#565)

### 3.0.6 (2022-01-14)

- Fix: categorization of plugins in management page (#554)
- Enhancement: Adjust webapp readme for Dicoogle 3 (#548)
- Chore: Update log4j to 2.17.1 (#556)
- Chore: Drop console.log in webapp js bundle (#551)

### 3.0.5 (2021-12-15)

- Fix: ExportCSVToFILEServlet query expansion handling (#550)
- Chore: Bump log4j-core to 2.16.0 (#546) 

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
