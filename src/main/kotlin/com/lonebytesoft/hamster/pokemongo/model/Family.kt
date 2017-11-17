package com.lonebytesoft.hamster.pokemongo.model

data class Family (
        val lead: Pokemon,
        val members: Set<Pokemon>
)
