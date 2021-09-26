package com.pendula.mckenzie

import cats.effect.{ExitCode, IO, IOApp}
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main extends IOApp {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def logAndDie(e: Throwable): IO[ExitCode] = {
    logger.error(s"\nStartup failed\n $e")
    IO.pure(ExitCode.Success)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    logger.debug("Starting McKenzie...")
    val getConfig = Config.createConfig(logger)

    getConfig.fold(
      logAndDie,
      config => Service.runService(config)
    )
  }

}
