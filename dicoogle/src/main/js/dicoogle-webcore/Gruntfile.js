module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    jshint: {
      all: ['Gruntfile.js', '<%= pkg.name %>.js'],
      options: {
        force: true,
        esnext: true
      }
    },
    babel: {
      options: {
      },
      build: {
        src: '<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.js'
      }
    }, 
    browserify: {
      options: {
        banner: '<%= grunt.file.read("license-header.txt") %>',
        browserifyOptions: {
          standalone: 'DicoogleWeb',
        }
      },
      buildDebug: {
        options: {
          browserifyOptions: {
            debug: true,
            standalone: 'DicoogleWeb',
          }
        },
        src:  'build/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>-debug.js'
      },
      build: {
        src:  'build/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.js'
      }
    },
    uglify: {
      options: {
        compress: true,
        banner: '<%= grunt.file.read("license-header.txt") %> */'
      },
      build: {
        src: 'build/<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.min.js'
      }
    }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-babel');
  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  

  // Default task(s).
  grunt.registerTask('default', ['jshint','babel','browserify:build','uglify:build']);
  grunt.registerTask('debug', ['jshint','babel','browserify:buildDebug']);

};
