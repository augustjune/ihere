package bot

import cats.MonadError
import cats.effect.IO
import cats.effect.concurrent.Ref
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.softwaremill.sttp.SttpBackend
import fs2.Stream
import cats.implicits._
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.{Location, Message}

class Bot(token: String)
         (implicit backend: SttpBackend[IO, Nothing], monadError: MonadError[IO, Throwable])
  extends TelegramBot[IO](token, backend) with Polling[IO] with Commands[IO] {

  type Conversation = List[Message]

  var conversations: Map[Long, Conversation] = Map()

  def conversation(id: Long): IO[Conversation] = IO(conversations.getOrElse(id, List()))

  onMessage { implicit msg =>
    conversation(msg.chat.id).flatMap {
      case Nil => reply("Oh hi")
      case h :: _ if h.text.getOrElse("") == "/go" =>
        msg.location match {
          case Some(location) =>
            registerUser(msg.chat.id, location) >>
              showUsers(msg.chat.id) >>
              showInfo(msg.chat.id) >>
              eraseConversation(msg.chat.id)
          case None => reply("I expect you to give me your location")
        }
    }.void
  }

  onCommand('go) { msg =>
    startConversation(msg) >> askForLocation(msg.chat.id)
  }

  def eraseConversation(chatId: Long): IO[Unit] = IO {
    conversations = conversations - chatId
  }

  def startConversation(message: Message): IO[Unit] = IO {
    conversations = conversations + (message.chat.id -> List(message))
  }

  def storeMessage(message: Message): IO[Unit] = IO {
    conversations = conversations + (message.chat.id -> (message :: conversations(message.chat.id)))
  }

  def askForLocation(chatId: Long): IO[Unit] =
    sendMessage(chatId, "Please provide your location (or any other location you want to find people from)")

  def registerUser(id: Long, location: Location): IO[Unit] =
    sendMessage(id, "Your location was stored")

  def showUsers(chatId: Long): IO[Unit] =
    sendMessage(chatId, "These are the strangers that looking for a conversation: id1, id2")

  def showInfo(chatId: Long): IO[Unit] =
    sendMessage(chatId, "Please select someone from the list. If you select more than one, you will be connected to the first person, who accepts your request")

  def sendMessage(chatId: Long, text: String): IO[Unit] = request(SendMessage(chatId, text)).void
}
