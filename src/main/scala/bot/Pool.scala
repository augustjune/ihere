package bot

import cats.effect.IO
import cats.effect.concurrent.Ref

trait Pool[F[_], A] {

  def current: F[Set[A]]

  def register(a: A): F[Unit]
}

class IoPool[A](pool: Ref[IO, Set[A]]) {

  def pair(a: A): IO[Either[Unit, (A, A)]] = {
    pool.get.flatMap {
      case s if s.isEmpty => pool.update(_ + a).map(Left(_))
      case _ =>
        pool.modify(s => {
          val el = s.head
          (s - el, el)
        }).map(head => Right(a, head))
    }
  }
}
