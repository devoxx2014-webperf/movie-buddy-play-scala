
import play.api._
import play.api.mvc._
import models._
import scala.collection.mutable.Set

object Global extends GlobalSettings {

  override def onStart(app: Application) {

  	def findUserById(id: Int) = Repository.users.find(u => u.id == id)
	def findMoviesById(id: Int) = Repository.movies.find(m => m.id == id)

	val initStates = Set(
		(3022,        772,          2 ),
  		(3022,        24 ,          10),
  		(3022,        482,          4 ),
  		(3022,        302,          7 ),
  		(3022,        680,          6 ),
  		(9649,        772,          2 ),
  		(9649,        24 ,          8 ),
  		(9649,        482,          9 ),
  		(9649,        302,          3 ),
  		(9649,        556,          8 ),
  		(2349,        453,          7 ),
  		(2349,        461,          9 ),
  		(2349,        258,          10),
  		(2349,        494,          9 ),
  		(2349,        158,          4 ),
  		( 496,        682,          4 ),
  		( 496,        559,          7 ),
  		( 496,        537,          4 ),
  		( 496,        352,          3 ),
  		( 496,        	5,          9 )
  	)

	Repository.rates = initStates.map( value =>
		Rate(findUserById(value._1).get, findMoviesById(value._2).get, value._3)
	)
		
  
  }

}