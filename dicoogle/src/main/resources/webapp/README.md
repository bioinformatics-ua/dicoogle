# Dicoogle Web Application

## Building

The web application is built using Node.js, npm, and webpack (see [package.json](package.json)). To build everything for production (ready to be bundled for when creating dicoogle.jar):

    npm install     # install npm dependencies
    npm run build   # build everything for production

To build all js, styles and html resources:

    npm run build        # for production
    npm run build-debug  # for development

To build and watch for changes, you can run the webpack development server via the `start` script:

    npm start

## Debugging the webapp

The web application can be tested separately without having it embedded in a jar file. The steps are simple:

1. Start Dicoogle, locally or on a server: `java -jar dicoogle.jar -s`. The jar file does not need to contain the web application in this case. You may also need to change your configuration in the `confs/server.xml` file, so as to enable cross-origin requests:

```xml
<web-server autostart="true" port="8080">
  <allowed-origins>*</allowed-origins>
</web-server>
```

2. Navigate to the webapp's source code. Define the URL to Dicoogle's base endpoint using the `DICOOGLE_BASE_URL` environment variable, and run the webpack development server:

```
DICOOGLE_BASE_URL=http://localhost:8080 npm start
```

This will deploy a server with automatic refresh on changes.

You can also run one of the other scripts available and deploy a static server on the webapp's base folder:

```
DICOOGLE_BASE_URL=http://localhost:8080 npm run build-debug
./run_server
```

See the **Building** section above for more scripts.

## Running as a standalone server

Running `npm start` is the recommended way to serve the webapp for development purposes.
But if you wish to serve the webapp separately from Dicoogle,
you can build it against a different Dicoogle endpoint (see above)
and use any other static HTTP server to serve this folder.
