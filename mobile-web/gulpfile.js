	
	
	var // require gulp & its plugins
	
		gulp = require('gulp'),
		plugins = require('gulp-load-plugins')();
	
	
	gulp.task('serve', require('./tasks/serve.js')(gulp, plugins));
	
	gulp.task('html', require('./tasks/html.js')(gulp, plugins));

	gulp.task('scss', require('./tasks/scss.js')(gulp, plugins));
	
	gulp.task('js', require('./tasks/js.js')(gulp, plugins));

	// lint, browserify, rename, copy to different directories, minify, etc.
	// gulp.task('javascript', require('./gulp-tasks/javascript.js')(gulp, plugins));

	// live reloading
	gulp.task('watch', require('./tasks/watch.js')(gulp, plugins));

	// call all development-stage tasks
	gulp.task('default', [ 'html', 'scss', 'js', 'serve', 'watch' ]);