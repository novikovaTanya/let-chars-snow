package chars

import scala.util.{ Failure, Success }
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http

import scala.io.StdIn

trait Server {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionService = system.dispatcher

  def runService(route: Route): Unit = {
    val config = system.settings.config.getConfig("app")
    val interface = config.getString("interface")
    val port = config.getInt("port")

    val binding = Http().bindAndHandle(route, interface, port)

    binding.onComplete {
      case Success(x) => println(s"Server is listening on ${x.localAddress.getHostName}:${x.localAddress.getPort}")
      case Failure(e) => println(s"Binding failed with ${e.getMessage}")
    }

    StdIn.readLine()
    system.shutdown()
    system.awaitTermination()
  }

}