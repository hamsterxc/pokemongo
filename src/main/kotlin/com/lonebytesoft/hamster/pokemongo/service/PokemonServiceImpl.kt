package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.Family
import com.lonebytesoft.hamster.pokemongo.model.Pokemon
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
        private val userPokemonRepository: UserPokemonRepository
) : PokemonService {

    override fun calculateFamilies(): Map<Int, Family> {
        val families: MutableCollection<MutableFamily> = ArrayList()
        val familiesCache: MutableMap<Int, MutableFamily> = HashMap()
        for(pokemon in pokemonRepository.findAll()) {
            var family: MutableFamily? = null

            val evolutionLine: MutableCollection<Pokemon> = ArrayList()
            var member: Pokemon? = pokemon
            while(member != null) {
                if(family == null) {
                    family = familiesCache[member.id]
                }
                evolutionLine.add(member)
                member = member.evolveFrom
            }

            if(family == null) {
                family = MutableFamily()
                families.add(family)
            }
            family.members.addAll(evolutionLine)
            if(pokemon.isFamilyLead) {
                if(family.lead == null) {
                    family.lead = pokemon
                } else {
                    throw IllegalStateException("Multiple leads: ${family.lead} and ${pokemon.id}")
                }
            }

            for(evolution in evolutionLine) {
                familiesCache[evolution.id] = family
            }
        }

        return families
                .associate {
                    val lead = it.lead
                    if(lead == null) {
                        throw IllegalStateException("No lead for family ${it.members}")
                    } else {
                        Pair(lead.id, Family(lead, it.members))
                    }
                }
    }

    override fun getPokemons(userId: Int): Map<Int, UserPokemon> {
        val pokemons: MutableMap<Int, UserPokemon> = userPokemonRepository.findAllByUserIdEquals(userId)
                .associateByTo(HashMap(), { it.pokemon.id })

        val user = userRepository.findOne(userId)
        pokemonRepository.findAll()
                .filter { it.id !in pokemons }
                .map { UserPokemon(0, it, user) }
                .associateByTo(pokemons, { it.pokemon.id })

        return pokemons
    }

    private data class MutableFamily(
            var lead: Pokemon? = null,
            val members: MutableSet<Pokemon> = HashSet()
    )

}
