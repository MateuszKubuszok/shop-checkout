package shop.checkout.services

import cats.implicits._
import shop.checkout.{ Erroring, Item, ItemType }

import scala.util.{ Failure, Success, Try }

class CheckoutServices[F[_]: Erroring] {

  private val unitPrices = Map(
    ItemType("apple") -> 60,
    ItemType("orange") -> 25
  )

  def totalCost(items: List[Item]): F[Int] =
    Try(items.map {
      case Item(itemType, quantity) =>
        unitPrices(itemType) * quantity.value
    }) match {
      case Success(value) => value.sum.pure[F]
      case Failure(error) => error.raiseError[F, Int]
    }
}
