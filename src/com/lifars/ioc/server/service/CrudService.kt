package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.payload.Payload

interface CrudService<Entity, ReadDto, SaveDto> {
    val repository: CrudRepository<Long, Entity>

    fun Entity.toDto(): ReadDto

    fun Entity.toSavedDto(): SaveDto

    fun SaveDto.toSaveEntity(): Entity

    suspend fun find(request: Payload.Request.GetList): Payload.Response.GetList<ReadDto> {
        val entities = repository.find(
            pagination = request.pagination.toRepositoryPagination(),
            sort = request.sort?.toRepositorySort(),
            filter = request.filter?.toRepositoryFilter(),
            reference = null
        )
        val dtos = entities.map { it.toDto() }
        return Payload.Response.GetList(
            data = dtos,
            total = entities.totalSize
        )
    }

    suspend fun find(request: Payload.Request.GetList, user: Long): Payload.Response.GetList<ReadDto> {
        val entities = repository.findOwned(
            pagination = request.pagination.toRepositoryPagination(),
            sort = request.sort?.toRepositorySort(),
            filter = request.filter?.toRepositoryFilter(),
            reference = null,
            ownerId = user
        )
        val dtos = entities.map { it.toDto() }
        return Payload.Response.GetList(
            data = dtos,
            total = entities.totalSize
        )
    }

    suspend fun find(request: Payload.Request.GetOne): Payload.Response.GetOne<ReadDto> {
        val entity = repository.findById(request.id)?.let { it.toDto() }
        return Payload.Response.GetOne(entity)
    }

    suspend fun find(request: Payload.Request.GetOne, user: Long): Payload.Response.GetOne<ReadDto> {
        val entity = repository.findByIdAndOwner(request.id, user)?.let { it.toDto() }
        return Payload.Response.GetOne(entity)
    }

    suspend fun find(request: Payload.Request.GetMany): Payload.Response.GetMany<ReadDto> {
        val entities = repository.findByIds(request.ids)
        return Payload.Response.GetMany(data = entities.map { it.toDto() })
    }

    suspend fun find(request: Payload.Request.GetMany, user: Long): Payload.Response.GetMany<ReadDto> {
        val entities = repository.findByIdsAndOwner(request.ids, user)
        return Payload.Response.GetMany(data = entities.map { it.toDto() })
    }

    suspend fun find(request: Payload.Request.GetManyReference): Payload.Response.GetManyReference<ReadDto> {
        val entities = repository.find(
            pagination = request.pagination.toRepositoryPagination(),
            sort = request.sort?.toRepositorySort(),
            filter = request.filter.toRepositoryFilter(),
            reference = Reference(
                targetTable = request.target,
                id = request.id
            )
        )
        return Payload.Response.GetManyReference(
            data = entities.map { it.toDto() },
            total = entities.totalSize
        )
    }

    suspend fun find(request: Payload.Request.GetManyReference, user: Long): Payload.Response.GetManyReference<ReadDto> {
        val entities = repository.findOwned(
            pagination = request.pagination.toRepositoryPagination(),
            sort = request.sort?.toRepositorySort(),
            filter = request.filter.toRepositoryFilter(),
            reference = Reference(
                targetTable = request.target,
                id = request.id
            ),
            ownerId = user
        )
        return Payload.Response.GetManyReference(
            data = entities.map { it.toDto() },
            total = entities.totalSize
        )
    }

    suspend fun save(request: Payload.Request.Create<SaveDto>): Payload.Response.Create<SaveDto> {
        val entity = repository.save(request.data.toSaveEntity())
        return Payload.Response.Create(data = entity.toSavedDto())
    }

    suspend fun save(request: Payload.Request.Update<SaveDto>): Payload.Response.Update<SaveDto> {
        val entity = repository.save(request.data.toSaveEntity(), request.id)
        return Payload.Response.Update(data = entity.toSavedDto())
    }

    suspend fun save(request: Payload.Request.UpdateMany<SaveDto>): Payload.Response.UpdateMany {
        repository.updateMany(request.data.toSaveEntity(), request.ids)
        return Payload.Response.UpdateMany(data = request.ids)
    }

    suspend fun delete(request: Payload.Request.Delete): Payload.Response.Delete<SaveDto> {
        repository.delete(request.id)
        return Payload.Response.Delete(data = null)
    }

    suspend fun delete(request: Payload.Request.DeleteMany): Payload.Response.DeleteMany {
        repository.deleteMany(request.ids)
        return Payload.Response.DeleteMany(data = null)
    }

    private fun Payload.Request.Pagination.toRepositoryPagination() = Pagination(
        offset = (page - 1) * perPage,
        limit = perPage
    )

    private fun Payload.Request.Sort.toRepositorySort() = Sort(
        column = field,
        order = Sort.Order.valueOf(order.name)
    )

    private fun Map<String, Any>.toRepositoryFilter(): Filter? =
        if (this.isEmpty())
            null
        else
            Filter(this.entries.map { (field, value) -> Filter.Item(field, value) })
}