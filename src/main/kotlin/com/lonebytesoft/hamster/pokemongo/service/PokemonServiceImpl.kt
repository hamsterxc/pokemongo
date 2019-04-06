package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.UserPokemon
import com.lonebytesoft.hamster.pokemongo.repository.PokemonRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserPokemonRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PokemonServiceImpl
@Autowired
constructor(
        private val userRepository: UserRepository,
        private val pokemonRepository: PokemonRepository,
        private val userPokemonRepository: UserPokemonRepository,
        private val familyService: FamilyService
) : PokemonService {

    override fun getPokemons(userId: Int): Collection<UserPokemon> {
        val user = userRepository.findOne(userId)
        return mergeUserItemsWithBase(
                userPokemonRepository.findAllByUserIdEquals(userId),
                pokemonRepository.findAll(),
                { it.pokemon.id },
                { it.id },
                { UserPokemon(0, it, user) }
        )
    }

}
