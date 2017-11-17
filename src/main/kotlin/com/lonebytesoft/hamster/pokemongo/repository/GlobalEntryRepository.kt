package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.GlobalEntry
import org.springframework.data.repository.CrudRepository

interface GlobalEntryRepository : CrudRepository<GlobalEntry, String> {
}
