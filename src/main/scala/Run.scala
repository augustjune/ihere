import bot.Bot
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Run extends App {
  val config = ConfigFactory.parseResources("credentials/telegram.conf")
  val token = config.getString("token")
  val chatId1 = config.getLong("chatId1")
  val chatId2 = config.getLong("chatId2")

  val bot = new Bot(token)(chatId1, chatId2)

  val end = bot.run()

  println("Bot is successfully started.")
  Await.ready(end, Duration.Inf)
}
