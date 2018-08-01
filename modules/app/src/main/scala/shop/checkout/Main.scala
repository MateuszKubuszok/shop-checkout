package shop.checkout

import cats.implicits._
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import shapeless._
import shop.checkout.services.CheckoutServices

object Main {

  sealed trait Mode
  case object Run extends Mode
  case object Help extends Mode

  final case class Config(items: List[Item] = Nil, mode: Mode = Run)

  class ArgParser extends scopt.OptionParser[Config]("checkout") {

    val config = lens[Config]

    opt[(String, Int)]("item").unbounded.action {
      case ((typeValue, quantityValue), c) =>
        config.items.modify(c) {
          _ :+ Item(ItemType(typeValue.trim.toLowerCase), Quantity(quantityValue))
        }
    }

    help("help").action { (_, c) =>
      config.mode.set(c)(Help)
    }
  }

  lazy val parser = new ArgParser
  lazy val logger: Logger = Logger("shop.checkout")
  def checkoutServices[F[_]: Erroring]: CheckoutServices[F] = new CheckoutServices[F]

  def main(args: Array[String]): Unit = parser.parse(args, Config()).foreach(startApplication)

  private def startApplication(config: Config): Unit = {
    program[Task](config).runAsync
    ()
  }

  private def program[F[_]: Erroring](config: Config): F[Unit] = config.mode match {
    case Run  => run[F](config)
    case Help => help[F]
  }

  private def run[F[_]: Erroring](config: Config): F[Unit] =
    (for {
      totalCost <- checkoutServices[F].totalCost(config.items)
    } yield logger.info(s"Result is $totalCost cents")).recover {
      case error: Throwable => logger.error("Error occured", error)
    }

  private def help[F[_]: Erroring]: F[Unit] =
    parser.showTryHelp().pure[F]
}
