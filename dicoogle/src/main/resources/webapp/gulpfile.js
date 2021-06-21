'use strict';

var browserify = require('browserify');
var buffer = require('vinyl-buffer');
var eslint = require('gulp-eslint');
var gulp = require('gulp');
var log = require('fancy-log');
var processhtml = require('gulp-processhtml');
var rename = require('gulp-rename');
var rm = require('gulp-rm');
var sass = require('gulp-dart-sass');
var source = require('vinyl-source-stream');
var sourcemaps = require('gulp-sourcemaps');
var uglify = require('gulp-uglify');
var watchify = require('watchify');

require('core-js/fn/object/assign');

var EXTERNAL_REQUIRES = [
    'react', 'react-router', 'reflux', 'dicoogle-webcore', 'dicoogle-client',
    'react-bootstrap', 'react-router-bootstrap', 'react-bootstrap-table', 'react-imageloader', "react-dom"];

function createBrowserify(debug, watch) {
  // set up the browserify instance on a task basis
  var b = browserify('./js/app.js', {
    cache: {},
    packageCache: {},
    extensions: ['.jsx'],
    debug: debug,
    transform: [
      [
        'babelify', {
          presets: ['es2015', 'react']
        }
      ],
      [
        'envify', {
          _: 'purge',
          global: true,
          NODE_ENV: debug ? 'development' : 'production'
        }
      ]
    ]
  });
  if (watch) {
    b.plugin(watchify);
  }
  return b.require(EXTERNAL_REQUIRES);
}

gulp.task('production-env', function() {
    process.env.NODE_ENV = 'production';
});

gulp.task('lint', function () {
  return gulp.src(['js/**/*.js', 'js/**/*.jsx'])
    .pipe(eslint({
      configFile: ".eslintrc"
    }))
    .pipe(eslint.format())
    .pipe(eslint.failAfterError());
});

function handleBundlingError(e) {
  log('' + e);
}

gulp.task('js', ['lint'], function () {
  return createBrowserify(false, false)
    .bundle()
    .on('error', handleBundlingError)
    .pipe(source('bundle.min.js'))
    .pipe(buffer())
    .pipe(uglify({compress: {
      dead_code: true,
      drop_console: true,
      warnings: false
      }, mangle: true}))
    .pipe(gulp.dest('lib'));
});

gulp.task('js-debug', ['lint'], function () {
  return createBrowserify(true, false)
    .bundle()
    .on('error', handleBundlingError)
    .pipe(source('bundle.js'))
    .pipe(buffer())
    .pipe(sourcemaps.init({loadMaps: true}))
    .pipe(sourcemaps.write('./'))
    .pipe(gulp.dest('lib'));
});

gulp.task('js:watch', function () {

  var b = createBrowserify(true, true);
  b.on('update', bundle); // on any dep update, runs the bundler
  b.on('log', log); // output build logs to terminal

  function bundle() {
    return b.bundle()
      .on('error', handleBundlingError)
      .pipe(source('bundle.js'))
      .pipe(buffer())
      .pipe(sourcemaps.init({loadMaps: true})) // loads map from browserify file
        // Add transformation tasks to the pipeline here.
      .pipe(sourcemaps.write('./')) // writes .map file
      .pipe(gulp.dest('lib'));
  }
  bundle();
});

gulp.task('html', function () {
  // use processhtml
  return gulp.src('index-template.html')
    .pipe(processhtml({
      environment: "dist",
      strip: true
    }))
    .pipe(rename('index.html'))
    .pipe(gulp.dest('.'));
});

gulp.task('html-debug', function () {
  // use processhtml
  return gulp.src('index-template.html')
    .pipe(processhtml({
      environment: "dev"
    }))
    .pipe(rename('index.html'))
    .pipe(gulp.dest('.'));
});

gulp.task('css', function () {
  // use sass
  return gulp.src('sass/dicoogle.scss')
    .pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
    .pipe(gulp.dest('css'));
});

gulp.task('css-debug', function () {
  // use sass
  return gulp.src('sass/dicoogle.scss')
    .pipe(sourcemaps.init())
    .pipe(sass().on('error', sass.logError))
    .pipe(sourcemaps.write())
    .pipe(gulp.dest('css'));
});

gulp.task('css:watch', function () {
  gulp.watch('sass/**/*.scss', ['css-debug']);
});

gulp.task('production', ['production-env', 'js', 'html', 'css']);
gulp.task('development', ['js-debug', 'html-debug', 'css-debug']);

gulp.task( 'clean', function() {
  return gulp.src(['lib/bundle.*', 'css/dicoogle.css*', 'index.html'], { read: false })
    .pipe( rm() );
});

gulp.task('default', ['production']);
