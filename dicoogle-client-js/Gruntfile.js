module.exports = function(grunt) {

  var globals = {
    "console": false,
    "define": false,
    "module": false,
    "exports": true,
    "XMLHttpRequest": false,
    "XDomainRequest": false
  };

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    replace: {
      browser: {
        src: ['<%= pkg.name %>.js'],      // source files array (supports minimatch)
        dest: 'browser/<%= pkg.name %>.js',  // destination directory or file
        replacements: [{
          from: /var\s+service_request\s*;?\s*(\/\/.*)?/,  // regex replacement
          to: '<%= grunt.file.read("servicerequest-browser.js") %>'
        }]
      },
      node: {
        src: ['<%= pkg.name %>.js'],      // source files array (supports minimatch)
        dest: 'node/<%= pkg.name %>.js',  // destination directory or file
        replacements: [{
          from: /var\s+service_request\s*;?\s*(\/\/.*)?/,  // regex replacement
          to: '<%= grunt.file.read("servicerequest-node.js") %>'
        }]
      }
    },
    jshint: {
      all: ['Gruntfile.js', '<%= pkg.name %>.js', 'browser/<%= pkg.name %>.js', 'node/<%= pkg.name %>.js'],
      options: {
        globals: globals
      }
    },
    browserify: {
      standalone: {
        src: [ './browser/<%= pkg.name %>.js' ],
        dest: './browser/build/<%= pkg.name %>.js',
        options: {
          browserifyOptions: {
            standalone: '<%= pkg.name %>'
          }
        }
      },
    },
    uglify: {
      options: {
        banner: '<%= grunt.file.read("license-header.txt") %>'
      },
      minimize: {
        options: {
          compress: true,
          preserveComments: false,
          mangle: true
        },
        src: './browser/build/<%= pkg.name %>.js',
        dest: './browser/build/<%= pkg.name %>.min.js'
      },
      pretty: {
        options: {
          compress: false,
          mangle: false,
          preserveComments: true,
          sourceMap: true
        },
        src: 'browser/build/<%= pkg.name %>.js',
        dest: 'browser/build/<%= pkg.name %>.js'
      }
    }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-text-replace');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  
  // Default task(s).
  grunt.registerTask('default', [
    'replace',
    'jshint',
    'browserify',
    'uglify:minimize','uglify:pretty']);

};
