package com.naytev.plugin.elasticsearch


import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current


object ElasticApp extends Specification {

  lazy val elasticApp = FakeApplication(
    additionalPlugins = Seq("com.naytev.plugin.elasticsearch.ElasticsearchPlugin")
  )

  "Elasticsearch Plugin with basic config" should {

    lazy val app = elasticApp.copy(
      additionalConfiguration = Map(
        ("elasticsearch.default.cluster_name" -> "elasticsearch_mbseid")
      ))

    lazy val es = app.plugin[ElasticsearchPlugin].get

    running(app) {
      "start" in {
        es must beAnInstanceOf[ElasticsearchPlugin]
      }

      "return a connection" in{
        es.client() must not be equalTo(null)
      }
      "fail if source doesn't exist" in {
        es.client("foobar") must throwAn[NoSuchElementException]
      }

    }

  }

  "Elasticsearch Plugin with single host config" should {

    lazy val app = elasticApp.copy(
      additionalConfiguration = Map(
        ("elasticsearch.default.hosts" -> "localhost"),
        ("elasticsearch.default.port" -> "9300"),
        ("elasticsearch.default.cluster_name" -> "elasticsearch_mbseid")
      ))

    lazy val es = app.plugin[ElasticsearchPlugin].get

    running(app) {
      "start" in {
        es must beAnInstanceOf[ElasticsearchPlugin]
      }

      "have the proper host" in{
        es.source().hosts must beEqualTo(List("localhost"))
      }
      "have the proper port" in{
        es.source().port must beEqualTo(9300)
      }
      "can insert into random index" in{
        import com.sksamuel.elastic4s.ElasticDsl._
        val indexName = "foobar"
        val client = es.client()
        client.execute{
          index into s"$indexName/foobar" fields "name" -> "mike"
        }
        val result = client.execute{
          delete index indexName
        }
        result.map(_.isAcknowledged) must beTrue.await()

      }

    }
  }

}
