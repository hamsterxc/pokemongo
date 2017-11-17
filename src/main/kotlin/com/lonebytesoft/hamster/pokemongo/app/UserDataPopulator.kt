package com.lonebytesoft.hamster.pokemongo.app

import com.lonebytesoft.hamster.pokemongo.repository.GlobalEntryRepository
import com.lonebytesoft.hamster.pokemongo.repository.ItemRepository
import com.lonebytesoft.hamster.pokemongo.repository.PokemonRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserGlobalEntryRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserItemRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserPokemonRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@DependsOn("dataPopulator")
class UserDataPopulator
@Autowired
constructor(
        private val userRepository: UserRepository,
        private val globalEntryRepository: GlobalEntryRepository,
        private val userGlobalEntryRepository: UserGlobalEntryRepository,
        private val pokemonRepository: PokemonRepository,
        private val userPokemonRepository: UserPokemonRepository,
        private val itemRepository: ItemRepository,
        private val userItemRepository: UserItemRepository
) {

    @PostConstruct
    fun populate() {
        // insert your data here
    }

}
