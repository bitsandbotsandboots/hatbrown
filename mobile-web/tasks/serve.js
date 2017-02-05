module.exports = function(gulp, plugins) {
	return function() {
		plugins.connect.server({
			root : 'dev',
			livereload : true
		});
	}
};