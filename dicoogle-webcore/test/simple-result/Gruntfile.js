module.exports = function(grunt) {

  var amdDependencies = ['dicoogle-webcore', 'react', 'reactable'];

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    react: {
      all: {
        src: '<%= pkg.name %>.jsx',
        dest: 'build/<%= pkg.name %>.js'
      }
    },
    jshint: {
      all: ['Gruntfile.js', 'build/<%= pkg.name %>.js'],
      options: {
      }
    },
    browserify: {
      all: {
        src: 'build/<%= pkg.name %>.js',
        dest: 'build/module.js',
        options: {
          browserifyOptions: {
            standalone: '<%= pkg.name %>'
          },
          external: ['react']
        }
      }
    },
//    umd: {
//      all: {
//        options: {
//          src: 'build/<%= pkg.name %>.js',
//          dest: 'build/module.js',
//          deps: {
//            amd: amdDependencies
//          },
//          template: 'template/returnModuleExports.hbs'
//          template: 'template/unit.hbs'
//        },
//      }
//    }, 
    uglify: {
      options: {
        //banner: '<%= grunt.file.read("license-header.txt") %>'
      },
      minimize: {
        options: {
          compress: true,
          preserveComments: false,
          mangle: true
        },
        src: 'build/module.js',
        dest: 'module.min.js'
      },
      pretty: {
        options: {
          beautify: true,
          compress: false,
          mangle: false,
          preserveComments: true
        },
        src: 'build/module.js',
        dest: 'module.js'
      },
      debug: {
        options: {
          beautify: true,
          compress: false,
          mangle: false,
          preserveComments: true,
          sourceMap: true
        },
        src: 'build/module.js',
        dest: 'module.js'
      }
    },
    clean: { all: ['build/'] }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-react');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-browserify');
  //grunt.loadNpmTasks('grunt-contrib-concat');
  //grunt.loadNpmTasks('grunt-umd');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-clean');

  // Default task(s).
  grunt.registerTask('default',
    ['react','jshint','browserify','uglify:minimize','uglify:pretty','clean']);
  grunt.registerTask('debug',
    ['react','jshint','browserify','uglify:debug']);
};
