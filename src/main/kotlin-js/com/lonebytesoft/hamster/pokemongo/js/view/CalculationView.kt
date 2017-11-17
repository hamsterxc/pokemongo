package com.lonebytesoft.hamster.pokemongo.js.view

external class CalculationView {
        val families: Array<CalculationFamilyView>
        val candyNeeded: Array<CalculationCandyView>
        val itemNeeded: CalculationItemView
}

external class CalculationFamilyView {
        val lead: Int
        val members: Array<CalculationMemberView>
        val candyNeeded: CalculationCandyView
        val itemNeeded: CalculationItemView
        val pokemonNeeded: Double?
}

external class CalculationMemberView {
        val id: Int
        val present: Boolean
}

external class CalculationCandyView {
        val lead: Int
        val candy: Int
        val distance: Double?
        val rareCandy: Int
        val distanceWithRare: Double?
}

external class CalculationItemView {
        val id: Array<Int>
        val distance: Double?
}
