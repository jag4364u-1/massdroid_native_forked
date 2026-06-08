package net.asksakis.massdroidv2.tv.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Offset-based forward pager over a :core library endpoint, shared by the home
 * rows and the Browse grids. De-duplicates by [key] when appending so a LazyList
 * never sees a duplicate stable key, and stops once a short/empty page signals
 * the end (no fixed cap — the whole library is reachable by paging).
 */
internal class LibraryPager<T>(
    private val scope: CoroutineScope,
    private val key: (T) -> Any,
    private val pageSize: Int = 50,
    private val fetch: suspend (limit: Int, offset: Int) -> List<T>,
) {
    private val _items = MutableStateFlow<List<T>>(emptyList())
    val items: StateFlow<List<T>> = _items.asStateFlow()

    private var offset = 0
    private var endReached = false
    private var loading = false

    fun loadMore() {
        if (loading || endReached) return
        loading = true
        scope.launch {
            val page = runCatching { fetch(pageSize, offset) }.getOrDefault(emptyList())
            if (page.size < pageSize) endReached = true
            if (page.isNotEmpty()) {
                offset += page.size
                val seen = _items.value.mapTo(HashSet(), key)
                val fresh = page.filter { seen.add(key(it)) }
                if (fresh.isNotEmpty()) _items.value = _items.value + fresh
            }
            loading = false
        }
    }

    /** Kick the first page when a category is opened for the first time. */
    fun loadMoreIfEmpty() {
        if (_items.value.isEmpty()) loadMore()
    }
}
