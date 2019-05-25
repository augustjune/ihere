import bot.Bot
import cats.effect.concurrent.Ref
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory

object Run extends IOApp {
  val config = ConfigFactory.parseResources("credentials/telegram.conf")
  val token = config.getString("token")
  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()

  def run(args: List[String]): IO[ExitCode] = for {
    ref <- Ref.of[IO, Set[Long]](Set.empty[Long])
    bot = new Bot(token, ref)
    _ <- bot.run()
    _ <- IO(println("Bot is successfully started."))
  } yield ExitCode.Success
}
