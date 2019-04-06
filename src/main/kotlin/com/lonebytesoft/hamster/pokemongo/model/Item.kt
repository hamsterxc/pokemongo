package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Item (
        @Id
        var id: Int = 0,
        var name: String = "",
        var probability: Double = 0.0
)
