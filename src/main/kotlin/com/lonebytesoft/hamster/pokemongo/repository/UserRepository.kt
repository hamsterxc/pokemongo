package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Int> {
}
