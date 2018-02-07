package com.lonebytesoft.hamster.pokemongo.controller

import com.lonebytesoft.hamster.pokemongo.service.PokemonService
import com.lonebytesoft.hamster.pokemongo.view.PokemonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PokemonController
@Autowired
constructor(
        private val pokemonService: PokemonService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/pokemon/{userId}"))
    fun getPokemons(@PathVariable userId: Int): Map<Int, PokemonView> {
        val pokemons = pokemonService.getPokemons(userId)

        val candy: MutableMap<Int, Int> = HashMap()
        pokemonService.calculateFamilies().forEach { _, family ->
            val candies = family.members
                    .map { pokemons[it.id]!!.candy }
                    .sum()
            family.members
                    .associateTo(candy, { Pair(it.id, candies) })
        }

        return pokemons
                .mapValues {
                    val userPokemon = it.value
                    val pokemon = userPokemon.pokemon
                    PokemonView(
                            userId,
                            pokemon.id, pokemon.name, pokemon.isFamilyLead, pokemon.generation, pokemon.notes,
                            pokemon.isWild, pokemon.eggDistance, pokemon.raidLevel, pokemon.region,
                            pokemon.buddyDistance, pokemon.candyCatch, pokemon.candyTransfer,
                            pokemon.evolveFrom?.id, pokemon.evolveCandy, pokemon.evolveItem?.id, pokemon.evolveNotes,
                            userPokemon.seen, userPokemon.caught, userPokemon.have, candy[pokemon.id]!!, userPokemon.introduced
                    )
                }
    }

}
