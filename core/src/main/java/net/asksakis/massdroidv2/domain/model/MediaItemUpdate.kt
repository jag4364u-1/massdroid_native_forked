package net.asksakis.massdroidv2.domain.model

/**
 * A `media_item_updated` server event mapped to the domain item it carries. Consumers patch the
 * matching row in place (stable URI keys), so metadata changes (e.g. artwork fetched after a
 * detail refresh) reach already-loaded lists without a reload or scroll reset.
 */
sealed interface MediaItemUpdate {
    data class ArtistUpdated(val item: Artist) : MediaItemUpdate
    data class AlbumUpdated(val item: Album) : MediaItemUpdate
    data class TrackUpdated(val item: Track) : MediaItemUpdate
    data class AudiobookUpdated(val item: Track) : MediaItemUpdate
    data class PlaylistUpdated(val item: Playlist) : MediaItemUpdate
    data class RadioUpdated(val item: Radio) : MediaItemUpdate
}
