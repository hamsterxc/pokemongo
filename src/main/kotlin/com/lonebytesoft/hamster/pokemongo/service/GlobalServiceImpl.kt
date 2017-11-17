package com.lonebytesoft.hamster.pokemongo.service

import com.lonebytesoft.hamster.pokemongo.model.Global
import com.lonebytesoft.hamster.pokemongo.model.GlobalEntry
import com.lonebytesoft.hamster.pokemongo.repository.GlobalEntryRepository
import com.lonebytesoft.hamster.pokemongo.repository.UserGlobalEntryRepository
import org.springframework.stereotype.Component

@Component
class GlobalServiceImpl(
        private val globalEntryRepository: GlobalEntryRepository,
        private val userGlobalEntryRepository: UserGlobalEntryRepository
) : GlobalService {

    private val mappers: Map<String, GlobalEntryMapper> = mapOf(
            "walked" to GlobalEntryMapper(
                    { global, value -> global.walked = value?.toDouble() ?: 0.0 },
                    { global -> global.walked.toString() }
            ),
            "pokestop" to GlobalEntryMapper(
                    { global, value -> global.pokestop = value?.toInt() ?: 0 },
                    { global -> global.pokestop.toString() }
            ),
            "rare_candy" to GlobalEntryMapper(
                    { global, value -> global.rareCandy = value?.toInt() ?: 0 },
                    { global -> global.rareCandy.toString() }
            )
    )

    override fun get(userId: Int): Global {
        val global = Global()
        (
                globalEntryRepository.findAll()
                + userGlobalEntryRepository.findAllByUserIdEquals(userId).map { GlobalEntry(it.key, it.value) }
                )
                .forEach { mappers[it.key]?.read?.invoke(global, it.value) }
        return global
    }

    override fun save(userId: Int, global: Global) {
        userGlobalEntryRepository.findAllByUserIdEquals(userId)
                .forEach {
                    if(it.key in mappers) {
                        it.value = mappers[it.key]!!.write.invoke(global)
                        userGlobalEntryRepository.save(it)
                    }
                }
        globalEntryRepository.findAll()
                .forEach {
                    if(it.key in mappers) {
                        it.value = mappers[it.key]!!.write.invoke(global)
                        globalEntryRepository.save(it)
                    }
                }
    }

    private class GlobalEntryMapper (
            val read: (Global, String?) -> Unit,
            val write: (Global) -> String?
    )

}
