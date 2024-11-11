package crawler

import crawler.model.{CrawlerRequest, CrawlerResponse}
import zio.http.*
import zio.json.*
import zio.{Cause, RIO, URIO, ZIO}

object RequestHandler {
  private val titleRegex      = "<title>(.*)</title>".r
  private val followRedirects = ZClientAspect.followRedirects(3)((resp, message) => ZIO.logInfo(message).as(resp))

  def handle(rawRequest: Request): RIO[Client, Response] = {
    for {
      requestEither  <- rawRequest.body.asString.map(_.fromJson[CrawlerRequest])
      request        <- ZIO.fromEither(requestEither).mapError(s => new Throwable(s))
      urlsWithTitles <- ZIO.foreachPar(request.urls)(processRawUrl).withParallelism(4).map(_.flatten)

      rawResponse = CrawlerResponse(urlsWithTitles.map((url, title) => s"$url: $title"))
    } yield Response.json(rawResponse.toJson)
  }

  private def processRawUrl(rawUrl: String): URIO[Client, Option[(URL, String)]] = {
    (for {
      url    <- ZIO.fromEither(URL.decode(rawUrl))
      client <- ZIO.serviceWith[Client](_ @@ followRedirects)
      res    <- client.batched(Request.get(url))
      data   <- res.body.asString
      _ <- ZIO.fail(new Throwable(s"Response failed with code ${res.status.code}: $data")).when(!res.status.isSuccess)

      maybeTitle = titleRegex.findFirstIn(data).map { case titleRegex(title) =>
        title
      }

      _ <- ZIO.logInfo(s"Title for url '$url' not found").when(maybeTitle.isEmpty)

    } yield maybeTitle.map(url -> _)).catchAll(e =>
      ZIO.logWarningCause(s"Got error during processing '$rawUrl': ${e.getMessage}", Cause.fail(e)).as(None),
    )
  }
}
