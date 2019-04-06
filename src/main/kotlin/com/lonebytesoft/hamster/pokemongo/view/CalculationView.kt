package com.lonebytesoft.hamster.pokemongo.view

data class CalculationView(
        val items: Collection<ItemView>,
        val families: Collection<FamilyView>
)

data class ItemView(
        val id: Int,
        val name: String,
        val count: Int,
        val distance: Double?
)

data class FamilyView(
        val number: Int,
        val name: String,
        val pokemons: Collection<PokemonView>,
        val buddyDistance: Double,

        val candiesNeeded: Int,

        val distanceGeneral: Double?,
        val distanceWithRareGeneral: Double?,
        val rareToUseGeneral: Int?,

        val distancePokemons: Double?,
        val distanceCandies: Double?,
        val distanceCandiesWithRare: Double?,
        val rareToUse: Int?,

        val itemsNeeded: Collection<ItemView>,
        val distanceItems: Double?
)

data class PokemonView(
        val number: Int,
        val name: String,
        val form: String?,
        val rank: Int,

        val isPresent: Boolean,

        val notes: String?,

        val evolveCandy: Int?,
        val evolveNotes: String?
)
