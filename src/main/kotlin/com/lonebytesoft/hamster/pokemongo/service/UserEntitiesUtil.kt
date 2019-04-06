package com.lonebytesoft.hamster.pokemongo.service

internal fun <K, U, T> mergeUserItemsWithBase(
        userItems: Iterable<U>, base: Iterable<T>,
        idExtractorUser: (U) -> K, idExtractorBase: (T) -> K, mapper: (T) -> U
): Collection<U> {
    val ids = userItems.map(idExtractorUser)
    return userItems + base
            .filter { idExtractorBase(it) !in ids }
            .map(mapper)
}
