package controllers

import javax.inject._
import models.UserData
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(repo: UserData,
                               cc: MessagesControllerComponents
                              )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {


  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "phone" ->number,
      "email" -> email,
      "age" -> number.verifying(min(0), max(200))

    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }


  def index() = Action { implicit request =>
    Ok(views.html.index(userForm))
  }

  def addUser = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      //if there are no errors, we will create the user
      person => {
        repo.createUser(person.name, person.phone, person.email, person.age).map { _ =>
          Redirect(routes.UserController.index).flashing("success" -> "user has been created")
        }
      }
    )
  }

  def getUsers = Action.async { implicit request =>
    repo.show().map { people =>
      Ok(Json.toJson(people))
    }
  }

    def deleteUser(id: Long) = Action.async {
      repo.delete(id).map { _ =>
        Redirect(routes.UserController.index())
      }
    }
  /*def edit(id: Long) = Action.async { implicit request =>
    repo.findById(id).map { user =>
      Ok(views.html.edits(id, userForm.fill(user)))
    }.getOrElse(NotFound)
  }*/

  def edit(id : Long )= Action.async{ implicit request =>
    repo.findById(id).map { people =>
      Ok(Json.toJson(people))
    }
    }

  def update(id: Long, name: String, phone: Int, email: String, age: Int) = Action.async {
    implicit request =>
        repo.update(id, name, phone, email, age).map { _ =>
          Redirect (routes.UserController.index())
        }
  }
}

case class CreateUserForm(name: String, phone: Int, email: String, age: Int)
