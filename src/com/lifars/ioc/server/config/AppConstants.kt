package com.lifars.ioc.server.config

import mu.KotlinLogging
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object AppConstants {
    val charset: Charset = StandardCharsets.UTF_8
}

object Logo {
    private val logger = KotlinLogging.logger(name = "")

    val lifars = """
@@@@          @@@@@@   @@@@@@@@@@      @@@@@@       @@@@@@@@@@@      @@@@@@@@@@%
@@@@         &@@@@@@&  @@@@           /@@@@@@\      @@@@    @@@@    @@@@      @%
@@@@           @@@@    @@@@@@@@@@    .@@@**@@@.     @@@@   @@@@@    @@@@@@@@.   
@@@@           @@@@    @@@@@@@@@@    @@@@  @@@@     @@@@@@@@@(        (@@@@@@@@@
@@@@          @@@@@@   @@@@         @@@@@@@@@@@@    @@@@  @@@@@    (@       @@@@
@@@@@@@@@@#   @@@@@@   @@@@        @@@@      @@@@   @@@@    @@@@@  (@@@@@@@@@@@                                                                            
    __      _   __   __  ___              __  _     __    __ __ __     _  __ __  
\_//  \/  \|_) |  \|/ _ | |  /\ |    |  |/  \|_)|  |  \  (_ |_ /  /  \|_)|_ |  \
 | \__/\__/| \ |__/|\__)| | /--\|__  |/\|\__/| \|__|__/, __)|__\__\__/| \|__|__/
 
################################################################################
################################################################################        
""".trimIndent()

    val app = """
 _____ ____   _____    _____ _               _             
|_   _/ __ \ / ____|  / ____| |             | |            
  | || |  | | |      | |    | |__   ___  ___| | _____ _ __ 
  | || |  | | |      | |    | '_ \ / _ \/ __| |/ / _ \ '__|
 _| || |__| | |____  | |____| | | |  __/ (__|   <  __/ |   
|_____\____/ \_____|  \_____|_| |_|\___|\___|_|\_\___|_|   
                                                            
  _____                          
 / ____|                         
| (___   ___ _ ____   _____ _ __ 
 \___ \ / _ \ '__\ \ / / _ \ '__|
 ____) |  __/ |   \ V /  __/ |   
|_____/ \___|_|    \_/ \___|_|
""".trimIndent()

    fun print(){
        logger.info("\n\n$lifars\n$app\n")
    }
}