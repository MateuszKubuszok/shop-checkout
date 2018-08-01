package shop.checkout.services

import cats.implicits._
import org.specs2.mutable.Specification
import shop.checkout.{ Item, ItemType }
import shop.checkout.TestItems._

import scala.util.{ Failure, Success, Try }

class CheckoutServicesSpec extends Specification {

  "CheckoutServices.totalCost" should {

    "calculate the total cost of apples and oranges" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneApple, oneOrange, oneApple)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(205))
    }

    "return error if some item has no price defined" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(Item(ItemType("undefined")))

      // when
      val result = services.totalCost(items)

      // then
      result must beAnInstanceOf[Failure[Int]]
    }
  }
}
