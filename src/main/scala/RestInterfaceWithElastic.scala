import akka.actor._
import akka.util.Timeout
import org.json4s._
import org.json4s.jackson.JsonMethods._
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}
import spray.routing._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class RestInterfaceWithElastic extends HttpServiceActor
  with RestApiElastic {

  def receive = runRoute(routes)
}

trait RestApiElastic extends HttpService with ActorLogging { actor: Actor =>

  implicit val timeout = Timeout(10 seconds)
  implicit val ec = actor.context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  val elasticAddress = "http://localhost:9200/argus/argus/1"

  def routes: Route =
    pathPrefix("send") {
      post {
        entity(as[String]) { data => requestContext =>

          requestContext.complete(data)
          pipeline(Delete(elasticAddress)) onComplete{
            case Success(_) => {
              val a = pipeline(Post(elasticAddress, data))
              Await.result(a, Duration.Inf)
              requestContext.complete(StatusCodes.Accepted)
            }
            case Failure (_)=> requestContext.complete(StatusCodes.FailedDependency)
          }
        }
      }
    }~
      path("get") {
        get { requestContext =>
          pipeline(Get(elasticAddress)) onComplete {
            case Success (response) => {
              val json = response.entity.asString
              val a = parse(json) \ "_source"
              requestContext.complete(compact(a))
            }
            case Failure (_) => requestContext.complete(StatusCodes.FailedDependency)
          }
        }
      }
}