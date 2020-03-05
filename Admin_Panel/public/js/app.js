// Initialize Firebase
var config = {
    apiKey: "AIzaSyB6sjJngt2829mIRS4Cekx-v1vfpB01W48",
    authDomain: "wallpaperapp-89c1c.firebaseapp.com",
    databaseURL: "https://wallpaperapp-89c1c.firebaseio.com",
    projectId: "wallpaperapp-89c1c",
    storageBucket: "wallpaperapp-89c1c.appspot.com",
    messagingSenderId: "640918109745"
  };
  
  firebase.initializeApp(config);

    //   If browser is closed, login session is stored
    firebase.auth.Auth.Persistence.LOCAL;

// User clicks on the button
$('#btn-login').click(function() {
    //  Email and Password IDs
    var email = $('#email').val();
    var password = $('#password').val();

    //  Authenticate the user
    var result = firebase.auth().signInWithEmailAndPassword(email, password);

    // If there is an error
    result.catch(function(error) {
        var errorCode = error.code;
        var errorMessage = error.message;

        //  Display the error message on console
        console.log(errorCode);
        console.log(errorMessage);
    });
});

$("#btn-logout").click(function() {
    firebase.auth().signOut();
});

function switchView(view) {
    $.get({
        url: view,
        cache: false,
        // data - the html
    }).then(function(data) {
        $('#container').html(data);
    })
}