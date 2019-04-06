package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.UserFamily
import org.springframework.data.repository.CrudRepository

interface UserFamilyRepository : CrudRepository<UserFamily, Int> {

    fun findAllByUserIdEquals(userId: Int): Iterable<UserFamily>

}
