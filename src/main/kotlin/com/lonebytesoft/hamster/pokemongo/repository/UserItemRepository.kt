package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.UserItem
import org.springframework.data.repository.CrudRepository

interface UserItemRepository : CrudRepository<UserItem, Int> {

    fun findAllByUserIdEquals(userId: Int): Iterable<UserItem>

}
