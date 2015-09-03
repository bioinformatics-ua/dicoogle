# Dicoogle Web Application

## Installing for development

    npm install

## Building

To build all js and html resources:

    npm run debug        # for development
    npm run build        # for production

To build css files:

    npm run css

To watch for changes in JavaScript resources (when building while developing):

    npm run js:watch

To watch for changes in the SASS resources (thus building css):

    npm run css:watch

Everything is build for production (js, html and css) in the prepublish script (this is also run automatically for `npm install`):
   
   npm run-script prepublish

## Running as a standalone server

    ./run_server

