package crawler.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class CrawlerResponse(urlsWithTitles: List[String])

object CrawlerResponse {
  given responseEncoder: JsonEncoder[CrawlerResponse] = DeriveJsonEncoder.gen
}
