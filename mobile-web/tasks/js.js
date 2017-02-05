module.exports = function(gulp, plugins) {

	var // ...
		stylish = require('jshint-stylish'),
		browserify = require('browserify'),
		source = require('vinyl-source-stream');
	
	return function() {	
	
		gulp.src([
			'src/**/*.js',
			'!src/bundle.js',
			'!src/js/lib/**/*'
		])
		.pipe(plugins.jshint())
		.pipe(plugins.jshint.reporter(stylish));	
		
		browserify('src/js/init.js')
		.bundle()
		.pipe(source('bundle.js'))
		.pipe(gulp.dest('dev'))
		.pipe(plugins.connect.reload());
		
	};
	
};