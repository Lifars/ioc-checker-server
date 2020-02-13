package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.VisitedFeed

interface VisitedFeedUrlRepository : CrudRepository<String, VisitedFeed>