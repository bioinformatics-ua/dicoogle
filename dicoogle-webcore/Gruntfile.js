module.exports = function(grunt) {

  var globals = {
    "document": false,
    "console": false,
    "define": false,
    "HTMLDivElement": false,
    "XMLHttpRequest": false,
    "XDomainRequest": false
  };

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    jshint: {
      all: ['Gruntfile.js', '<%= pkg.name %>.js'],
      options: {
        esnext: true,
        globals: globals
      }
    },
    babel: {
      options: {
      },
      all: {
        src: '<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.js'
      }
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
        src: 'build/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.min.js'
      },
      pretty: {
        options: {
          beautify: true,
          compress: false,
          mangle: false,
          preserveComments: true,
          sourceMap: true
        },
        src: 'build/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.js'
      }
    }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-babel');
  grunt.loadNpmTasks('grunt-contrib-uglify');

  // Default task(s).
  grunt.registerTask('default', ['jshint','babel','uglify:minimize','uglify:pretty']);

};
