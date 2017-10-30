import akka.actor._
import akka.util.Timeout
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}
import spray.routing._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class RestInterface(hosts:Array[String]) extends HttpServiceActor
  with RestApi {

  otherHosts = hosts
  println("This are the other hosts -> " + otherHosts(0) + " <--> " + otherHosts(1))
  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>

  implicit val timeout = Timeout(10 seconds)
  implicit val ec = actor.context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  var otherHosts : Array[String] = Array("")
  var db = ""

  def routes: Route =
    post {
      path("send") {
        entity(as[String]) { data => requestContext =>
          db = data
          otherHosts.foreach(x => pipeline(Post("http://" + x + "update",data)))
          requestContext.complete(StatusCodes.Accepted)
        }
      }~
        path("update"){
          entity(as[String]) { data => requestContext =>
            db = data
            requestContext.complete(StatusCodes.Accepted)
          }
        }
    }~
      path("get") {
        get { requestContext =>
          requestContext.complete(db)
        }
      }
}