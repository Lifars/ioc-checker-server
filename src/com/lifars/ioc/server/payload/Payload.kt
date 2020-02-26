package com.lifars.ioc.server.payload


object Payload {
    object Response{
        data class GetList<T>(
            val data: List<T>,
            val total: Int
        )

        data class GetOne<T>(
            val data: T?
        )

        data class GetMany<T>(
            val data: List<T>
        )

        data class GetManyReference<T>(
            val data: List<T>,
            val total: Int
        )

        data class Create<T>(
            val data: T
        )

        data class Update<T>(
            val data: T
        )

        data class UpdateMany(
            val data: List<Long>
        )

        data class Delete<T>(
            val data: T? = null
        )

        data class DeleteMany(
            val data: List<Long>? = null
        )
    }

    object Request{
        data class GetList(
            val pagination: Pagination,
            val sort: Sort?,
            val filter: Map<String, Any>
        )

        data class GetOne(
            val id: Long
        )

        data class GetMany(
            val ids: List<Long>
        )

        data class GetManyReference(
            val target: String,
            val id: Long,
            val pagination: Pagination,
            val sort: Sort?,
            val filter: Map<String, Any>
        )

        interface Create<T> {
            val data: T
        }

        interface Update<T> {
            val id: Long
            val data: T
            val previousData: T
        }

        interface UpdateMany<T> {
            val ids: List<Long>
            val data: T
        }

        data class Delete(
            val id: Long
        )

        data class DeleteMany(
            val ids: List<Long>
        )

        data class Pagination(
            val page: Int,
            val perPage: Int
        )

        data class Sort(
            val field: String,
            val order: Order
        ){
            enum class Order{
                ASC,
                DESC
            }
        }
    }
}