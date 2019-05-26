import bot.Bot
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory

object Run extends IOApp {
  val config = ConfigFactory.parseResources("credentials/telegram.conf")
  val token = config.getString("token")
  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()

  val bot = new Bot(token)

  def run(args: List[String]): IO[ExitCode] = for {
    _ <- bot.run()
    _ <- IO(println("Bot is successfully started."))
  } yield ExitCode.Success
}
