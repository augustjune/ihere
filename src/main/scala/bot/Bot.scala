package bot

import cats.MonadError
import cats.effect.IO
import cats.effect.concurrent.Ref
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.softwaremill.sttp.SttpBackend
import cats.implicits._
import com.bot4s.telegram.methods.SendMessage

class Bot(token: String, ref: Ref[IO, Set[Long]])
         (implicit backend: SttpBackend[IO, Nothing], monadError: MonadError[IO, Throwable])
  extends TelegramBot[IO](token, backend) with Polling[IO] with Commands[IO] {

  val pool: IoPool[Long] = new IoPool[Long](ref)
  var pairs: Map[Long, Long] = Map()

  def paired(id1: Long): IO[Option[Long]] = IO(pairs.get(id1))

  onMessage { msg =>
    paired(msg.chat.id).flatMap {
      case None => reply("Ye, hi")(msg)
      case Some(another) => request(SendMessage(another, s"Stranger says: ${msg.text.get}"))
    }.void
  }

  onCommand("/find") { msg =>
    pool.pair(msg.chat.id).flatMap {
      case Left(_) =>
        reply("We are looking for a pair for you. You will be notified when another person is found")(msg)
      case Right((id1, id2)) =>
        IO(pairs = pairs ++ Map(id1 -> id2, id2 -> id1)).flatMap{_ =>
          List(id1, id2).traverse(id => request(SendMessage(id, "You were paired with a stranger, you cans start to talk")))
        }
    }.void
  }

  onCommand("/bye") { msg =>
    paired(msg.chat.id).flatMap{
      case None => reply("You were not paired with anyone, but that's alright")(msg)
      case Some(another) =>
        IO(pairs = pairs -- List(msg.chat.id, another)).flatMap(_ =>
        List(msg.chat.id, another).traverse(id => request(SendMessage(id, "Your conversation was ended."))))
    }.void
  }

}
