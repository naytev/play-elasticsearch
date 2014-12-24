package com.naytev.controllers.actions

import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api._
import play.api.mvc._
import play.api.test._

import scala.concurrent.Future


object CORSActionSpec extends PlaySpecification {
  lazy val playApp = FakeApplication()

  object FakeController extends Controller{
    def fakeCORSAction = CORSAction { implicit request =>
      Ok("hi!")
    }

    def fakeAsyncCORSAction = CORSAction.async { implicit request =>
      Future { Ok("hi!") }
    }
  }

  "CORSAction" should {
    running(playApp) {
      "run" in {
        1 mustEqual 1
      }
      "run sync request" in {
        val origin = "http://www.naytev.com"

        val fakeRequest = FakeRequest(Helpers.GET, "/events", FakeHeaders(
          Seq(ORIGIN->Seq(origin))
        ), "")
        val result = FakeController.fakeCORSAction(fakeRequest).run

        status(result) must equalTo(OK)
        //result.map( result => Logger.info(result.toString()))
        header(ACCESS_CONTROL_ALLOW_HEADERS, result) must beSome("true")
        header(ACCESS_CONTROL_ALLOW_METHODS, result) must beSome("POST, GET")
        header(ACCESS_CONTROL_ALLOW_ORIGIN, result) must beSome(origin)
      }
      "run async request" in {
        val origin = "http://www.naytev.com"

        val fakeRequest = FakeRequest(Helpers.GET, "/events", FakeHeaders(
          Seq(ORIGIN->Seq(origin))
        ), "")
        val result = FakeController.fakeAsyncCORSAction(fakeRequest).run

        status(result) must equalTo(OK)
        //result.map( result => Logger.info(result.toString()))
        header(ACCESS_CONTROL_ALLOW_HEADERS, result) must beSome("true")
        header(ACCESS_CONTROL_ALLOW_METHODS, result) must beSome("POST, GET")
        header(ACCESS_CONTROL_ALLOW_ORIGIN, result) must beSome(origin)
      }
    }
  }


}
