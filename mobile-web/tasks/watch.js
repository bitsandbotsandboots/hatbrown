module.exports = function(gulp, plugins) {
	return function() {
    	gulp.watch([ 'src/index.html' ], [ 'html' ]);
    	gulp.watch([ 'src/**/*.scss' ], [ 'scss' ]);
    	gulp.watch([
    		'src/**/*.js',
			'!src/bundle.js'
    	], [ 'js' ]);
	}
};
