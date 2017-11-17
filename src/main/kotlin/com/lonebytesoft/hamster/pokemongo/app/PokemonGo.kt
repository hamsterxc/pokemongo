package com.lonebytesoft.hamster.pokemongo.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan("com.lonebytesoft.hamster.pokemongo")
@EnableJpaRepositories("com.lonebytesoft.hamster.pokemongo.repository")
@EntityScan("com.lonebytesoft.hamster.pokemongo.model")
open class PokemonGo {
}

fun main(args: Array<String>) {
    SpringApplication.run(PokemonGo::class.java, *args)
}
