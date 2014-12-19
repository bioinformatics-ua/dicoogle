dicoogle
========

Project  Medical Imaging Repositories using Indexing System and P2P mechanisms



Brief Documentation
========


#### Setup Dicoogle Platform Environment
  1. Copy Dicoogle.jar to the installation folder. For example DicoogleDir/
  2. Create the Folder DicoogleDir/Plugins.

      This folder will hold the plugins used by our instance of the Dicoogle Platform.
  3. Next, copy the desired plugins into the DicoogleDir/Plugins Folder.
      
      The typical setup of Dicoogle involves the deployment of an Index and a Query Plugin. 
      We supply an implementation of such plugin based in the Lucene Search Engine (Lucene-plugin.jar). 
  4. Run Dicoogle.
  
      Dicoogle may be run as a server: ```java -jar Dicoogle.jar -s```

      or using the RMI Graphical Interface: ```java -jar Dicoogle.jar```

#### Available Plugins
  * Lucene Index/Query Plugin - (Lucene.jar)
      
      Plugin Based on lucene to support index and query of DICOM Meta-data.
  * File Storage Plugin - (filestorage.jar)

      Plugin used in the storage of DICOM Files. This plugin is necessary in order to use Dicoogle as a DICOM Storage Provider.
      
      Our file storage plugin maps the DICOM hierarchical organization (Patient->Study->Series->Image) into a directory tree in the file system. Every object in the Dicoogle Platform may be traced back to its storage location by an URI, similar to file:///tmp/file. In order to support multiple providers, every Storage plugin must define a unique schema, i.e protocol. 
      
      * Settings Parameters
      
        * root-dir: is the root directory where DICOM Files will be stored
        *  schema: Specifies the schema/protocol of the file plugin. This value is arbitrary, however it must be unique, as such avoid using well known protocol names such as http or file.

  * Dicoogle Wan Plugin - (wan-plugin.jar)

      The WAN Plugin may be used to federate instances of the Dicoogle Repository. The google app engine is used to federate and relay communications between the multiple instances.
      It is possible to call reservices on remote instances of the Dicoogle Platform seamlessly, such as, Query or Retrieval of studies.

#### Configuring Plugins

  Plugins configurations are accessible via /DicoogleDir/Plugins/settings/PluginName.xml. Where PluginName stands for the name of the plugin.
  Upon initialization, if no configurations file is supplied the Dicoogle Platform creates one with the default Values.

#### Using the Web interface

  * Configuring Services

      In the Management Page, Services and Plugins settings, it is possible to stard and/or stop plugins in real time. Moreover, some configurations like the web-services port or the interface port may be set.

  * Index a Directory

      Indexing a directory is done simply by accessing the Management page, in the top right corner. 
      In the Management page, we are able to select which root directory we want to index (Dicoogle Drectory Monitorization). We may also enable the (Dicoogle Directory Watcher), this creates a daemon that listens for new Files in the root Directory.
      After selecting the configurations, the apply settings button must be pressed.
      
      When the right settings are saved, the Start buttons fires the process of indexing. Please note that this process may take considerable time, as such, be patient until the progress bar fill up.

  * Using the Search Interface

      The search page enables users to execute queries about the indexed meta-data.
      The query syntax is similar to the Lucene's, Tag:Value. There are numerous tutorials online explaining this format. For inexperienced users, and advanced input module may also be used.
      
      In the search interface it is also possible to selected which providers to query. Query providers are actually Query Plugins, that are installed either in the local instance of Dicoogle, or in remote instances if the platform is using the WAN-Plugin. Therefore, be careful and selected exactly which providers to you want to query, in order to retrieve more accurate and faster results.

  * Export Results 

      After running a query, the Query Browser shows up, giving the user an intuitive hierarchical view of the results. On the button of the Browser there is the export button. The export button is used in order to export the query results into a CSV file. When the export button is clicked, the user has to select which Tags he wants to export in the CSV file. This selection is heavily assisted by the interface, on which the user may type an incomplete tag and have presented the available candidates that match the inserted term. Moreover, the text box allows users to copy a list of tags directly from another CSV file, enabling easy generation of reports.


#### Using the Web Services

  Lets assume that the Web Services for our instance of Dicoogle are running in http://www.dicoogle.com/services/dws/
    
  * Search by Date Range, Access images in date 2005/03/29:

    ```URL: http://www.dicoogle.com/services/dws/dim?advq=StudyDate:[20050329 TO 20050329]```
        
  * Access images in date 2005/03/29 and Modality CT (Computer Tomography):
        
    ```URL: http://www.dicoogle.com/services/dws/dim?advq=Modality:CT AND StudyDate:[20050329 TO 20050329]```

  * Access parameters for each image (use uid):

    ```URL: http://www.dicoogle.com/services/dws/dump?uid=1.3.12.2.1107.5.1.4.54023.30000005032914013107800000965```

  * Search Free Text, Looking for CT keyword:

    ```URL http://www.dicoogle.com/services/dws/dim?q=CT```

  * Get a DICOM File

    ```URL: http://www.dicoogle.com/services/dws/file?uid=1.3.12.2.1107.5.1.4.54023.30000005032914013107800000965```

  * Query Return Raw Documents in JSON Format (useful for queries on indexers that do not follow DIM)
  
    ```URL: http://localhost:6060/dim?advq=Modality:NM&type=raw&provider=lucene&provider=mongo```
    ```
    Parameters:
      advq : Query String
      type: Type of rendering used to format results : opticional
           dim: default - uses the DICOM DIM Model - Output --> XML
           raw: Renders the full search results in JSON format Output --> JSON
      provider: name of the query providers - multiple - optional
           all: default - asks all available providers.
           provider_name: name of the provider, ex lucene.
    ```

  * Force DICOOGLE to index a given Resource. (useful when conventional notification systems (DICOM Services, DirectoryMonitoring, Human Interface) fail to start the index procedure)

  ```URL: http://localhost:6060/doIndex?uri=file:///tmp/dataset-ieeta/```
  ```
  Parameters:
    uri: URI of the resource to index - multiple
         The identifier of the resources that will be indexed. Please note that DICOOGLE will fectch these resources from a StoragePlugin. Therefore, a plugin capable of handling these resources must be enabled.
  ```

  These examples are a live demo, fell free to access these services.

#### Create your own Plugins

  In order to integrate new functionality in Dicoogle, you may create your own PluginSet. A PluginSet is a set of plugins that developed with the intent of supporting a given functionality




Web site
========

www.dicoogle.com


Authors
========

* Luís Bastião
* Frederico Valente
* Luís Ribeiro
* Carlos Ferreira
* Eriksson Monteiro
* Tiago Godinho
* Samuel Campos



Project leaders
========

* Carlos Costa
* José Luis Oliveira

