package com.pendula.mckenzie

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val getConfig = Config.createConfig()

    getConfig.fold(
      Function.const(IO.pure(ExitCode.Error)),
      config => Service.runService(config)
    )
  }

}
