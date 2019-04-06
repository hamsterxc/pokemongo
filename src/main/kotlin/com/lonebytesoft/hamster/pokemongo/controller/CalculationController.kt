package com.lonebytesoft.hamster.pokemongo.controller

import com.lonebytesoft.hamster.pokemongo.math.LinearEquation
import com.lonebytesoft.hamster.pokemongo.math.SolutionType
import com.lonebytesoft.hamster.pokemongo.math.solve
import com.lonebytesoft.hamster.pokemongo.model.Global
import com.lonebytesoft.hamster.pokemongo.model.Item
import com.lonebytesoft.hamster.pokemongo.model.Pokemon
import com.lonebytesoft.hamster.pokemongo.model.UserItem
import com.lonebytesoft.hamster.pokemongo.model.UserPokemon
import com.lonebytesoft.hamster.pokemongo.service.FamilyService
import com.lonebytesoft.hamster.pokemongo.service.GlobalService
import com.lonebytesoft.hamster.pokemongo.service.ItemService
import com.lonebytesoft.hamster.pokemongo.service.PokemonService
import com.lonebytesoft.hamster.pokemongo.view.CalculationView
import com.lonebytesoft.hamster.pokemongo.view.FamilyView
import com.lonebytesoft.hamster.pokemongo.view.ItemView
import com.lonebytesoft.hamster.pokemongo.view.PokemonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class CalculationController
@Autowired
constructor(
        private val globalService: GlobalService,
        private val familyService: FamilyService,
        private val pokemonService: PokemonService,
        private val itemService: ItemService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/calculate/{userId}"))
    fun calculateDistance(@PathVariable userId: Int): CalculationView {
        val global = globalService.get(userId)
        val families = familyService.getFamilies(userId)
        val pokemons = pokemonService.getPokemons(userId)
        val items = itemService.getItems(userId)

        val itemPoolInitial = items.associateBy({ it.item }, { it.have })
        val itemPool: MutableMap<Item, Int> = HashMap(itemPoolInitial)

        val calculationFamilyViews = families.map { family ->
            val members = pokemons.filter { it.pokemon.family?.number == family.family.number }
            val pokemonViews: MutableCollection<PokemonView> = ArrayList()

            val pokemonPool: MutableMap<Pokemon, Int> = members.associateByTo(HashMap(), { it.pokemon }, { it.have })
            val itemPoolFamily: MutableMap<Item, Int> = HashMap(itemPoolInitial)
            var candiesNeeded = 0
            var pokemonDistance: Double? = 0.0

            for(member in members) {
                val pokemon = member.pokemon
                var itemAbsent = false
                var current: Pokemon? = pokemon
                while(current != null) {
                    val currentPokemonCount = pokemonPool[current] ?: 0
                    if(currentPokemonCount > 0) {
                        pokemonPool[current] = currentPokemonCount - 1
                        break
                    } else {
                        val previous = current.evolveFrom
                        if(previous != null) {
                            candiesNeeded += current.evolveCandy ?: 0
                            current.evolveItem?.let {
                                val itemCount = itemPoolFamily[it] ?: 0
                                itemAbsent = itemAbsent || (itemCount <= 0)
                                itemPoolFamily[it] = itemCount - 1
                                itemPool[it] = (itemPool[it] ?: 0) - 1
                            }
                        }
                        current = previous
                    }
                }

                val rank = members
                        .filter { it.pokemon.name == pokemon.name }
                        .map {
                            var rank = 0
                            var evolutionPokemon: Pokemon? = it.pokemon
                            while(evolutionPokemon != null) {
                                rank++
                                evolutionPokemon = evolutionPokemon.evolveFrom
                            }
                            rank
                        }
                        .max() ?: 0

                pokemonViews.add(PokemonView(
                        number = pokemon.number,
                        name = pokemon.name,
                        form = pokemon.form,
                        rank = rank,
                        isPresent = (current != null) && !itemAbsent,
                        notes = pokemon.notes,
                        evolveCandy = pokemon.evolveCandy,
                        evolveNotes = pokemon.evolveNotes
                ))

                if(current == null) {
                    var speed = 0.0
                    var evolution: UserPokemon? = member
                    while(evolution != null) {
                        if (global.walked > evolution.introduced) {
                            speed += evolution.caught.toDouble() / (global.walked - evolution.introduced)
                        }
                        evolution = pokemons.firstOrNull { it.pokemon.id == evolution!!.pokemon.evolveFrom?.id }
                    }

                    if(speed > 0.0) {
                        if(pokemonDistance != null) {
                            pokemonDistance += 1.0 / speed
                        }
                    } else {
                        pokemonDistance = null
                    }
                }
            }
            candiesNeeded = maxOf(0, candiesNeeded - family.candy)

            val speedCommon = calculateSpeedCommon(global, members) +
                    (family.family.buddyDistance?.let { if (it > 0.0) 1.0 / it else 0.0 } ?: 0.0)
            val calculationItemViews = buildCalculationItemViews(global, items, itemPoolFamily)
            FamilyView(
                    number = family.family.number,
                    name = family.family.name,
                    pokemons = pokemonViews,
                    buddyDistance = family.family.buddyDistance ?: 0.0,
                    candiesNeeded = candiesNeeded,
                    distanceGeneral = null,
                    distanceWithRareGeneral = null,
                    rareToUseGeneral = null,
                    distancePokemons = pokemonDistance,
                    distanceCandies = if (speedCommon > 0.0) candiesNeeded / speedCommon else null,
                    distanceCandiesWithRare = if (speedCommon > 0.0) maxOf(0, candiesNeeded - global.rareCandy) / speedCommon else null,
                    rareToUse = minOf(candiesNeeded, global.rareCandy),
                    itemsNeeded = calculationItemViews,
                    distanceItems = calculationItemViews.map { it.distance }.filterNotNull().sum()
            )
        }

        val candyViews = buildCommonCalculationCandyViews(global, calculationFamilyViews)
        return CalculationView(
                buildCalculationItemViews(global, items, itemPool),
                calculationFamilyViews.map {
                    val commonView = candyViews.firstOrNull { candyView -> candyView.number == it.number }
                    it.copy(
                            distanceGeneral = commonView?.distance,
                            distanceWithRareGeneral = commonView?.distanceWithRare,
                            rareToUseGeneral = commonView?.rareToUse
                    )
                }
        )
    }

    private fun calculateSpeedCommon(global: Global, members: Collection<UserPokemon>) =
            members
                    .sumByDouble {
                        if(global.walked <= it.introduced) {
                            0.0
                        } else {
                            it.caught.toDouble() * (it.pokemon.candyCatch + it.pokemon.candyTransfer) / (global.walked - it.introduced)
                        }
                    }

    private fun buildCommonCalculationCandyViews(
            global: Global,
            families: Collection<FamilyView>
    ): Collection<CalculationGeneral> {
        val equationsData = families
                .filter { (it.candiesNeeded > 0) && (it.distanceCandies != null) && it.pokemons.any { it.isPresent } }
                .associate {
                    val buddySpeed = 1.0 / it.buddyDistance
                    val equationData = EquationData(
                            it.candiesNeeded / it.distanceCandies!! - buddySpeed, buddySpeed, it.candiesNeeded)
                    Pair(it.number, equationData)
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

        return distances.keys.map { CalculationGeneral(
                    number = it,
                    distance = distances[it],
                    distanceWithRare = distancesMin[it],
                    rareToUse = equationsData[it]!!.candy - equationsDataMin[it]!!.candy
        ) }
    }

    private fun copyEquationsData(equationsData: Map<Int, EquationData>): Map<Int, EquationData> {
        return equationsData.entries
                .associate { Pair(it.key, EquationData(it.value.speedCommon, it.value.speedBuddy, it.value.candy)) }
    }

    private fun calculateSolution(equationsData: Map<Int, EquationData>): Map<Int, Double?> {
        val numbers = ArrayList(equationsData.keys)
        val equations = numbers.mapIndexed { index, number ->
            val coeffs = numbers.mapIndexed { indexInner, numberInner ->
                val equationData = equationsData[numberInner]
                if (indexInner == index) equationData!!.speedCommon + equationData.speedBuddy else equationData!!.speedCommon
            }
            LinearEquation(coeffs, equationsData[number]!!.candy.toDouble())
        }
        val solution = solve(equations)

        if(solution.type == SolutionType.UNIQUE) {
            val distances: MutableMap<Int, Double?> = HashMap()
            return if(solution.values.all { it > 0 }) {
                solution.values
                        .mapIndexed { index, distance -> Pair(numbers[index], distance) }
                        .associate { it }
            } else {
                val equationsDataNew: MutableMap<Int, EquationData> = HashMap()
                solution.values.forEachIndexed { index, distance ->
                    if(distance > 0) {
                        equationsDataNew += Pair(numbers[index], equationsData[numbers[index]]!!)
                    } else {
                        distances += Pair(numbers[index], 0.0)
                    }
                }
                distances + calculateSolution(equationsDataNew)
            }
        } else {
            return numbers.associate { Pair(it, null) }
        }
    }

    private fun buildCalculationItemViews(global: Global, items: Collection<UserItem>, pool: Map<Item, Int>): Collection<ItemView> =
            pool
                    .filterValues { it < 0 }
                    .map { entry ->
                        val item = items.first { it.item == entry.key }.item
                        ItemView(
                                id = item.id,
                                name = item.name,
                                count = -entry.value,
                                distance = if (global.pokestop * item.probability == 0.0) null else -entry.value * global.walked / (global.pokestop * item.probability)
                        )
                    }

    private data class EquationData(
            val speedCommon: Double,
            val speedBuddy: Double,
            var candy: Int
    )

    private data class CalculationGeneral(
            val number: Int,
            val distance: Double?,
            val distanceWithRare: Double?,
            val rareToUse: Int?
    )

}
