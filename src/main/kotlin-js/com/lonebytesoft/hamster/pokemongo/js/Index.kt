package com.lonebytesoft.hamster.pokemongo.js

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass

fun main(args: Array<String>) {
    val userId = if(window.location.search.isNotBlank()) window.location.search.substring(1) else ""
    window.onload = {
        async {
            val promiseCalculate = window.fetch("calculate/$userId")
            val calculation = parse<CalculationView>(promiseCalculate)

            fillBuddyTable(calculation)
            fillItemTable(calculation)
            fillFamilyTables(calculation)
        }
    }
}

private fun fillBuddyTable(calculation: CalculationView) {
    val buddyBody = document.getElementById("buddy-body") as Node

    var distanceIndividual: Double? = 0.0
    var distanceTotal: Double? = 0.0
    var distanceRareTotal: Double? = 0.0
    var rareCandyTotal = 0

    calculation.families.sortBy { it.number }
    calculation.families
            .filter { it.pokemons.any { it.present } && it.hasDistance() }
            .forEach {
                buddyBody.appendChild(createTableRow(document, "td",
                        it.number.toString(),
                        it.name,
                        it.distanceCandies?.format(2).orUnknownDistance(),
                        it.distanceGeneral?.format(2).orUnknownDistance(),
                        it.distanceWithRareGeneral?.format(2).orUnknownDistance(),
                        it.rareToUseGeneral.toString()
                ))

                distanceIndividual = distanceIndividual.aggregate(it.distanceCandies) { a, b -> a + b }
                distanceTotal = distanceTotal.aggregate(it.distanceGeneral) { a, b -> a + b }
                distanceRareTotal = distanceRareTotal.aggregate(it.distanceWithRareGeneral) { a, b -> a + b }
                rareCandyTotal += it.rareToUseGeneral ?: 0
            }

    val rowBuddyTotal = createTableRow(document, "th",
            "Total",
            distanceIndividual?.format(2).orUnknownDistance(),
            distanceTotal?.format(2).orUnknownDistance(),
            distanceRareTotal?.format(2).orUnknownDistance(),
            rareCandyTotal.toString()
    )
    (rowBuddyTotal.childNodes[0] as Element).setAttribute("colspan", "2")
    buddyBody.appendChild(rowBuddyTotal)
}

private fun fillItemTable(calculation: CalculationView) {
    if (calculation.items.asDynamic().length as Int > 0) {
        val itemBody = document.getElementById("item-body") as Node
        calculation.items.sortBy { it.id }
        calculation.items.forEach {
            itemBody.appendChild(createTableRow(document, "td", it.name, it.count.toString()))
        }

        val total = calculation.items
                .map { it.distance }
                .let {
                    if (it.any { it == null }) {
                        null
                    } else {
                        it.sumByDouble { it!! }
                    }
                }
        val rowItemsTotal = createTableRow(document, "th", "Total: " + total?.format(2).orUnknownDistance())
        (rowItemsTotal.childNodes[0] as Element).setAttribute("colspan", "2")
        itemBody.appendChild(rowItemsTotal)
    } else {
        val item = document.getElementById("item") as Element
        item.setAttribute("style", "display:none;" + item.getAttribute("style"))
    }
}

private fun fillFamilyTables(calculation: CalculationView) {
    val families = document.getElementById("families") as Node
    calculation.families.sortBy { it.number }
    calculation.families.forEach { familyView ->
        var problem = false
        val family = document.createElement("table")

        familyView.pokemons.sort { a, b ->
            if (a.rank == b.rank) {
                if (a.number == b.number) {
                    if (a.form.isFormNormal()) {
                        if (b.form.isFormNormal()) 0 else -1
                    } else {
                        if (b.form.isFormNormal()) {
                            1
                        } else {
                            a.form?.compareTo(b.form ?: "") ?: 0
                        }
                    }
                } else {
                    a.number - b.number
                }
            } else {
                a.rank - b.rank
            }
        }

        familyView.pokemons.forEachIndexed { index, member ->
            val evolveCandy = member.evolveCandy?.toString() ?: "&ndash;"
            val buddyDistance = familyView.buddyDistance.format(0)

            var notes = ""
            if (!member.notes.isNullOrBlank()) {
                notes += member.notes
            }
            if (!member.evolveNotes.isNullOrBlank()) {
                notes = notes.appendLineBreak() + "Evolution: " + member.evolveNotes
            }

            val row: Element
            if (index == 0) {
                var cellItems = ""
                familyView.itemsNeeded.forEach {
                    cellItems = cellItems.appendLineBreak()
                    if (cellItems.isNotEmpty()) {
                        cellItems += "<br/>"
                    }
                    cellItems += it.name
                    if (it.count > 1) {
                        cellItems += " x" + it.count.toString()
                    }
                }

                var cellDistance = ""
                var distanceTotal: Double? = 0.0
                if (familyView.distancePokemons.isNullOrGreaterZero()) {
                    cellDistance += "Pokemon: " + familyView.distancePokemons?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(familyView.distancePokemons) { a, b -> maxOf(a, b) }
                }
                if (familyView.distanceCandies.isNullOrGreaterZero()) {
                    cellDistance = cellDistance.appendLineBreak()
                    cellDistance += "Candies: " + familyView.distanceCandies?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(familyView.distanceCandies) { a, b -> maxOf(a, b) }
                    if ((familyView.rareToUse ?: 0) > 0) {
                        cellDistance = cellDistance.appendLineBreak()
                        cellDistance += "Candies w/" + familyView.rareToUse.toString() + " rare: " +
                                familyView.distanceCandiesWithRare?.format(2).orUnknownDistance()
                        distanceTotal = distanceTotal.aggregate(familyView.distanceCandiesWithRare) { a, b -> maxOf(a, b) }
                    }
                }
                if (familyView.distanceItems.isNullOrGreaterZero()) {
                    cellDistance = cellDistance.appendLineBreak()
                    cellDistance += "Items: " + familyView.distanceItems?.format(2).orUnknownDistance()
                    distanceTotal = distanceTotal.aggregate(familyView.distanceItems) { a, b -> maxOf(a, b) }
                }
                if (distanceTotal.isNullOrGreaterZero()) {
                    problem = true
                    cellDistance += "<hr/><strong>Total: " + distanceTotal?.format(2).orUnknownDistance() + "</strong>"
                }

                row = createTableRow(document, "td",
                        member.number.toString(),
                        member.nameWithForm(),
                        evolveCandy,
                        buddyDistance,
                        familyView.candiesNeeded.toString(),
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

                for (i in 3..6) {
                    (row.childNodes[i] as Element).setAttribute("rowspan", familyView.pokemons.size.toString())
                }
                if (familyView.candiesNeeded > 0) {
                    (row.childNodes[4] as Element).addClass("fail")
                }
                if (familyView.itemsNeeded.isNotEmpty()) {
                    (row.childNodes[5] as Element).addClass("fail")
                }
            } else {
                row = createTableRow(document, "td",
                        member.number.toString(),
                        member.nameWithForm(),
                        evolveCandy,
                        notes
                )

                (row.childNodes[0] as Element).addClass("family-id")
                (row.childNodes[1] as Element).addClass("family-name")
                (row.childNodes[2] as Element).addClass("family-candy-evolve")
                (row.childNodes[3] as Element).addClass("family-notes")
            }

            for (i in 0..1) {
                val cell = row.childNodes[i] as Element
                cell.addClass(if (member.present) "success" else "fail")
                if (member.number == familyView.number) {
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

private fun String?.isFormNormal() = this.isNullOrBlank() || (this == "normal")

private fun PokemonView.nameWithForm() = name + (form?.let { " ($it)" } ?: "")

private fun FamilyView.hasDistance() =
        distanceCandies.isGreaterZero() || distanceGeneral.isGreaterZero() || distanceWithRareGeneral.isGreaterZero()
