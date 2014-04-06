package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable.{Map,SynchronizedMap, HashMap}
import play.api.libs.json
import scala.io.Source
import play.api.libs.json.Writes._
import scala.collection.mutable.Set

case class Movie(id: Int, title: String, actors: String, genre: String)


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
      	(__ \ "Genre").format[String] 
    )(Movie.apply, unlift(Movie.unapply))

}

case class Reader[A](users:Seq[A] = Seq()) {
	def get() = users
}

object Reader {

	def apply[A](filePath:String)(implicit reader:Reads[A]) = {

		val usersSource = Source.fromFile(filePath).getLines().mkString
  
    	val jsonUsers = Json.parse(usersSource)
    	val users = Json.fromJson[Seq[A]](jsonUsers).get
    	new Reader(users)
	}

}


object Repository {
	import models.JsonReader._

	val users = Reader[User]("conf/users.json").get()
	val movies = Reader[Movie]("conf/movies.json").get()

	var rates = Set[Rate]()
		
}