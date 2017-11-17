package com.lonebytesoft.hamster.pokemongo.view

class CalculationView(
        val families: Collection<CalculationFamilyView>,
        val candyNeeded: Collection<CalculationCandyView>,
        val itemNeeded: CalculationItemView
)

class CalculationFamilyView(
        val lead: Int,
        val members: Collection<CalculationMemberView>,
        val candyNeeded: CalculationCandyView,
        val itemNeeded: CalculationItemView,
        val pokemonNeeded: Double?
)

class CalculationMemberView(
        val id: Int,
        val isPresent: Boolean
)

class CalculationCandyView(
        val lead: Int,
        val candy: Int,
        val distance: Double?,
        val rareCandy: Int,
        val distanceWithRare: Double?
)

class CalculationItemView(
        val id: Collection<Int>,
        val distance: Double?
)
