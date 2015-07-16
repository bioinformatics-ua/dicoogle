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
    eslint: {
      gruntfile: 'Gruntfile.js',
      main: 'dicoogle-webcore.js'
    },
    babel: {
      options: {
      },
      all: {
        src: '<%= pkg.name %>.js',
        dest: 'build/<%= pkg.name %>.js'
      }
    }, 
    umd: {
      options: {
        amdModuleId: 'dicoogle-webcore',
        globalAlias: 'DicoogleWebcore',
        template: 'unit'
      },
      all: {
        src: 'build/<%= pkg.name %>.js',
        dest: 'build/module.js'
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
        src: 'build/module.js',
        dest: 'dist/<%= pkg.name %>.min.js'
      },
      pretty: {
        options: {
          beautify: true,
          compress: false,
          mangle: false,
          preserveComments: true,
          sourceMap: true
        },
        src: 'build/module.js',
        dest: 'dist/<%= pkg.name %>.js'
      }
    }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-eslint');
  grunt.loadNpmTasks('grunt-babel');
  grunt.loadNpmTasks('grunt-umd');
  grunt.loadNpmTasks('grunt-contrib-uglify');

  // Default task(s).
  grunt.registerTask('default', ['eslint','babel','umd','uglify:minimize','uglify:pretty']);

};
