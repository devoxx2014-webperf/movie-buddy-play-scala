package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable.{Map,SynchronizedMap, HashMap}
import play.api.libs.json
import scala.io.Source
import play.api.libs.json.Writes._
import scala.collection.mutable.Set

case class Movie(id: Int, title: String, actors: String, poster: String, genre: String)
//case class Movie(id: Int, title: String, actors: String, poster: String, genre: String, year: String, rated: String, released: String, 
//	runtime: String, director: String, writer: String, plot: String, language: String, country: String, awards: String, metascore: String, imdbRating: String, imdbVotes: String, imdbID: String)


case class User(id: Int, name: String) 


case class Rate(user: User, movie: Movie, rate: Int) {
	override def equals(obj: Any): Boolean = {
		obj match {
			case rate:Rate => user.equals(rate.user) && movie.equals(rate.movie)
			case _ => false
		}
		
	}

	override def hashCode = user.hashCode + movie.hashCode
}




object JsonReader {

	implicit val userFormat = (
    	(__ \ "_id").format[Int] ~
      	(__ \ "name").format[String] 
    )(User.apply, unlift(User.unapply))

    
    implicit val movieFormat = (
    	(__ \ "_id").format[Int] ~
      	(__ \ "Title").format[String] ~
      	(__ \ "Actors").format[String] ~
      	(__ \ "Poster").format[String] ~
      	(__ \ "Genre").format[String] /*~

		(__ \ "Year").format[String] ~
      	(__ \ "Rated").format[String] ~
      	(__ \ "Released").format[String] ~
      	(__ \ "Runtime").format[String] ~
      	(__ \ "Director").format[String] ~
      	(__ \ "Writer").format[String] ~
      	(__ \ "Plot").format[String] ~
      	(__ \ "Language").format[String] ~
      	(__ \ "Country").format[String] ~
      	(__ \ "Awards").format[String] ~
      	(__ \ "Metascore").format[String] ~
      	(__ \ "imdbRating").format[String] ~
      	(__ \ "imdbVotes").format[String] ~
      	(__ \ "imdbID").format[String]     */
      
    )(Movie.apply, unlift(Movie.unapply))

}

case class Reader[A](users:Seq[A] = Seq()) {
	def get() = users
}

object Reader {

	def apply[A](filePath:String)(implicit reader:Reads[A]) = {

		import play.api.Play.current
		val usersSource = Source.fromInputStream(Play.resourceAsStream(filePath).get).getLines().mkString
  
    	val jsonUsers = Json.parse(usersSource)
    	val users = Json.fromJson[Seq[A]](jsonUsers).get
    	new Reader(users)
	}

}


object Repository {
	import models.JsonReader._

	val users = Reader[User]("users.json").get()
	val movies = Reader[Movie]("movies.json").get()

	var rates = Set[Rate]()
		
}