package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class Pokemon(
        @Id
        var id: Int = 0,
        var name: String = "",
        var isFamilyLead: Boolean = false,
        var generation: Int = 0,
        var notes: String = "",

        var isWild: Boolean = false,
        var eggDistance: Double? = null,
        var raidLevel: Int? = null,
        var region: String? = null,

        var buddyDistance: Double = 0.0,
        var candyCatch: Int = 0,
        var candyTransfer: Int = 0,

        @OneToOne
        @JoinColumn(name = "evolveFrom")
        var evolveFrom: Pokemon? = null,
        var evolveCandy: Int? = null,
        @OneToOne
        @JoinColumn(name = "evolveItem")
        var evolveItem: Item? = null,
        var evolveNotes: String? = null
)
