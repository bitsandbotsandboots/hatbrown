module.exports = function(gulp, plugins) {
	return function() {		
		gulp.src([ 'src/index.html' ])
		.pipe(gulp.dest('dev'))
  		.pipe(plugins.connect.reload());
	}
};