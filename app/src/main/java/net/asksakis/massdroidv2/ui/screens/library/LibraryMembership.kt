package net.asksakis.massdroidv2.ui.screens.library

import net.asksakis.massdroidv2.domain.model.Album
import net.asksakis.massdroidv2.domain.model.Artist

/**
 * Library membership signal, mirroring how the MA web UI gates its add/remove-from-library
 * actions: an item is in the library when its (resolved) provider is `library`, i.e. its URI
 * is a `library://` URI. Provider-specific items (spotify, deezer, ...) are not yet in the
 * library and can be added.
 */
private fun isLibraryMember(provider: String?, uri: String?): Boolean =
    provider == "library" || uri?.startsWith("library://") == true

internal fun Album?.isInLibrary(): Boolean = isLibraryMember(this?.provider, this?.uri)
internal fun Artist?.isInLibrary(): Boolean = isLibraryMember(this?.provider, this?.uri)
