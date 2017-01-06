Dicoogle
========

Dicoogle is an extensible, platform-independent and open-source PACS archive software that replaces the traditional centralized database with a more agile indexing and retrieval mechanism. It was designed with automatic extraction, indexing and storage of all meta-data detected in medical images, including private DICOM attribute tags, without re-engineering or reconfiguration requirements.

Brief Documentation
===================

#### Setup Dicoogle Platform Environment
  1. Copy dicoogle.jar to the installation folder. For example DicoogleDir/
  2. Create the Folder DicoogleDir/Plugins.

      This folder will hold the plugins used by our instance of the Dicoogle Platform.
  3. Next, copy the desired plugins into the DicoogleDir/Plugins Folder.
      
      The typical setup of Dicoogle involves the deployment of an Index and a Query Plugin. 
      We supply an implementation of such plugin based in the Lucene Search Engine (Lucene-plugin.jar). 
  4. Run Dicoogle.
  
      Dicoogle may be run as a server: ```java -jar dicoogle.jar -s```

      To also load Dicoogle's user interface to the default browser: ```java -jar dicoogle.jar```

#### Available Plugins
  * Lucene Index/Query Plugin - (lucene.jar)
      
      Plugin Based on lucene to support index and query of DICOM Meta-data.
  * File Storage Plugin - (filestorage.jar)

      Plugin used for the storage of DICOM Files. This plugin is necessary in order to use Dicoogle as a DICOM Storage Provider.
      
      Our file storage plugin maps the DICOM hierarchical organization (Patient->Study->Series->Image) into a directory tree in the file system. Every object in the Dicoogle Platform may be traced back to its storage location by a URI, similar to file:///tmp/file. In order to support multiple providers, every Storage plugin must define a unique schema, i.e protocol. 
      
      * Settings
      
        * root-dir: is the root directory where DICOM Files will be stored
        * schema: Specifies the schema/protocol of the file plugin. This value is arbitrary, but must be unique among all installed plugins. As such, avoid using well known protocol names such as http or file.

  * Dicoogle Wan Plugin - (wan-plugin.jar)

      The WAN Plugin may be used to federate instances of the Dicoogle Repository. The google app engine is used to federate and relay communications between the multiple instances.
      It is possible to call services on remote instances of the Dicoogle Platform seamlessly, such as, Query or Retrieval of studies.

#### Configuring Plugins

  Plugin configurations are accessible via "/DicoogleDir/Plugins/settings/PluginName.xml", where PluginName stands for the name of the plugin.
  Upon initialization, if no configurations file is supplied the Dicoogle Platform creates one with the default values.

#### Using the Web interface

  * Configuring Services

      In the Management Page, Services and Plugins settings, it is possible to start and/or stop currently running services in real time. Moreover, some configurations like the DICOM service ports may be set.

  * Index a Directory

      Indexing a directory is done simply by accessing the Management page, in the top right corner. 
      In the Management page, we are able to select which root directory we want to index (Dicoogle Drectory Monitorization). We may also enable the (Dicoogle Directory Watcher), this creates a daemon that listens for new files in the root Directory.
      After selecting the configurations, the "Apply Settings" button must be pressed.
      
      When the right settings are saved, the Start buttons fires the indexation process. Please note that this process may take considerable time to complete.

  * Using the Search Interface

      The search page enables users to execute queries over the indexed meta-data.
      The query syntax is similar to the Lucene's Tag:Value query format, but free text searches are also supported. For inexperienced users, an advanced input module may also be used.
      
      In the search interface, it is also possible to select which providers to query. Query providers are actually Query Plugins, that are installed either in the local instance of Dicoogle, or in remote instances if the platform is using the WAN plugin. Therefore, be careful and select exactly which providers you want to query, in order to retrieve more accurate and faster results.

  * Export Results 

      After running a query, the Query Browser shows up, giving the user an intuitive hierarchical view of the results. On this page, there is also an Export button, which is used in order to export the query results into a CSV file. When the export button is clicked, the user has to select which tags (s)he wants to export in the CSV file. This selection is heavily assisted by the interface, on which the user may type an incomplete tag and have presented the available candidates that match the inserted term. Moreover, the text box allows users to copy a list of tags directly from another CSV file, enabling an easier generation of reports.

#### Using the Web Services

  Let us assume that the Web Services for our instance of Dicoogle are running in http://demo.dicoogle.com/
    
  * Search by Date Range, Access images in date 2005/03/29

    ```URL: http://demo.dicoogle.com/search?query=StudyDate:[20050329 TO 20050329]```
        
  * Access images in date 2005/03/29 and CT (Computer Tomography) modality
        
    ```URL: http://demo.dicoogle.com/search?query=Modality:CT AND StudyDate:[20050329 TO 20050329]```

  * Access the list of attributes of an image (by uid)

    ```URL: http://demo.dicoogle.com/dump?uid=1.3.12.2.1107.5.1.4.54023.30000005032914013107800000965```

  * Free text search, looking for CT keyword

    ```URL http://demo.dicoogle.com/search?query=CT```

  * Get a DICOM File

    ```URL: http://demo.dicoogle.com/legacy/file?uid=1.3.12.2.1107.5.1.4.54023.30000005032914013107800000965```

  * Return documents from particular query providers (useful for queries that do not follow the typical Lucene query format)
  
    ```URL: http://demo.dicoogle.com/search?query=Modality:NM&provider=lucene&provider=mongo```
    ```
    Parameters:
      query : Query String
      provider: name of the query providers - multiple - optional
           all: default - asks all available providers.
           provider_name: name of the provider, ex lucene.
    ```

  * Force Dicoogle to index a given Resource. (useful when conventional notification systems (DICOM Services, DirectoryMonitoring, Human Interface) fail to start the index procedure)

  ```URL: http://demo.dicoogle.com/management/tasks/index?uri=file:///tmp/dataset-ieeta/```
  ```
  Method: POST
  Parameters:
    uri: URI of the resource to index
         The identifier of the resources that will be indexed. Please note that Dicoogle will fetch these resources from a storage plugin. Therefore, a plugin capable of handling these resources must be enabled.
  ```

  A live demo was deployed on the given url. Feel free to experiment with these services.

#### Create your own Plugins

  In order to integrate new functionality in Dicoogle, you may create your own PluginSet. A PluginSet is a set of plugins that developed with the intent of supporting a given functionality

## Development 

If you are in the development process and using the UI, you may need to change your configuration in the config.xml file, so as to enable cross-origin requests:

```
<server enable="true" port="8080" allowedOrigins="*" />
```

### Building Dicoogle 



 1. Retrieve the full source code from this repository: `git clone https://github.com/bioinformatics-ua/dicoogle.git`
 2. Navigate to the project's base directory, and build the parent Maven project by calling `mvn install`.
    - Note: if you want, you can skip the npm part: `mvn install -Dskip.npm`
 3. The resulting jar file can be found in "./dicoogle/target".


Web site
========

www.dicoogle.com

Authors
=======

* Luís Bastião
* Frederico Valente
* Eriksson Monteiro
* Tiago Godinho
* Eduardo Pinho
* Luis Ribeiro

Past developers:

* Samuel Campos
* Carlos Ferreira

Project leaders
===============

* Carlos Costa
* José Luis Oliveira

