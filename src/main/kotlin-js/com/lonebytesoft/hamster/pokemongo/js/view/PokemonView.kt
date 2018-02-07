package com.lonebytesoft.hamster.pokemongo.js.view

external class PokemonView {
        val userId: Int

        val id: Int
        val name: String
        val isFamilyLead: Boolean
        val generation: Int
        val notes: String

        val isWild: Boolean
        var eggDistance: Double?
        var raidLevel: Int?
        var region: String?

        val buddyDistance: Double
        val candyCatch: Int
        val candyTransfer: Int

        val evolveFrom: Int?
        val evolveCandy: Int?
        val evolveItem: Int?
        val evolveNotes: String?

        val seen: Int
        val caught: Int
        val have: Int
        val candy: Int
        val introduced: Double
}
