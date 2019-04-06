package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserPokemon

interface PokemonService {

    fun getPokemons(userId: Int): Collection<UserPokemon>

}
