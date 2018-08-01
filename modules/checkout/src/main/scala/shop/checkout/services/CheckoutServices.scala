package shop.checkout.services

import cats.implicits._
import shop.checkout.{ Erroring, Item, ItemType, Quantity }

import scala.util.{ Failure, Success, Try }

class CheckoutServices[F[_]: Erroring] {

  private val unitPrices = Map(
    ItemType("apple") -> 60,
    ItemType("orange") -> 25
  )

  def totalCost(items: List[Item]): F[Int] =
    Try((aggregateItems andThen applyPromotions andThen countTotal)(items)) match {
      case Success(value) => value.pure[F]
      case Failure(error) => error.raiseError[F, Int]
    }

  private val aggregateItems: List[Item] => List[Item] =
    _.groupBy(_.itemType).mapValues(i => Quantity(i.map(_.quantity.value).sum)).map(Item.tupled).toList

  private val applyPromotions: List[Item] => List[Item] = _.map {
    case apples @ Item(ItemType("apple"), Quantity(quantity)) =>
      val newQuantity = Quantity(quantity - quantity / 2)
      apples.copy(quantity = newQuantity)
    case oranges @ Item(ItemType("orange"), Quantity(quantity)) =>
      val newQuantity = Quantity(quantity - quantity / 3)
      oranges.copy(quantity = newQuantity)
    case item => item
  }

  private val countTotal: List[Item] => Int = _.map {
    case Item(itemType, quantity) =>
      unitPrices(itemType) * quantity.value
  }.sum
}
