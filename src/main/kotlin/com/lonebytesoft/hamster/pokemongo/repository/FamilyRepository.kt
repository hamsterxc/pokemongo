package com.lonebytesoft.hamster.pokemongo.repository

import com.lonebytesoft.hamster.pokemongo.model.Family
import org.springframework.data.repository.CrudRepository

interface FamilyRepository : CrudRepository<Family, Int> {
}
