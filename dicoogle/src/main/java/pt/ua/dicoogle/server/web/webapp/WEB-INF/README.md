# Dicoogle Web Application

## Installing for development

    npm install

## Building

To build all js and html resources:

    npm run-script build-debug  # for development
    npm run-script build        # for production

To watch for changes in the SASS resources (thus building css):

    ./run_sass

Everything is build for production (js, html and css) in the prepublish script (this is also run automatically for `npm install`):
   
   npm run-script prepublish

## Running as a standalone server

    ./run_server

