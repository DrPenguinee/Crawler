package crawler.model

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class CrawlerRequest(urls: List[String])

object CrawlerRequest {
  given requestDecoder: JsonDecoder[CrawlerRequest] = DeriveJsonDecoder.gen
}
