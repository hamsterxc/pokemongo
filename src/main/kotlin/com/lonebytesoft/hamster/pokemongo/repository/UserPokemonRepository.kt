package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.UserPokemon
import org.springframework.data.repository.CrudRepository

interface UserPokemonRepository : CrudRepository<UserPokemon, Int> {

    fun findAllByUserIdEquals(userId: Int): Iterable<UserPokemon>

}
