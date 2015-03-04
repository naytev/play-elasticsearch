package com.naytev.plugin.elasticsearch

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import org.elasticsearch.common.settings.ImmutableSettings
import play.api._

class ElasticsearchPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("elasticsearch").getOrElse(Configuration.empty)
  case class ElasticsearchSource(
                          val hosts:List[String],
                          val port:Int,
                          val clusterName: String,
                          private var conn: ElasticClient = null
                          ){

    def connection: ElasticClient = {
      if (conn == null) {
        val settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build()
        val connectionString = hosts.map( host => s"$host:$port" ).mkString("elasticsearch://",",","")
        val uri = ElasticsearchClientUri(connectionString)
        Logger.info(s"Elasticsearch connecting to: ${hosts} | port: ${port}")
        conn = ElasticClient.remote(settings, uri)
      }
      conn
    }

    def reset() {
      conn.close()
      conn = null
    }


    override def toString() = {
      s"ElasticsearchSource"
    }
  }

  lazy val sources: Map[String, ElasticsearchSource] = configuration.subKeys.map { sourceKey =>
      val source = configuration.getConfig(sourceKey).getOrElse(Configuration.empty)


      val clusterName = source.getString("cluster_name").getOrElse(throw configuration.reportError("elasticsearch." + sourceKey + ".cluster", "cluster missing for source[" + sourceKey + "]"))

      // Simple config
      val hosts = source.getString("hosts").getOrElse("127.0.0.1").split(";").toList
      val port = source.getInt("port").getOrElse(9300)
      sourceKey -> ElasticsearchSource(hosts, port, clusterName)
  }.toMap

  override def enabled = !configuration.subKeys.isEmpty

  override def onStart() {
    sources.map { source =>
      app.mode match {
        case Mode.Test =>
        case _ => {
          try {
            source._2.connection
          } catch {
            case e: Throwable => throw configuration.reportError("elasticsearch." + source._1, "couldn't connect to [" + source._2.hosts.mkString(", ") + "]", Some(e))
          } finally {
            Logger("play").info("elasticsearch [" + source._1 + "] connected at " + source._2)
          }
        }
      }
    }
  }

  override def onStop(){
    sources.map { source =>
      // @fix See if we can get around the plugin closing connections in testmode
      if (app.mode != Mode.Test)
        source._2.reset()
    }
  }

  def source(name:String = "default") = sources(name)
  def client(name:String = "default") = sources(name).connection
}
