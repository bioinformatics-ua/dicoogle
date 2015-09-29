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
    }
  });

  // Load plugin tasks.
  grunt.loadNpmTasks('grunt-eslint');

  // Default task(s).
  grunt.registerTask('test', ['eslint']);

};
