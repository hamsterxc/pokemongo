package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "global")
data class GlobalEntry(
        @Id
        var key: String = "",
        var value: String? = null
)
