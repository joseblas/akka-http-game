package uk.gov


import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContextExecutor


case class Result(json: String)

trait Protocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val result = jsonFormat1(Result.apply)
}

trait Router extends Protocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  val routes = processMultiPartData

  val config = ConfigFactory.load()



  def processMultiPartData: Route = cors() {
    get {
      pathSingleSlash {
        complete(List(1, 2, 3))
      } ~
        path("json") {
          complete("json bourne")
        } ~
        path("plaintext") {
          complete("Hello, World!")
        } ~
        path(Segment) { string =>
          complete(string)
        }
    }



  }

}
