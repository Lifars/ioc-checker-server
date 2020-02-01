package com.lifars.ioc.server.config

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class CommandLineArguments(parser: ArgParser) {
    val initAdminEmail: String? by parser.storing(
        "--admin-email",
        help = "Set admin login email. Requires also --admin-pass option"
    ).default<String?>(null)
    val initAdminPass: String? by parser.storing(
        "--admin-pass",
        help = "Set admin password. Requires also --admin-email option"
    ).default<String?>(null)

    val initAdminProbeName: String? by parser.storing(
        "--probe-name",
        help = "Set initial probe name for admin. Requires also --probe-key option"
    ).default<String?>(null)

    val initAdminProbeKey: String? by parser.storing(
        "--probe-key",
        help = "Set initial probe api key for admin. Requires also --probe-name option"
    ).default<String?>(null)

    val insertDummyIoc by parser.flagging(
        "--dummy-ioc",
        help = "Insert dummy IOC"
    )
}

private val argParserIgnore = listOf(
    "-config",
    "-host",
    "-port",
    "-watch",
    "-jar",
    "-sslPort",
    "-sslKeyStore"
)

fun filterArgsForArgParser(args: Array<String>): Array<String> =
    args.filterNot { it.split("=")[0] in argParserIgnore }.toTypedArray()