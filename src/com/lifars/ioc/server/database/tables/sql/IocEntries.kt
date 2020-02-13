//package com.lifars.ioc.server.database.tables
//
//import org.jetbrains.exposed.sql.ReferenceOption
//import org.jetbrains.exposed.sql.Table
//
//object IocEntries : BaseTable("ioc_entries") {
//    val evaluationPolicy = enumeration("evalutaion_policy", EvaluationPolicy::class)
//        .default(EvaluationPolicy.default)
//    val searchType = enumeration("search_type", SearchType::class)
//        .default(SearchType.EXACT)
//    val name = varchar("name", 255)
//    val childEvaluationPolicy = enumeration("child_evalutaion_policy", EvaluationPolicy::class)
//        .default(EvaluationPolicy.default)
//    val parent = reference("parent", IocEntries, onDelete = ReferenceOption.CASCADE).nullable()
//    val mutexCheck = bool("mutex_check")
//    val processCheck = bool("process_check")
//    val dnsCheck = bool("dns_check")
//    val connsCheck = bool("conns_check")
//    val certsCheck = bool("certs_check")
////    val active = bool("active").index().default(true)
//
//    enum class EvaluationPolicy {
//        ALL,
//        ONE;
//
//        companion object {
//            val default = ONE
//        }
//    }
//
//    enum class SearchType {
//        EXACT,
//        REGEX;
//
//        companion object {
//            val default = EXACT
//        }
//    }
//}
//
////object IocChildren : Table("ioc_children"){
////    val parent = reference("parent", Iocs)
////    val child = reference("child", Iocs)
////
////    override val primaryKey = PrimaryKey(parent, child)
////}
//
//object IocFileInfos: Table("ioc_file_info"){
//    val iocId = reference("ioc_id", IocEntries, onDelete = ReferenceOption.CASCADE)
//    val filename = varchar("file_name", 512)
//    val hash = binary("file_hash_hex", 64)
//        .nullable()
//    val hashType = enumeration("hash_type", HashType::class)
//        .nullable()
//
//    override val primaryKey = PrimaryKey(iocId)
//
//    enum class HashType{
//        MD5,
//        SHA1,
//        SHA256
//    }
//}
//
//object IocRegistryInfos: Table("ioc_registry_info"){
//    val iocId = reference("ioc_id", IocEntries, onDelete = ReferenceOption.CASCADE)
//    val key = varchar("registry_key", 512)
//    val valueName = varchar("registry_value_name", 512)
//    val value = varchar("registry_value", 512)
//        .nullable()
//
//    override val primaryKey = PrimaryKey(iocId)
//}
//
////class User(id: EntityID<Long>) : BaseEntity(id, Users) {
////    companion object : UpdateHookedEntityClass<User>(Users)
////
////    var name by Users.name
////    var email by Users.email
////    var company by Users.company
////    var password by Users.password
////    var expires by Users.expires
////    var role by Users.role
////    val registeredProbes by Probe referrersOn Probes.owner
////}