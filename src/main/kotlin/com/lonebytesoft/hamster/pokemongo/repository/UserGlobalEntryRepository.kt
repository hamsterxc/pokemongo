package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.UserGlobalEntry
import org.springframework.data.repository.CrudRepository

interface UserGlobalEntryRepository : CrudRepository<UserGlobalEntry, String> {

    fun findAllByUserIdEquals(userId: Int): Iterable<UserGlobalEntry>

}
