package shop.checkout.services

import cats.implicits._
import shop.checkout.{ Erroring, Item, ItemType, Quantity }

import scala.util.{ Failure, Success, Try }

class CheckoutServices[F[_]: Erroring] {

  private val unitPrices = Map(
    ItemType("apple") -> 60,
    ItemType("orange") -> 25,
    ItemType("banana") -> 20
  )

  private val itemsByPrice = unitPrices.toList.sortBy(_._2)

  def totalCost(items: List[Item]): F[Int] =
    Try {
      (aggregateItems andThen applyApplesBananasPromotion andThen applyOrangePromotion andThen countTotal)(items)
    } match {
      case Success(value) => value.pure[F]
      case Failure(error) => error.raiseError[F, Int]
    }

  private val aggregateItems: List[Item] => List[Item] =
    _.groupBy(_.itemType).mapValues(i => Quantity(i.map(_.quantity.value).sum)).map(Item.tupled).toList

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private val applyApplesBananasPromotion: List[Item] => List[Item] = { items =>
    val appleOrBananaItemType = Set(ItemType("apple"), ItemType("banana"))
    val applesAndBananasNumber = items.collect {
      case Item(itemType, Quantity(quantity)) if appleOrBananaItemType.contains(itemType) => quantity
    }.sum
    val toRemove = applesAndBananasNumber / 2

    itemsByPrice
      .collect {
        case (itemType, _) if appleOrBananaItemType.contains(itemType) => itemType
      }
      .foldLeft((toRemove, items): (Int, List[Item])) {
        case ((stillToRemove, itemsToUpdate), updatedItemType) =>
          itemsToUpdate
            .collectFirst {
              case Item(itemType, Quantity(quantity)) if itemType == updatedItemType =>
                val toRemoveHere  = math.min(stillToRemove, quantity)
                val toRemoveLater = stillToRemove - toRemoveHere
                toRemoveLater -> Item(itemType, Quantity(quantity - toRemoveHere))
            }
            .map {
              case (toRemoveLater, updatedItem) =>
                toRemoveLater -> itemsToUpdate.map {
                  case Item(itemType, _) if itemType == updatedItemType => updatedItem
                  case item                                             => item
                }
            }
            .getOrElse {
              stillToRemove -> itemsToUpdate
            }
      }
      ._2
  }

  private val applyOrangePromotion: List[Item] => List[Item] = _.map {
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
