package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.Item
import org.springframework.data.repository.CrudRepository

interface ItemRepository : CrudRepository<Item, Int> {
}
