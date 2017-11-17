package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class UserPokemon(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int = 0,
        @ManyToOne(optional = false)
        @JoinColumn(name = "pokemon_id")
        var pokemon: Pokemon = Pokemon(),
        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id")
        var user: User = User(),

        var seen: Int = 0,
        var caught: Int = 0,
        var have: Int = 0,
        var candy: Int = 0,
        var introduced: Double = 0.0
)
