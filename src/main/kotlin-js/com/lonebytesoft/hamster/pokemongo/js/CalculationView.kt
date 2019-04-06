package com.lonebytesoft.hamster.pokemongo.js

external class CalculationView {
    val items: Array<ItemView>
    val families: Array<FamilyView>
}

external class ItemView {
    val id: Int
    val name: String
    val count: Int
    val distance: Double?
}

external class FamilyView {
    val number: Int
    val name: String
    val pokemons: Array<PokemonView>
    val buddyDistance: Double

    val candiesNeeded: Int

    val distanceGeneral: Double?
    val distanceWithRareGeneral: Double?
    val rareToUseGeneral: Int?

    val distancePokemons: Double?
    val distanceCandies: Double?
    val distanceCandiesWithRare: Double?
    val rareToUse: Int?

    val itemsNeeded: Array<ItemView>
    val distanceItems: Double?
}

external class PokemonView {
    val number: Int
    val name: String
    val form: String?
    val rank: Int

    val present: Boolean

    val notes: String?

    val evolveCandy: Int?
    val evolveNotes: String?
}
