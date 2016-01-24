package chars

import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl._
import akka.stream.{ SourceShape, ThrottleMode }

import scala.concurrent.duration._
import scala.language.{ implicitConversions, postfixOps }
import scala.util.Random

object Chars extends Server with App {

  val resources =
    get {
      pathSingleSlash {
        getFromResource("web/snow.html")
      }
    } ~
      getFromResourceDirectory("web")

  val snowRoute =
    get {
      path("snow") {
        val flow = Flow.fromSinkAndSource(Sink.ignore, source)
        handleWebsocketMessages(flow)
      }
    }

  runService { resources ~ snowRoute }

  lazy val sink = Sink.fold[Map[String, Int], String](Map()) { (m, msg) =>
    val updated = m + (msg -> (m.getOrElse(msg, 0) + 1))
    showTop10(updated)
    updated
  }

  lazy val source = Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val chars =
      Source(Random.alphanumeric)
        .throttle(10, 1 second, 10, ThrottleMode.Shaping)
        // .via(logFlow)
        .map(ch => TextMessage(ch.toString))
    val bcast = b.add(Broadcast[Message](2))

    chars ~> bcast ~> transform ~> sink

    SourceShape(bcast.out(1))
  })

  implicit def messageToString[T <: Message](m: T): String = m.asInstanceOf[TextMessage.Strict].text // ?
  def transform: Flow[Message, String, Unit] = Flow[Message].map { ch => ch.toUpperCase }

  private def showTop10(m: Map[String, Int]): Unit = {
    val top10 = for {
      (v, k) <- m.toSeq.sortBy(-_._2).take(10)
    } yield  v + " -> " + k
    print("\r" + top10.mkString(", "))
  }
}
