package models


import play.api.libs.json.Json

case class User (id: Long, name: String, phone: Int, email: String, age: Int)
object User {
  implicit val userJsonFormat = Json.format[User]
}