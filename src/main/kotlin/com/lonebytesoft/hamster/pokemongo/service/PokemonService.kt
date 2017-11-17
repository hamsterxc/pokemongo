package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.Family
import com.lonebytesoft.hamster.pokemongo.model.UserPokemon

interface PokemonService {

    fun calculateFamilies(): Map<Int, Family>

    fun getPokemons(userId: Int): Map<Int, UserPokemon>

}
