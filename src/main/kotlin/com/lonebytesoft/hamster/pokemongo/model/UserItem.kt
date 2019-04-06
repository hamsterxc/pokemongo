package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class UserItem(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int = 0,
        @ManyToOne(optional = false)
        @JoinColumn(name = "item_id")
        var item: Item = Item(),
        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id")
        var user: User = User(),

        var have: Int = 0,
        var introduced: Double = 0.0
)
