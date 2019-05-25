package bot

import cats.effect.IO
import cats.effect.concurrent.Ref

/**
  * Blocking pool of elements of type `A`
  */
trait Pool[F[_], A] {
  /**
    * Returns pair of `A` or blocks until the pair is available
    */
  def pair(a: A): F[(A, A)]
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
