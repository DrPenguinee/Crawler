package crawler

import zio.http.*
import zio.{Scope, ZIO, ZIOAppDefault}

object CrawlerApp extends ZIOAppDefault {
  private val app = Routes(
    Method.GET / "titles" -> handler(RequestHandler.handle),
  ).sandbox

  override val run: ZIO[Any, Throwable, Nothing] = Server.serve(app).provide(Client.default, Server.default)
}
