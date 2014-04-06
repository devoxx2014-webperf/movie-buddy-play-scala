function getScripts(){ return [
  
  "assets/javascripts/models/MovieModel.js",
  "assets/javascripts/models/MoviesCollection.js",
  "assets/javascripts/models/RateModel.js",
  "assets/javascripts/models/RatesCollection.js",
  "assets/javascripts/models/UserModel.js",
  "assets/javascripts/models/UsersCollection.js",
  
  
  "assets/javascripts/components/ApplicationTab.js",
  "assets/javascripts/components/MoviesTable.js",
  "assets/javascripts/components/UsersTable.js",
  "assets/javascripts/main.js"
];};

getScripts().forEach(function(s){
	var script = document.createElement('script');
	script.src = s;
	script.type = "text/jsx";
	document.querySelector('head').appendChild(script);
});


