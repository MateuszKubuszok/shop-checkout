# Checkout app

Application for calculating checkout.

## Building

    scripts/sbt compile      # builds
    scripts/sbt test         # run tests
    scripts/sbt app/assembly # run tests

## Running

You can run app via sbt or using assembled uberjar

    scripts/sbt "run --help"
    scripts/sbt "run --item:apple=1 --item:orange=1"
    java -jar checkout.jar --help
    java -jar checkout.jar --item:apple=1 --item:orange=1
