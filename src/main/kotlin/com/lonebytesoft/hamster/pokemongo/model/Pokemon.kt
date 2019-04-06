package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class Pokemon(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int = 0,

        var number: Int = 0,
        var generation: Int = 0,
        var name: String = "",
        var form: String? = null,
        @OneToOne
        @JoinColumn
        var family: Family? = null,
        var notes: String? = null,

        var candyCatch: Int = 0,
        var candyTransfer: Int = 0,

        @OneToOne
        @JoinColumn
        var evolveFrom: Pokemon? = null,
        var evolveCandy: Int? = null,
        @OneToOne
        @JoinColumn
        var evolveItem: Item? = null,
        var evolveNotes: String? = null
)
