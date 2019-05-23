package bot

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.methods.SendMessage
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}

import scala.concurrent.Future

class Bot(token: String)(chat1: Long, chat2: Long) extends TelegramBot with Polling with Commands[Future] {
  LoggerConfig.factory = PrintLoggerFactory()
  LoggerConfig.level = LogLevel.TRACE

  implicit val backend = OkHttpFutureBackend()
  override val client: RequestHandler[Future] = new FutureSttpClient(token)

  onMessage { implicit msg =>
    msg.chat.id match {
      case `chat1` => request(SendMessage(chat2, msg.text.map(s => s"Stranger says: $s").getOrElse(""))).map(_ => ())
      case `chat2` => request(SendMessage(chat1, msg.text.map(s => s"Stranger says: $s").getOrElse(""))).map(_ => ())
      case _ => Future.unit
    }
  }

}
