package com.naytev.controllers.actions

import play.api.http.HeaderNames
import play.api.mvc.{Result, Request, ActionBuilder}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

object CORSAction extends ActionBuilder[Request]{
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    block(request).map{ response =>
      response.withHeaders(
        HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "true",
        HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET",
        HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> request.headers.get(HeaderNames.ORIGIN).getOrElse(""),
        HeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS -> "Access-Control-Allow-Origin",
        "P3P" -> "CP=\"IDC DSP COR CURa ADMa OUR IND PHY ONL COM STA\"",
        HeaderNames.CACHE_CONTROL ->"private, no-cache, no-cache=Set-Cookie, proxy-revalidate"
      )
    }
  }
}
