require('./lib/jscolor.js');

$(function() {



	// account & session

	function setUser(response) {
		$('#account-action-buttons').css('display', 'none');
		$('#session-bar').css('display', 'block');
		$('#profile-pic').replaceWith((function() {
			var profPic = document.createElement('img');
			profPic.src = response.user.photoURL;
			profPic.id = 'profile-pic';
			return profPic;
		})());
		$('#display-name').html(response.user.displayName + '<span>' + response.user.email + '</span>');
		$('header p').css('display', 'none');
		$('main').css('display', 'block');
	}
	
	$('#login-with-google-button').click(function() {
		var provider = new firebase.auth.GoogleAuthProvider();
		firebase.auth().signInWithPopup(provider).then(function(response) {
			setUser(response);
		}).catch(function(error) {
		  var errorCode = error.code;
		  var errorMessage = error.message;
		  var email = error.email;
		  var credential = error.credential;
		});
	});
	
	$('#logout-button').click(function() {
		firebase.auth().signOut().then(function() {
			$('#account-action-buttons').css('display', 'block');
			$('#session-bar').css('display', 'none');
			$('main').css('display', 'none');
			$('header p').css('display', 'block');
		}, function(error) {
			console.log(error);
		});
	});
	
	
	
	
	
	var // ...
	
		b = $('#b'),
		g = $('#g'),
		a = $('#a'),
		x = $('#x'),
		y = $('#y'),
		z = $('#z');
	
	
	var streaming = true;
	
	var orientationIteration = 0;
	
	window.addEventListener('deviceorientation', function(e) {
		
		if(streaming) {
		
			if(orientationIteration % 5 === 0) {
		
				b.html(e.beta.toFixed(2));
				g.html(e.gamma.toFixed(2));
				a.html(e.alpha.toFixed(2));
		
				/*orientations.update({
					b : e.beta.toFixed(2),
					g : e.gamma.toFixed(2),
					a : e.alpha.toFixed(2)
				});*/
			
				orientationIteration = 0;
			
			}
		
			orientationIteration++;
		
		}
		
	}, false);
	
	var accelerationIteration = 0;
	
	window.addEventListener('devicemotion', function(e) {
		
		if(streaming) {
		
			if(accelerationIteration % 5 === 0) {
		
				x.html(e.acceleration.x.toFixed(2));
				y.html(e.acceleration.y.toFixed(2));
				z.html(e.acceleration.z.toFixed(2));
		
				/*accelerations.update({
					x : e.acceleration.x.toFixed(2),
					y : e.acceleration.y.toFixed(2),
					z : e.acceleration.z.toFixed(2)
				});*/
			
				accelerationIteration = 0;
			
			}
		
			accelerationIteration++;
			
		}
		
	}, false);
	
	$('#stop-start-streaming-button').click(function() {
		$(this).html(streaming ? 'start streaming' : 'stop streaming');
		streaming = !streaming;
		console.log(streaming);
	});

});