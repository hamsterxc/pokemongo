package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class UserFamily(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int = 0,

        @ManyToOne(optional = false)
        @JoinColumn
        var family: Family = Family(),

        @ManyToOne(optional = false)
        @JoinColumn
        var user: User = User(),

        var candy: Int = 0
)
