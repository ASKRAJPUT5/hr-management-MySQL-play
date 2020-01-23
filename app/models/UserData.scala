package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}




@Singleton
class UserData @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import slick.jdbc.MySQLProfile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "people1") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)


    def name = column[String]("name")

    def phone = column[Int]("phone")

    def email = column[String]("email")

    def age = column[Int]("age")


    def * = (id, name, phone, email, age) <> ((User.apply _).tupled, User.unapply)
  }

  private val allUsers = TableQuery[UserTable]

  def createUser(name: String, phone: Int, email: String, age: Int): Future[User] = dbConfig.db.run(
    (allUsers.map(p => (p.name, p.phone, p.email, p.age))

      //We return the id, so as to know which id was generated for each user, also we define a transformation for the
      //returned value which combines our parameters with the returned id
      returning allUsers.map (_.id)
      into ((nameP, id) => User(id, nameP._1, nameP._2, nameP._3, nameP._4))
      //this is where we insert a user into the database
      ) += (name, phone, email, age))



  def show(): Future[Seq[User]] = dbConfig.db.run(allUsers.result)

  def findById(id : Long): Future[Seq[User]] = {
    dbConfig.db.run(allUsers.filter(_.id === id).result)
  }

  def update (id: Long, name: String, phone: Int, email: String, age:Int) : Future[Int] ={
    dbConfig.db.run(allUsers.filter(_.id === id).map(p => (p.name,p.phone,p.email,p.age)).update(name,phone,email,age))
  }

  def delete(id : Long): Future[Int] = {
    dbConfig.db.run(allUsers.filter(_.id === id).delete)
  }
}