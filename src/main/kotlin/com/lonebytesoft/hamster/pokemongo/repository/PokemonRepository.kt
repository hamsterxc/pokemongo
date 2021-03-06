package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.Family
import com.lonebytesoft.hamster.pokemongo.model.Pokemon
import org.springframework.data.repository.CrudRepository

interface PokemonRepository : CrudRepository<Pokemon, Int> {

    fun findByNameAndForm(name: String, form: String?): Pokemon?

    fun findAllByFamily(family: Family): Collection<Pokemon>

}
