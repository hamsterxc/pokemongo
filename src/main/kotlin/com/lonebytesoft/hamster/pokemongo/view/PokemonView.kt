package com.lonebytesoft.hamster.pokemongo.view

class PokemonView(
        val userId: Int = 0,
        
        val id: Int = 0,
        val name: String = "",
        val isFamilyLead: Boolean = false,
        val generation: Int = 0,
        val notes: String = "",

        val isWild: Boolean = false,
        val isEgg: Boolean = false,
        val isRaid: Boolean = false,
        val isRegional: Boolean = false,

        val buddyDistance: Double = 0.0,
        val candyCatch: Int = 0,
        val candyTransfer: Int = 0,

        val evolveFrom: Int? = null,
        val evolveCandy: Int? = null,
        val evolveItem: Int? = null,
        val evolveNotes: String? = null,

        val seen: Int = 0,
        val caught: Int = 0,
        val have: Int = 0,
        val candy: Int = 0,
        val introduced: Double = 0.0
)
