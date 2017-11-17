package com.lonebytesoft.hamster.pokemongo.app

import com.lonebytesoft.hamster.pokemongo.repository.GlobalEntryRepository
import com.lonebytesoft.hamster.pokemongo.repository.ItemRepository
import com.lonebytesoft.hamster.pokemongo.repository.PokemonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DataPopulator
@Autowired
constructor(
        private val globalEntryRepository: GlobalEntryRepository,
        private val pokemonRepository: PokemonRepository,
        private val itemRepository: ItemRepository
) {

    @PostConstruct
    fun populate() {
        // insert your data here
    }

}
