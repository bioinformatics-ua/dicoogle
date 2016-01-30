# Dicoogle Web Application

## Building

`npm` version 2 or earlier is required. To build everything for production (ready to be bundled for when creating dicoogle.jar):

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

If the webapp shows an error due to the `dicoogle-webcore` package, simply navigate to the webcore package
(at the base of the project: "./webcore"), and run `npm install` there. Then try building the webapp again.

## Running as a standalone server

    ./run_server
