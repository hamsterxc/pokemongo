package com.lonebytesoft.hamster.pokemongo.controller

import com.lonebytesoft.hamster.pokemongo.math.LinearEquation
import com.lonebytesoft.hamster.pokemongo.math.SolutionType
import com.lonebytesoft.hamster.pokemongo.math.solve
import com.lonebytesoft.hamster.pokemongo.model.Family
import com.lonebytesoft.hamster.pokemongo.model.Global
import com.lonebytesoft.hamster.pokemongo.model.Pokemon
import com.lonebytesoft.hamster.pokemongo.model.UserItem
import com.lonebytesoft.hamster.pokemongo.model.UserPokemon
import com.lonebytesoft.hamster.pokemongo.service.GlobalService
import com.lonebytesoft.hamster.pokemongo.service.ItemService
import com.lonebytesoft.hamster.pokemongo.service.PokemonService
import com.lonebytesoft.hamster.pokemongo.view.CalculationCandyView
import com.lonebytesoft.hamster.pokemongo.view.CalculationFamilyView
import com.lonebytesoft.hamster.pokemongo.view.CalculationItemView
import com.lonebytesoft.hamster.pokemongo.view.CalculationMemberView
import com.lonebytesoft.hamster.pokemongo.view.CalculationView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.Collections

@RestController
class CalculationController
@Autowired
constructor(
        private val globalService: GlobalService,
        private val pokemonService: PokemonService,
        private val itemService: ItemService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/calculate/{userId}"))
    fun calculateDistance(@PathVariable userId: Int): CalculationView {
        val global = globalService.get(userId)
        val pokemons = pokemonService.getPokemons(userId)
        val items = itemService.getItems(userId)
        val itemPoolInitial = items.mapValues { it.value.have }
        val itemPool: MutableMap<Int, Int> = HashMap(itemPoolInitial)

        val families: MutableCollection<CalculationFamilyView> = ArrayList()
        pokemonService.calculateFamilies().forEach { _, family ->
            val memberViews: MutableCollection<CalculationMemberView> = ArrayList()
            var candiesNeeded = 0
            var pokemonDistance: Double? = 0.0

            val pokemonPool: MutableMap<Int, Int> = family.members
                    .associateByTo(HashMap(), { it.id }, { if (it.id in pokemons) pokemons[it.id]!!.have else 0 })
            val itemPoolFamily: MutableMap<Int, Int> = HashMap(itemPoolInitial)

            var candiesHave = 0
            for(member in family.members) {
                var current: Pokemon? = member
                while(current != null) {
                    val currentPokemonCount = pokemonPool[current.id] ?: 0
                    if(currentPokemonCount > 0) {
                        pokemonPool[current.id] = currentPokemonCount - 1
                        break
                    } else {
                        val previous = current.evolveFrom
                        if(previous != null) {
                            candiesNeeded += current.evolveCandy ?: 0
                            if(current.evolveItem != null) {
                                val itemId = current.evolveItem!!.id
                                itemPoolFamily[itemId] = (itemPoolFamily[itemId] ?: 0) - 1
                                itemPool[itemId] = (itemPool[itemId] ?: 0) - 1
                            }
                        }
                        current = previous
                    }
                }

                memberViews.add(CalculationMemberView(member.id, current != null))
                if(current == null) {
                    var speed = 0.0
                    var evolution: Pokemon? = member
                    while(evolution != null) {
                        val pokemon = pokemons[evolution.id]
                        speed += (pokemon?.caught?.toDouble() ?: 0.0) / (global.walked - (pokemon?.introduced ?: 0.0))
                        evolution = evolution.evolveFrom
                    }

                    if(speed > 0.0) {
                        if(pokemonDistance != null) {
                            pokemonDistance += 1.0 / speed
                        }
                    } else {
                        pokemonDistance = null
                    }
                }

                candiesHave += pokemons[member.id]?.candy ?: 0
            }
            candiesNeeded = maxOf(0, candiesNeeded - candiesHave)

            val lead = family.lead.id
            val candyView = buildCalculationCandyView(global, pokemons, family, candiesNeeded)
            val itemView = buildCalculationItemView(global, items, itemPoolFamily)
            families.add(CalculationFamilyView(lead, memberViews, candyView, itemView, pokemonDistance))
        }

        val candyViews = buildCommonCalculationCandyViews(global, pokemons, families)
        val itemView = buildCalculationItemView(global, items, itemPool)
        return CalculationView(families, candyViews, itemView)
    }

    private fun buildCalculationCandyView(global: Global, pokemons: Map<Int, UserPokemon>,
                                          family: Family, candiesNeeded: Int): CalculationCandyView {
        val members = family.members
                .mapNotNull { pokemons[it.id] }
        return if(candiesNeeded > 0) {
            val speed = calculateSpeedCommon(global, members) + calculateSpeedBuddy(members)
            if(speed > 0.0) {
                CalculationCandyView(family.lead.id, candiesNeeded,
                        candiesNeeded / speed,
                        minOf(candiesNeeded, global.rareCandy),
                        if (candiesNeeded > global.rareCandy) (candiesNeeded - global.rareCandy) / speed else 0.0)
            } else {
                if(candiesNeeded > global.rareCandy) {
                    CalculationCandyView(family.lead.id, candiesNeeded, null, global.rareCandy, null)
                } else {
                    CalculationCandyView(family.lead.id, candiesNeeded, null, candiesNeeded, 0.0)
                }
            }
        } else {
            CalculationCandyView(family.lead.id, 0, 0.0, 0, 0.0)
        }
    }

    private fun calculateSpeedCommon(global: Global, members: Collection<UserPokemon>): Double {
        return members
                .map {
                    if(global.walked <= it.introduced) {
                        0.0
                    } else {
                        it.caught.toDouble() * (it.pokemon.candyCatch + it.pokemon.candyTransfer) / (global.walked - it.introduced)
                    }
                }
                .sum()
    }

    private fun calculateSpeedBuddy(members: Collection<UserPokemon>): Double {
        val buddyDistanceMin = members
                .map { it.pokemon.buddyDistance }
                .min()
        return if (buddyDistanceMin == null) 0.0 else 1.0 / buddyDistanceMin
    }

    private fun buildCommonCalculationCandyViews(global: Global, pokemons: Map<Int, UserPokemon>,
                                                 families: Collection<CalculationFamilyView>): Collection<CalculationCandyView> {
        val equationsData = families
                .filter { it.candyNeeded.candy > 0 }
                .associate {
                    val members = it.members
                            .mapNotNull { pokemons[it.id] }
                    val equationData = EquationData(
                            calculateSpeedCommon(global, members), calculateSpeedBuddy(members), it.candyNeeded.candy)
                    Pair(it.lead, equationData)
                }
        val distances = calculateSolution(equationsData)

        var equationsDataMin = copyEquationsData(equationsData)
        var distancesMin = distances
        for(i in 0 until global.rareCandy) {
            var distanceMin = Double.MAX_VALUE
            var equationsDataNew = equationsDataMin
            equationsDataMin
                    .forEach { _, equationData ->
                        equationData.candy -= 1
                        val distancesWithOneRare = calculateSolution(equationsDataMin)
                        val distanceWithOneRare = distancesWithOneRare.values.sumByDouble { it ?: 0.0 }
                        if(distanceWithOneRare < distanceMin) {
                            equationsDataNew = copyEquationsData(equationsDataMin)
                            distancesMin = distancesWithOneRare
                            distanceMin = distanceWithOneRare
                        }
                        equationData.candy += 1
                    }
            equationsDataMin = equationsDataNew
        }

        return families
                .filter { it.candyNeeded.candy > 0 }
                .map { CalculationCandyView(it.lead, it.candyNeeded.candy, distances[it.lead],
                        equationsData[it.lead]!!.candy - equationsDataMin[it.lead]!!.candy, distancesMin[it.lead]) }
    }

    private fun copyEquationsData(equationsData: Map<Int, EquationData>): Map<Int, EquationData> {
        return equationsData.entries
                .associate { Pair(it.key, EquationData(it.value.speedCommon, it.value.speedBuddy, it.value.candy)) }
    }

    private fun calculateSolution(equationsData: Map<Int, EquationData>): Map<Int, Double?> {
        val leads = ArrayList(equationsData.keys)
        val equations = leads.mapIndexed { index, lead ->
            val coeffs = leads.mapIndexed { indexInner, leadInner ->
                val equationData = equationsData[leadInner]
                if (indexInner == index) equationData!!.speedCommon + equationData!!.speedBuddy else equationData!!.speedCommon
            }
            LinearEquation(coeffs, equationsData[lead]!!.candy.toDouble())
        }
        val solution = solve(equations)

        if(solution.type == SolutionType.UNIQUE) {
            val distances: MutableMap<Int, Double?> = HashMap()
            return if(solution.values.all { it > 0 }) {
                solution.values
                        .mapIndexed { index, distance -> Pair(leads[index], distance) }
                        .associate { it }
            } else {
                val equationsDataNew: MutableMap<Int, EquationData> = HashMap()
                solution.values.forEachIndexed { index, distance ->
                    if(distance > 0) {
                        equationsDataNew += Pair(leads[index], equationsData[leads[index]]!!)
                    } else {
                        distances += Pair(leads[index], 0.0)
                    }
                }
                distances + calculateSolution(equationsDataNew)
            }
        } else {
            return leads.associate { Pair(it, null) }
        }
    }

    private fun buildCalculationItemView(global: Global, items: Map<Int, UserItem>, pool: Map<Int, Int>): CalculationItemView {
        val id = pool.entries
                .filter { it.value < 0 }
                .flatMap { Collections.nCopies(-it.value, it.key) }
        return if(id
                .distinct()
                .map { items[it] }
                .any { (it == null) || (it.item.probability == 0.0) }) {
            CalculationItemView(id, null)
        } else {
            val distance = id
                    .map { global.walked / (global.pokestop * items[it]!!.item.probability) }
                    .sum()
            CalculationItemView(id, distance)
        }
    }

    private data class EquationData(
            val speedCommon: Double,
            val speedBuddy: Double,
            var candy: Int
    )

}
