package com.lonebytesoft.hamster.pokemongo.js

import com.lonebytesoft.hamster.pokemongo.js.view.CalculationItemView
import com.lonebytesoft.hamster.pokemongo.js.view.CalculationView
import com.lonebytesoft.hamster.pokemongo.js.view.ItemView
import com.lonebytesoft.hamster.pokemongo.js.view.PokemonView
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.js.asDynamic

fun main(args: Array<String>) {
    val userId = if(window.location.search.isNotBlank()) window.location.search.substring(1) else ""
    window.onload = {
        async {
            val promiseCalculate = window.fetch("calculate/" + userId)
            val promisePokemon = window.fetch("pokemon/" + userId)
            val promiseItem = window.fetch("item/" + userId)

            val calculation = parse<CalculationView>(promiseCalculate)
            val pokemons = parse<Array<PokemonView>>(promisePokemon)
            val items = parse<Array<ItemView>>(promiseItem)

            fillBuddyTable(calculation, pokemons)
            fillItemTable(calculation, items)
            fillFamilyTables(calculation, pokemons, items)
        }
    }
}

private fun fillBuddyTable(calculation: CalculationView, pokemons: Array<PokemonView>) {
    val buddyBody = document.getElementById("buddy-body") as Node
    var distanceTotal: Double? = 0.0
    var distanceRareTotal: Double? = 0.0
    var rareCandyTotal = 0
    calculation.candyNeeded.sortBy { it.lead }
    calculation.candyNeeded
            .filter { it.distance.isGreaterZero() || it.distanceWithRare.isGreaterZero() }
            .forEach {
                buddyBody.appendChild(createTableRow(document, "td",
                        it.lead.toString(),
                        pokemons[it.lead].name,
                        it.distance?.format(2).orUnknownDistance(),
                        it.distanceWithRare?.format(2).orUnknownDistance(),
                        it.rareCandy.toString()
                ))
                distanceTotal = distanceTotal.aggregate(it.distance, { a, b -> a + b })
                distanceRareTotal = distanceRareTotal.aggregate(it.distanceWithRare, { a, b -> a + b })
                rareCandyTotal += it.rareCandy
            }

    val rowBuddyTotal = createTableRow(document, "th",
            "Total",
            distanceTotal?.format(2).orUnknownDistance(),
            distanceRareTotal?.format(2).orUnknownDistance(),
            rareCandyTotal.toString()
    )
    (rowBuddyTotal.childNodes[0] as Element).setAttribute("colspan", "2")
    buddyBody.appendChild(rowBuddyTotal)
}

private fun fillItemTable(calculation: CalculationView, items: Array<ItemView>) {
    if (calculation.itemNeeded.id.asDynamic().length as Int > 0) {
        val itemBody = document.getElementById("item-body") as Node
        val itemsNeeded = calculateItemsNeeded(calculation.itemNeeded)
        itemsNeeded.forEach {
            itemBody.appendChild(createTableRow(document, "td", items[it.id].name, it.count.toString()))
        }

        val rowItemsTotal = createTableRow(document, "th",
                "Total: " + calculation.itemNeeded.distance?.format(2).orUnknownDistance())
        (rowItemsTotal.childNodes[0] as Element).setAttribute("colspan", "2")
        itemBody.appendChild(rowItemsTotal)
    } else {
        val item = document.getElementById("item") as Element
        item.setAttribute("style", "display:none;" + item.getAttribute("style"))
    }
}

private fun fillFamilyTables(calculation: CalculationView, pokemons: Array<PokemonView>, items: Array<ItemView>) {
    val families = document.getElementById("families") as Node
    calculation.families.sortBy { it.lead }
    calculation.families.forEach {
        var problem = false
        val family = document.createElement("table")

        it.members.sort { a, b ->
            val rankA = calculateRank(a.id, pokemons)
            val rankB = calculateRank(b.id, pokemons)
            if (rankA == rankB) {
                a.id - b.id
            } else {
                rankA - rankB
            }
        }

        val membersArray = Array(0, { 0 })
        it.members.forEach { membersArray.asDynamic().push(it) }
        val membersCount = membersArray.size

        it.members.forEachIndexed { index, member ->
            val pokemon = pokemons[member.id]
            val evolveCandy = pokemon.evolveCandy?.toString() ?: "&ndash;"
            val buddyDistance = pokemon.buddyDistance.format(0)

            var notes = ""
            if (pokemon.notes.isNotBlank()) {
                notes += pokemon.notes
            }
            if (!pokemon.evolveNotes.isNullOrBlank()) {
                if (notes.isNotBlank()) {
                    notes += "<br/>"
                }
                notes += pokemon.evolveNotes
            }

            val row: Element
            if (index == 0) {
                val itemsNeeded = calculateItemsNeeded(it.itemNeeded)
                var cellItems = ""
                if (itemsNeeded.size > 0) {
                    itemsNeeded.forEach {
                        cellItems = cellItems.appendLineBreak()
                        if (cellItems.isNotEmpty()) {
                            cellItems += "<br/>"
                        }
                        cellItems += items[it.id].name
                        if (it.count > 1) {
                            cellItems += " x" + it.count.toString()
                        }
                    }
                }

                var cellDistance = ""
                var distanceTotal: Double? = 0.0
                if (it.pokemonNeeded.isGreaterZero()) {
                    cellDistance += "Pokemon: " + it.pokemonNeeded?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(it.pokemonNeeded, { a, b -> maxOf(a, b) })
                }
                if (it.candyNeeded.distance.isGreaterZero()) {
                    cellDistance = cellDistance.appendLineBreak()
                    cellDistance += "Candies: " + it.candyNeeded.distance?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(it.candyNeeded.distance, { a, b -> maxOf(a, b) })
                    if (it.candyNeeded.rareCandy > 0) {
                        cellDistance = cellDistance.appendLineBreak()
                        cellDistance += "Candies w/" + it.candyNeeded.rareCandy.toString() + " rare: " +
                                it.candyNeeded.distanceWithRare?.format(2).orUnknownDistance()
                        distanceTotal = distanceTotal.aggregate(it.candyNeeded.distanceWithRare, { a, b -> maxOf(a, b) })
                    }
                }
                if (it.itemNeeded.distance.isGreaterZero()) {
                    cellDistance = cellDistance.appendLineBreak()
                    cellDistance += "Items: " + it.itemNeeded.distance?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(it.itemNeeded.distance, { a, b -> maxOf(a, b) })
                }
                if (distanceTotal.isGreaterZero()) {
                    problem = true
                    cellDistance += "<hr/><strong>Total: " + distanceTotal?.format(2).orUnknownDistance() + "</strong>"
                }

                row = createTableRow(document, "td",
                        pokemon.id.toString(),
                        pokemon.name,
                        evolveCandy,
                        buddyDistance,
                        it.candyNeeded.candy.toString(),
                        cellItems,
                        cellDistance,
                        notes
                )

                (row.childNodes[0] as Element).addClass("family-id")
                (row.childNodes[1] as Element).addClass("family-name")
                (row.childNodes[2] as Element).addClass("family-candy-evolve")
                (row.childNodes[3] as Element).addClass("family-buddy-distance")
                (row.childNodes[4] as Element).addClass("family-candy-needed")
                (row.childNodes[5] as Element).addClass("family-item-needed")
                (row.childNodes[6] as Element).addClass("family-distance")
                (row.childNodes[7] as Element).addClass("family-notes")

                for (i in 4..6) {
                    (row.childNodes[i] as Element).setAttribute("rowspan", membersCount.toString())
                }
                if (it.candyNeeded.candy > 0) {
                    (row.childNodes[4] as Element).addClass("fail")
                }
                if (itemsNeeded.size > 0) {
                    (row.childNodes[5] as Element).addClass("fail")
                }
            } else {
                row = createTableRow(document, "td",
                        pokemon.id.toString(),
                        pokemon.name,
                        evolveCandy,
                        buddyDistance,
                        notes
                )

                (row.childNodes[0] as Element).addClass("family-id")
                (row.childNodes[1] as Element).addClass("family-name")
                (row.childNodes[2] as Element).addClass("family-candy-evolve")
                (row.childNodes[3] as Element).addClass("family-buddy-distance")
                (row.childNodes[4] as Element).addClass("family-notes")
            }

            for (i in 0..1) {
                val cell = row.childNodes[i] as Element
                cell.addClass(if (member.present) "success" else "fail")
                if (member.id == it.lead) {
                    cell.addClass("lead")
                }
            }

            family.appendChild(row)
        }

        if (problem) {
            families.appendChild(document.createElement("br"))
            families.appendChild(family)
        }
    }
}

private fun createTableRow(document: Document, tag: String, vararg values: String): Element {
    val row = document.createElement("tr")
    for(value in values) {
        val cell = document.createElement(tag)
        cell.asDynamic().innerHTML = value
        row.appendChild(cell)
    }
    return row
}

private fun calculateItemsNeeded(calculationItemView: CalculationItemView): Array<ItemNeededData> {
    val itemsNeeded: Array<ItemNeededData> = Array(0, { ItemNeededData(0) })
    calculationItemView.id.forEach {
        var index: Int? = null
        itemsNeeded.forEachIndexed { itemNeededDataIndex, itemNeededData ->
            if(itemNeededData.id == it) {
                index = itemNeededDataIndex
            }
        }
        if(index == null) {
            val itemNeededData = ItemNeededData(it, 1)
            itemsNeeded.asDynamic().push(itemNeededData)
        } else {
            itemsNeeded[index!!].count += 1
        }
    }
    itemsNeeded.sortBy { it.id }
    return itemsNeeded
}

private fun calculateRank(id: Int, pokemons: Array<PokemonView>): Int {
    var current: Int? = id
    var rank = 1
    while(current != null) {
        current = pokemons[current].evolveFrom
        rank++
    }
    return rank
}

private class ItemNeededData (
    val id: Int,
    var count: Int = 0
)
