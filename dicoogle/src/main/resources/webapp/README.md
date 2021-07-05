# Dicoogle Web Application
## Building

Building the webapp of Dicoogle 2 requires Node.js 10,
the final version of which is `10.24.1`.
If necessary, it can be obtained with a Node.js version manager (nvm)
or at the official [Node.js website](https://nodejs.org/dist/latest-v10.x/).
npm version 3 or higher is required, npm 6 is recommended.

To build everything for production (ready to be bundled for when creating dicoogle.jar):

    npm install

To build all js and html resources:

    npm run debug        # for development
    npm run build        # for production

To build just the css files:

    npm run css

To watch for changes in JavaScript resources (good for building while developing):

    npm run js:watch

To watch for changes in the SASS resources (thus building css):

    npm run css:watch

Everything is build for production (js, html and css) in the prepublish script (this is also run automatically for `npm install`):

    npm run-script prepublish

All of these npm scripts map directly to gulp tasks:

```bash
$ gulp --tasks

 ├── lint
 ├─┬ js
 │ └── lint
 ├─┬ js-debug
 │ └── lint
 ├── js:watch
 ├── html
 ├── html-debug
 ├── css
 ├── css-debug
 ├── css:watch
 ├─┬ production
 │ ├── js
 │ ├── html
 │ └── css
 ├─┬ development
 │ ├── js-debug
 │ ├── html-debug
 │ └── css
 ├── clean
 └─┬ default
   └── production
```

## Running as a standalone server

We have included a script for running a static server containing the standalone webapp. If you already have Python installed, simply execute:

    ./run_server

## Debugging the webapp

The web application can be tested separately without having it embedded in a jar file. The steps are simple:

1. Start Dicoogle, locally or on a server: `java -jar dicoogle.jar -s`. The jar file does not need to contain the web application in this case. You may also need to change your configuration in the config.xml file, so as to enable cross-origin requests:

```xml
<server enable="true" port="8080" allowedOrigins="*" />
```

2. Navigate to the webapp's source code. Define the URL to Dicoogle's base endpoint using the `DICOOGLE_BASE_URL` environment variable, and bundle the source code: `DICOOGLE_BASE_URL=http://localhost:8080 npm run debug`. See the **Building** section above for more scripts.

3. Start a static server on the web application's base folder. If you have Python, the `run_server` script will do.

4. Open your browser and navigate to the static server: http://localhost:9000
