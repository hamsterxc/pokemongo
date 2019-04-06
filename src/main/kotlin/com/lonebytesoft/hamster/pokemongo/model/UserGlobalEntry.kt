package com.lonebytesoft.hamster.pokemongo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "user_global")
data class UserGlobalEntry (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Int = 0,
        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id")
        var user: User = User(),

        var key: String = "",
        var value: String? = null
)
