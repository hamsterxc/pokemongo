package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Family(
        @Id
        var number: Int = 0,
        var name: String = "",
        var buddyDistance: Double? = null
)
