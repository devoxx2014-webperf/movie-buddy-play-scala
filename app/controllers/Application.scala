package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.libs.json
import play.api.libs.json._
import models.JsonReader._
import java.util.regex.Pattern
import play.api.libs.iteratee._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws._
import java.io._
import java.util.regex._

object Application extends Controller {

  def getImage(url:String) =  Action.async {
	    WS.url(url).withHeaders("referer" -> "localhost").get().map(response => {
		    val asStream: InputStream = response.ahcResponse.getResponseBodyAsStream
		    Ok.chunked(Enumerator.fromStream(asStream))
		  })
	}

  def index = Action {

  	Ok(views.html.index("Movie buddy"))
  }

  def search = Action {

  	Ok(views.html.search("Movie buddy"))
  }
	
  def users = Action {
  	// Stream the content in chuncks
  	// 	SimpleResult(
	 //     header = ResponseHeader(200),
	 //     body = Enumerator.fromStream(Play.resourceAsStream("users.json").get)
	 // ).as("application/json")   	 
  	Ok.chunked(Enumerator.fromStream(Play.resourceAsStream("users.json").get)).as("application/json") 
 
  }

  def userById(id: Int) = Action {
  	 val user:Option[User] = Repository.users.find(u => u.id == id)
  	 user match {
  	 	case Some(user) => Ok(Json.toJson(user))
  	 	case None => NotFound("user " + id + " not found")
  	 }
  }

  def userByName(name: String, limit: Int) = Action {
  	val pattern = Pattern.compile(name.toLowerCase);
    val users:Seq[User] = name match {
  		case "*" => Repository.users
                .take(limit)
        case _ => Repository.users
                .filter(user =>  pattern.matcher(user.name.toLowerCase).find())
                .take(limit)
  	}
    Ok(Json.toJson(users))           
  }

 def movies = Action {
  	Ok.chunked(Enumerator.fromStream(Play.resourceAsStream("movies.json").get)).as("application/json") 
  }

  def movieById(id: Int) = Action {
  	val movie:Option[Movie] = Repository.movies.find(u => u.id == id)
  	movie match {
  	 	case Some(movie) => Ok(Json.toJson(movie))
  	 	case None => NotFound("movie " + id + " not found")
  	 }
   }

  def movieByTitle(title: String, limit: Int) = Action {
  	val pattern = Pattern.compile(title.toLowerCase);
    val movies:Seq[Movie] = title match {
  		case "*" => Repository.movies
                .take(limit)
        case _ => Repository.movies
                .filter(movie => pattern.matcher(movie.title.toLowerCase).find())
                .take(limit)
  	}
    Ok(Json.toJson(movies))           
  }

  def movieByActor(actors: String, limit: Int) = Action {
  	val pattern = Pattern.compile(actors.toLowerCase);
    val movies:Seq[Movie] = actors match {
  		case "*" => Repository.movies
                .take(limit)
        case _ => Repository.movies
                .filter(movie => pattern.matcher(movie.actors.toLowerCase).find())
                .take(limit)
  	}
    Ok(Json.toJson(movies))       
  }

  def movieByGenre(genre: String, limit: Int) = Action {
  	val pattern = Pattern.compile(genre.toLowerCase);
    val movies:Seq[Movie] = genre match {
  		case "*" => Repository.movies
                .take(limit)
        case _ => Repository.movies
                .filter(movie => pattern.matcher(movie.genre.toLowerCase).find())
                .take(limit)
  	}
    Ok(Json.toJson(movies))       
  }

  def rates = Action { request =>
  	request.body.asJson.map { json => 
	   	val userId = (json \ "userId").as[Int]
	  	val movieId = (json \ "movieId").as[Int]
   		val rateStr = (json \ "rate").as[Int]

    	val user = Repository.users.find(u => u.id == userId.toInt)
    	val movie = Repository.movies.find(u => u.id == movieId.toInt)
    	val rate = rateStr.toInt

    	Repository.rates += Rate(user.get, movie.get, rate)
    	//Created("user " + user.get.name + " rated " + movie.get.title + " " + rate)    

		// Redirect to user ud
		Redirect(routes.Application.userById(userId.toInt))    	
    }.getOrElse {
      	BadRequest("Expecting Json data")
    }
  }

  def ratesByUser(userId: Int) = Action { 
  	val rates = Repository.rates.filter(r => r.user.id == userId).map(_.rate)
	Ok(Json.toJson(rates))
  }
 
  def shareRates(userOneId: Int, userTwoId: Int) = Action {
  		// Return the intesection between user1 and user2 movies
  		val user1Rates = Repository.rates.filter(r => r.user.id == userOneId)
  		val user2Rates = Repository.rates.filter(r => r.user.id == userTwoId)
  		val movies = user1Rates.map(_.movie).intersect(user2Rates.map(_.movie))
  		Ok(Json.toJson(movies))
  }

  def distance(userOneId: Int, userTwoId: Int) = Action {
  		// Return the intesection between user1 and user2 movies
  		val user1Rates = Repository.rates.filter(r => r.user.id == userOneId)
  		val user2Rates = Repository.rates.filter(r => r.user.id == userTwoId)
  		// for each rate in common, calculate the diff * diff
  		val x = user1Rates.toList.map { r1 =>
       		val rate2 = user2Rates.filter(_.movie == r1.movie)
        	rate2.toList.map { r2 => 
          		val diff = r1.rate - r2.rate
          		diff * diff
        	}
      	}.flatten
      	val result = 1.0 / (1.0 + Math.sqrt(x.sum))
  		Ok(result.toString)
  		
  }


}