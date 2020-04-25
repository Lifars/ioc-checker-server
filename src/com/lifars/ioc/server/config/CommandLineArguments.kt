package com.lifars.ioc.server.config

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class CommandLineArguments(parser: ArgParser) {
    val initAdminEmail: String? by parser.storing(
        "--admin-email",
        help = "Set admin login email. Requires also --admin-pass option"
    ).default<String?>(null)

    val smtpServer: String? by parser.storing(
        "--smtp-server",
        help = "Set SMTP server for email sending."
    ).default<String?>(null)
    val smtpPort by parser.storing(
        "--smtp-port",
        help = "Set SMTP port. Default is 465"
    ) { toInt() }.default(465)
    val smtpUserEmail: String? by parser.storing(
        "--smtp-user",
        help = "Set email address to use for email sending."
    ).default<String?>(null)
    val smtpUserPassword: String? by parser.storing(
        "--smtp-pass",
        help = "Set email address password"
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

    val defaultIocFeeders by parser.flagging(
        "--default-feed-sources",
        help = "Insert default IOC feed sources."
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