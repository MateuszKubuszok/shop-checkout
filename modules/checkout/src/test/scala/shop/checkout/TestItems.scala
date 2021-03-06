package shop.checkout

object TestItems {

  val oneApple  = Item(ItemType("apple"))
  val twoApples = Item(ItemType("apple"), Quantity(2))

  val oneOrange  = Item(ItemType("orange"))
  val twoOranges = Item(ItemType("orange"), Quantity(2))

  val oneBanana  = Item(ItemType("banana"))
  val twoBananas = Item(ItemType("banana"), Quantity(2))
}
