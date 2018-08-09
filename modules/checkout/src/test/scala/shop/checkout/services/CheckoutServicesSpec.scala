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
      result must beEqualTo(Success(145))
    }

    "with 2 apples have one free" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneApple, oneApple)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(120))
    }

    "with 2 bananas have one free" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneBanana, oneBanana, oneBanana)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(40))
    }

    "with 2 apples or bananas have one free (cheapest one)" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneBanana, oneApple)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(120))
    }

    "with 3 oranges pay for 2" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneOrange, oneOrange, oneOrange, oneOrange)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(75))
    }

    "with one apple and the bananas it should be 0.80p" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneBanana, oneBanana)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(80))
    }

    "with one apple and three bananas it should be 0.80p" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneBanana, oneBanana, oneBanana)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(80))
    }

    "with two apple and three bananas it should be 1.40p" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneApple, oneBanana, oneBanana, oneBanana)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(140))
    }

    "with three apple and three bananas it should be 1.80p" in {
      // given
      val services = new CheckoutServices[Try]
      val items    = List(oneApple, oneApple, oneApple, oneBanana, oneBanana, oneBanana)

      // when
      val result = services.totalCost(items)

      // then
      result must beEqualTo(Success(180))
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
