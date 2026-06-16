package net.asksakis.massdroidv2.ui.screens.home

import android.util.Log
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import net.asksakis.massdroidv2.data.genre.GenreRepository
import net.asksakis.massdroidv2.data.lastfm.LastFmGenreResolver
import net.asksakis.massdroidv2.data.lastfm.LastFmSimilarResolver
import net.asksakis.massdroidv2.data.lastfm.SimilarArtist
import net.asksakis.massdroidv2.data.util.ProviderHealthReporter
import net.asksakis.massdroidv2.domain.model.Album
import net.asksakis.massdroidv2.domain.model.Artist
import net.asksakis.massdroidv2.domain.model.RecommendationFolder
import net.asksakis.massdroidv2.domain.model.RecommendationItems
import net.asksakis.massdroidv2.domain.recommendation.RecommendationEngine
import net.asksakis.massdroidv2.domain.repository.ArtistScore
import net.asksakis.massdroidv2.domain.repository.GenreScore
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayHistoryRepository
import net.asksakis.massdroidv2.domain.repository.RecentAlbum
import net.asksakis.massdroidv2.domain.repository.SearchResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class DiscoverRecommendationOrchestratorTest {

    private val music: MusicRepository = mockk()
    private val playHistory: PlayHistoryRepository = mockk()
    private val genre: GenreRepository = mockk()
    private val engine: RecommendationEngine = mockk(relaxed = true)
    private val similar: LastFmSimilarResolver = mockk()
    private val genreResolver: LastFmGenreResolver = mockk()
    private val health: ProviderHealthReporter = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>(), any()) } returns 0

        // Make randomness deterministic: no score/MMR jitter, deterministic seed sampling.
        mockkObject(Random.Default)
        every { Random.nextDouble(any(), any()) } returns 0.0
        every { Random.nextInt(any()) } returns 0

        coEvery { playHistory.getScoredArtists(any(), any()) } returns emptyList()
        coEvery { playHistory.getRecentAlbums(any()) } returns emptyList()
        coEvery { genre.scoredGenres(any(), any()) } returns emptyList()
        coEvery { genre.genreArtistMap() } returns emptyMap()
        coEvery { similar.resolve(any(), any()) } returns emptyList()
        coEvery { genreResolver.resolve(any()) } returns emptyList()
        coEvery { music.search(any(), any(), any()) } returns SearchResult()
        coEvery { music.getArtist(any(), any()) } returns null
        coEvery { music.getArtistAlbums(any(), any()) } returns emptyList()
    }

    @AfterEach
    fun tearDown() = unmockkAll()

    // ---------------------------------------------------------------------------------------
    // Domain object builders
    // ---------------------------------------------------------------------------------------

    private fun score(name: String, s: Double, uri: String = "spotify://artist/$name") =
        ArtistScore(artistUri = uri, artistName = name, score = s)

    private fun sim(name: String, match: Double) = SimilarArtist(name = name.lowercase(), matchScore = match)

    /** Default genre is unique-per-name so each candidate is its own genre family (cap is a no-op). */
    private fun artist(
        name: String,
        itemId: String = name,
        provider: String = "spotify",
        uri: String = "spotify://artist/$itemId",
        imageUrl: String? = "http://img/$name",
        genres: List<String> = listOf("fam-$name")
    ) = Artist(
        itemId = itemId,
        provider = provider,
        name = name,
        uri = uri,
        imageUrl = imageUrl,
        genres = genres
    )

    private fun album(
        name: String,
        itemId: String = name,
        provider: String = "spotify",
        uri: String = "spotify://album/$itemId",
        imageUrl: String? = "http://img/$name",
        albumType: String? = "album",
        genres: List<String> = emptyList(),
        artists: List<Artist> = emptyList()
    ) = Album(
        itemId = itemId,
        provider = provider,
        name = name,
        uri = uri,
        imageUrl = imageUrl,
        albumType = albumType,
        genres = genres,
        artists = artists
    )

    private fun folder(
        itemId: String,
        provider: String = "spotify",
        artists: List<Artist> = emptyList(),
        albums: List<Album> = emptyList()
    ) = RecommendationFolder(
        itemId = itemId,
        name = itemId,
        provider = provider,
        items = RecommendationItems(artists = artists, albums = albums)
    )

    private fun recent(uri: String) =
        RecentAlbum(albumName = uri, albumUri = uri, imageUrl = null, year = null, lastPlayedAt = 0L)

    // ---------------------------------------------------------------------------------------
    // Stubbing helpers
    // ---------------------------------------------------------------------------------------

    private fun givenSeeds(vararg s: ArtistScore) {
        coEvery { playHistory.getScoredArtists(any(), any()) } returns s.toList()
    }

    private fun givenSimilars(seedName: String, vararg s: SimilarArtist) {
        coEvery { similar.resolve(seedName, any()) } returns s.toList()
    }

    /** The orchestrator searches by the lower-cased candidate name; the hit must name-match. */
    private fun givenResolved(a: Artist) {
        coEvery { music.search(a.name.lowercase(), any(), any()) } returns SearchResult(artists = listOf(a))
    }

    /** Wire one seed with many resolvable similars at once. */
    private fun wireSeed(seed: ArtistScore, vararg candidates: Pair<Artist, Double>) {
        givenSimilars(seed.artistName, *candidates.map { sim(it.first.name, it.second) }.toTypedArray())
        candidates.forEach { givenResolved(it.first) }
    }

    private fun givenArtistAlbums(a: Artist, vararg albums: Album) {
        coEvery { music.getArtistAlbums(a.itemId, a.provider) } returns albums.toList()
    }

    private fun orchestrator() = DiscoverRecommendationOrchestrator(
        musicRepository = music,
        playHistoryRepository = playHistory,
        genreRepository = genre,
        recommendationEngine = engine,
        lastFmSimilarResolver = similar,
        lastFmGenreResolver = genreResolver,
        providerHealthReporter = health
    )

    private suspend fun build(
        libraryArtists: List<Artist> = emptyList(),
        serverFolders: List<RecommendationFolder> = emptyList(),
        excludedArtistUris: Set<String> = emptySet(),
        artistCount: Int = 10,
        albumCount: Int = 10
    ): DiscoveryResult = orchestrator().buildDiscovery(
        libraryArtists = libraryArtists,
        serverFolders = serverFolders,
        excludedArtistUris = excludedArtistUris,
        artistCount = artistCount,
        albumCount = albumCount
    )

    private fun DiscoveryResult.artistNames() = artists.map { it.name }
    private fun DiscoveryResult.albumNames() = albums.map { it.name }

    // =======================================================================================
    // Seed selection
    // =======================================================================================

    @Test
    fun `no scored artists yields empty result`() = runTest {
        givenSeeds()

        val result = build()

        assertTrue(result.artists.isEmpty())
        assertTrue(result.albums.isEmpty())
    }

    @Test
    fun `only the first DISCOVERY_SEED_LIMIT (8) scored artists are used as seeds`() = runTest {
        // 12 scored artists -> seeds are the first 8 (scored-genres empty => no family sampling).
        val seeds = (1..12).map { score("Seed$it", 100.0 - it) }
        givenSeeds(*seeds.toTypedArray())

        // Each seed (in and out of the limit) recommends one unique resolvable candidate.
        seeds.forEachIndexed { idx, s ->
            wireSeed(s, artist("Cand${idx + 1}") to 0.9)
        }

        val names = build(artistCount = 12).artistNames()

        assertEquals(8, names.size)
        (1..8).forEach { assertTrue("Cand$it" in names, "Cand$it should be present") }
        (9..12).forEach { assertFalse("Cand$it" in names, "Cand$it should be excluded") }
    }

    @Test
    fun `genre-family seed path drives candidates when scored genres are present`() = runTest {
        // Two rock-family genres + one electronic genre; the family sampler picks the top-scored
        // artist of each family, whose similars must reach the output.
        givenSeeds(
            score("RockTop", 99.0, uri = "spotify://artist/rocktop"),
            score("RockLow", 10.0, uri = "spotify://artist/rocklow"),
            score("EdmTop", 80.0, uri = "spotify://artist/edmtop")
        )
        coEvery { genre.scoredGenres(any(), any()) } returns listOf(
            GenreScore("rock", 9.0),
            GenreScore("alternative rock", 8.0),
            GenreScore("techno", 7.0)
        )
        coEvery { genre.genreArtistMap() } returns mapOf(
            "rock" to listOf("spotify://artist/rocktop"),
            "alternative rock" to listOf("spotify://artist/rocklow"),
            "techno" to listOf("spotify://artist/edmtop")
        )

        wireSeed(score("RockTop", 99.0, uri = "spotify://artist/rocktop"), artist("RockResult") to 0.9)
        wireSeed(score("EdmTop", 80.0, uri = "spotify://artist/edmtop"), artist("EdmResult") to 0.9)

        val names = build().artistNames()

        assertTrue("RockResult" in names)
        assertTrue("EdmResult" in names)
    }

    @Test
    fun `scoredGenres failure falls back to scored-artist seeds`() = runTest {
        val seeds = (1..5).map { score("Seed$it", 50.0 - it) }
        givenSeeds(*seeds.toTypedArray())
        coEvery { genre.scoredGenres(any(), any()) } throws RuntimeException("boom")
        seeds.forEachIndexed { idx, s -> wireSeed(s, artist("Resilient${idx + 1}") to 0.8) }

        val names = build().artistNames()

        (1..5).forEach { assertTrue("Resilient$it" in names) }
    }

    @Test
    fun `genreArtistMap failure still produces output via padding seeds`() = runTest {
        val seeds = (1..4).map { score("Seed$it", 40.0 - it) }
        givenSeeds(*seeds.toTypedArray())
        coEvery { genre.scoredGenres(any(), any()) } returns listOf(GenreScore("rock", 5.0))
        coEvery { genre.genreArtistMap() } throws RuntimeException("boom")
        seeds.forEachIndexed { idx, s -> wireSeed(s, artist("Padded${idx + 1}") to 0.8) }

        val names = build().artistNames()

        (1..4).forEach { assertTrue("Padded$it" in names) }
    }

    // =======================================================================================
    // Candidate pool
    // =======================================================================================

    @Test
    fun `similars with non-positive match score are dropped`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        wireSeed(
            seed,
            artist("Keep1") to 0.9,
            artist("Keep2") to 0.5,
            artist("Keep3") to 0.05,
            artist("ZeroMatch") to 0.0,
            artist("NegMatch") to -0.3
        )

        val names = build().artistNames()

        assertTrue("Keep1" in names && "Keep2" in names && "Keep3" in names)
        assertFalse("ZeroMatch" in names)
        assertFalse("NegMatch" in names)
    }

    @Test
    fun `similars matching library artist names are excluded before resolution`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val library = listOf(
            artist("Muse", uri = "library://artist/muse"),
            artist("Radiohead", uri = "library://artist/radiohead"),
            // a non-library uri must NOT contribute to library names
            artist("NotLibrary", uri = "spotify://artist/notlib")
        )
        wireSeed(
            seed,
            artist("Muse") to 0.9,
            artist("Radiohead") to 0.8,
            artist("NotLibrary") to 0.7,
            artist("FreshArtist") to 0.6
        )

        val names = build(libraryArtists = library).artistNames()

        assertFalse("Muse" in names)
        assertFalse("Radiohead" in names)
        assertTrue("NotLibrary" in names) // its library entry is not a library:// uri
        assertTrue("FreshArtist" in names)
    }

    @Test
    fun `candidates whose search returns no name-matching artist are dropped`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        givenSimilars(seed.artistName, sim("Wanted", 0.9), sim("AlsoWanted", 0.8), sim("Found", 0.7))
        givenResolved(artist("Found"))
        // "wanted" returns a differently-named artist; "alsowanted" returns nothing.
        coEvery { music.search("wanted", any(), any()) } returns
            SearchResult(artists = listOf(artist("Unrelated", uri = "spotify://artist/unrelated")))

        val names = build().artistNames()

        assertEquals(listOf("Found"), names)
    }

    @Test
    fun `resolved artists without image are filtered out`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        wireSeed(
            seed,
            artist("HasImg1") to 0.9,
            artist("NoImg1", imageUrl = null) to 0.8,
            artist("NoImg2", imageUrl = null) to 0.7,
            artist("HasImg2") to 0.6
        )

        val names = build().artistNames()

        assertTrue("HasImg1" in names && "HasImg2" in names)
        assertFalse("NoImg1" in names)
        assertFalse("NoImg2" in names)
    }

    @Test
    fun `resolved artists excluded by exact uri or canonical key are filtered out`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        wireSeed(
            seed,
            artist("ExactExcluded", uri = "spotify://artist/exact") to 0.9,
            // canonical key strips the fragment, matching the exclusion entry
            artist("FragExcluded", uri = "spotify://artist/frag#x") to 0.8,
            artist("Survivor1") to 0.7,
            artist("Survivor2") to 0.6
        )

        val names = build(
            excludedArtistUris = setOf("spotify://artist/exact", "spotify://artist/frag")
        ).artistNames()

        assertFalse("ExactExcluded" in names)
        assertFalse("FragExcluded" in names)
        assertTrue("Survivor1" in names && "Survivor2" in names)
    }

    @Test
    fun `candidates resolving to the same uri are de-duplicated`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        givenSimilars(
            seed.artistName,
            sim("A", 0.9), sim("B", 0.8), sim("C", 0.7), sim("D", 0.6)
        )
        // A, B, C all resolve to the same uri; D is distinct.
        coEvery { music.search("a", any(), any()) } returns
            SearchResult(artists = listOf(artist("A", uri = "spotify://artist/shared")))
        coEvery { music.search("b", any(), any()) } returns
            SearchResult(artists = listOf(artist("B", uri = "spotify://artist/shared")))
        coEvery { music.search("c", any(), any()) } returns
            SearchResult(artists = listOf(artist("C", uri = "spotify://artist/shared")))
        givenResolved(artist("D", uri = "spotify://artist/d"))

        val result = build()

        assertEquals(2, result.artists.size) // one for the shared uri, one for D
    }

    @Test
    fun `candidate resolution is capped at DISCOVERY_RESOLVE_BUDGET (25)`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        // 30 candidates, strictly-decreasing match, unique genre families (no per-family cap),
        // large artistCount (no final take) => only the resolve budget can drop candidates.
        val candidates = (1..30).map { artist("Cand$it") to (1.0 - it * 0.01) }
        wireSeed(seed, *candidates.toTypedArray())

        val names = build(artistCount = 30).artistNames()

        assertEquals(25, names.size)
        assertTrue("Cand1" in names)
        assertTrue("Cand25" in names)
        (26..30).forEach { assertFalse("Cand$it" in names, "Cand$it is beyond the budget") }
    }

    @Test
    fun `empty candidate pool yields empty result`() = runTest {
        val seeds = (1..5).map { score("Seed$it", 10.0 - it) }
        givenSeeds(*seeds.toTypedArray()) // seeds resolve no similars

        assertTrue(build().artists.isEmpty())
    }

    // =======================================================================================
    // Genre enrichment
    // =======================================================================================

    @Test
    fun `artists with existing genres are not enriched and artists without genres are enriched`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val withGenres = artist("HasGenres", genres = listOf("metal"))
        val noGenres1 = artist("NoGenres1", genres = emptyList())
        val noGenres2 = artist("NoGenres2", genres = emptyList())
        val enrichFails = artist("EnrichFails", genres = emptyList())
        wireSeed(
            seed,
            withGenres to 0.9,
            noGenres1 to 0.8,
            noGenres2 to 0.7,
            enrichFails to 0.6
        )
        coEvery { genreResolver.resolve("NoGenres1") } returns listOf("jazz")
        coEvery { genreResolver.resolve("NoGenres2") } returns listOf("ambient", "techno")
        coEvery { genreResolver.resolve("EnrichFails") } returns emptyList()

        val result = build()
        val byName = result.artists.associateBy { it.name }

        assertEquals(listOf("metal"), byName.getValue("HasGenres").genres)
        assertEquals(listOf("jazz"), byName.getValue("NoGenres1").genres)
        assertEquals(listOf("ambient", "techno"), byName.getValue("NoGenres2").genres)
        assertTrue(byName.getValue("EnrichFails").genres.isEmpty())
        coVerify(exactly = 0) { genreResolver.resolve("HasGenres") }
    }

    // =======================================================================================
    // Artist ranking (per-genre-family cap + MMR)
    // =======================================================================================

    @Test
    fun `per-genre-family cap keeps the top-2 of each family once the pool is large`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        // 6 families x 3 artists. The cap keeps the top-2 per family (=12 >= the MIN-for-ranking
        // threshold), so the lowest-scored artist of each family drops out.
        val families = listOf("rock", "metal", "pop", "jazz", "folk", "reggae")
        val candidates = mutableListOf<Pair<Artist, Double>>()
        families.forEach { fam ->
            listOf(0.9, 0.8, 0.1).forEachIndexed { idx, m ->
                candidates.add(artist("$fam$idx", genres = listOf(fam)) to m)
            }
        }
        wireSeed(seed, *candidates.toTypedArray())

        val names = build(artistCount = 12).artistNames()

        assertEquals(12, names.size)
        families.forEach { fam ->
            assertTrue("${fam}0" in names, "${fam}0 (0.9) kept")
            assertTrue("${fam}1" in names, "${fam}1 (0.8) kept")
            assertFalse("${fam}2" in names, "${fam}2 (0.1) dropped by family cap")
        }
    }

    @Test
    fun `highest composite-score artist is ranked first by MMR`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        // 8 distinct-family candidates; "Top" has the strongest match so it must lead.
        val candidates = buildList {
            add(artist("Top") to 0.99)
            (1..7).forEach { add(artist("Mid$it") to (0.5 - it * 0.01)) }
        }
        wireSeed(seed, *candidates.toTypedArray())

        val names = build().artistNames()

        assertEquals("Top", names.first())
    }

    @Test
    fun `vote count boosts composite score so a multi-voted candidate outranks a single-voter`() = runTest {
        // Three seeds recommend "Popular"; only one recommends "Niche" at a higher raw match.
        val seeds = (1..3).map { score("Seed$it", 5.0) }
        givenSeeds(*seeds.toTypedArray())
        val popular = artist("Popular")
        val niche = artist("Niche")
        seeds.forEach { givenSimilars(it.artistName, sim(popular.name, 0.6), sim("Filler${it.artistName}", 0.2)) }
        // Niche only from Seed1, plus its filler already added above -> re-stub Seed1 fully.
        givenSimilars("Seed1", sim(popular.name, 0.6), sim(niche.name, 0.7))
        givenResolved(popular)
        givenResolved(niche)

        val names = build().artistNames()

        assertEquals("Popular", names.first())
        assertTrue("Niche" in names)
    }

    // =======================================================================================
    // Provider artists fallback
    // =======================================================================================

    @Test
    fun `provider artist fallback fills artists when the Last fm pool is empty`() = runTest {
        givenSeeds(score("Seed", 5.0)) // resolves nothing
        val folders = listOf(
            folder("recommended_artists", artists = (1..8).map { artist("Fb$it", uri = "spotify://artist/fb$it") })
        )

        val names = build(serverFolders = folders).artistNames()

        assertEquals(8, names.size)
        (1..8).forEach { assertTrue("Fb$it" in names) }
    }

    @Test
    fun `provider artist fallback ignores folders with the wrong id or the library provider`() = runTest {
        givenSeeds(score("Seed", 5.0))
        val folders = listOf(
            folder("recommended_artists", provider = "library", artists = listOf(artist("FromLibrary"))),
            folder("some_other_folder", artists = listOf(artist("WrongFolder"))),
            folder("recommended_albums", artists = listOf(artist("WrongTypeFolder"))),
            folder("recommended_artists", provider = "spotify", artists = listOf(artist("Valid1"), artist("Valid2")))
        )

        val names = build(serverFolders = folders).artistNames()

        assertEquals(setOf("Valid1", "Valid2"), names.toSet())
    }

    @Test
    fun `provider artist fallback applies image, library, exclusion and dedupe filters`() = runTest {
        givenSeeds(score("Seed", 5.0))
        val library = listOf(
            artist("Known1", uri = "library://artist/known1"),
            artist("Known2", uri = "library://artist/known2")
        )
        val folders = listOf(
            folder(
                "recommended_artists",
                artists = listOf(
                    artist("NoImg", imageUrl = null),
                    artist("Known1"),
                    artist("Known2"),
                    artist("Excluded1", uri = "spotify://artist/ex1"),
                    artist("Excluded2", uri = "spotify://artist/ex2"),
                    artist("Dup", uri = "spotify://artist/dup"),
                    artist("Dup", itemId = "dup2", uri = "spotify://artist/dup"),
                    artist("Good1", uri = "spotify://artist/good1"),
                    artist("Good2", uri = "spotify://artist/good2")
                )
            )
        )

        val names = build(
            libraryArtists = library,
            serverFolders = folders,
            excludedArtistUris = setOf("spotify://artist/ex1", "spotify://artist/ex2")
        ).artistNames()

        assertEquals(listOf("Dup", "Good1", "Good2"), names)
    }

    @Test
    fun `provider artist fallback skips names already picked from the Last fm pool`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val shared1 = artist("Shared1", uri = "spotify://artist/lf1")
        val shared2 = artist("Shared2", uri = "spotify://artist/lf2")
        wireSeed(seed, shared1 to 0.9, shared2 to 0.8)
        val folders = listOf(
            folder(
                "recommended_artists",
                artists = listOf(
                    artist("Shared1", itemId = "fb1", uri = "spotify://artist/fb1"),
                    artist("Shared2", itemId = "fb2", uri = "spotify://artist/fb2"),
                    artist("Fresh1", uri = "spotify://artist/fresh1"),
                    artist("Fresh2", uri = "spotify://artist/fresh2")
                )
            )
        )

        val names = build(serverFolders = folders).artistNames()

        assertEquals(1, names.count { it == "Shared1" })
        assertEquals(1, names.count { it == "Shared2" })
        assertTrue("Fresh1" in names && "Fresh2" in names)
    }

    @Test
    fun `final artist list is de-duplicated by uri and capped at artistCount`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val lastFm = (1..6).map { artist("Lf$it", uri = "spotify://artist/lf$it") to (0.9 - it * 0.05) }
        wireSeed(seed, *lastFm.toTypedArray())
        val folders = listOf(
            folder("recommended_artists", artists = (1..6).map { artist("Fb$it", uri = "spotify://artist/fb$it") })
        )

        val result = build(serverFolders = folders, artistCount = 5)

        assertEquals(5, result.artists.size)
    }

    // =======================================================================================
    // Album selection (Last.fm pool) + album fallback
    // =======================================================================================

    @Test
    fun `best album skips singles, compilations, imageless and recently played albums`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val band = artist("Band")
        wireSeed(seed, band to 0.9)
        givenArtistAlbums(
            band,
            album("Single1", albumType = "single"),
            album("Comp1", albumType = "compilation"),
            album("NoCover", imageUrl = null),
            album("Recent", uri = "spotify://album/recent"),
            album("Pick", uri = "spotify://album/pick"),
            album("AlsoEligible", uri = "spotify://album/also")
        )
        coEvery { playHistory.getRecentAlbums(any()) } returns listOf(recent("spotify://album/recent"))

        // firstOrNull -> "Pick" (first eligible in list order).
        assertEquals(listOf("Pick"), build().albumNames())
    }

    @Test
    fun `best album falls back to a compilation when no regular album is available`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val band = artist("Band")
        wireSeed(seed, band to 0.9)
        givenArtistAlbums(
            band,
            album("OnlySingle1", albumType = "single"),
            album("OnlySingle2", albumType = "single"),
            album("CompFallback", albumType = "compilation")
        )

        assertEquals(listOf("CompFallback"), build().albumNames())
    }

    @Test
    fun `album candidates come from multiple artists in the pool`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val a = artist("A")
        val b = artist("B")
        val c = artist("C")
        wireSeed(seed, a to 0.9, b to 0.8, c to 0.7)
        givenArtistAlbums(a, album("AlbumA", uri = "spotify://album/a"))
        givenArtistAlbums(b, album("AlbumB", uri = "spotify://album/b"))
        givenArtistAlbums(c, album("AlbumC", uri = "spotify://album/c"))

        val albums = build().albumNames()

        assertEquals(setOf("AlbumA", "AlbumB", "AlbumC"), albums.toSet())
    }

    @Test
    fun `getRecentAlbums failure is swallowed and albums are still produced`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val band = artist("Band")
        wireSeed(seed, band to 0.9)
        givenArtistAlbums(band, album("Stable", uri = "spotify://album/stable"))
        coEvery { playHistory.getRecentAlbums(any()) } throws RuntimeException("boom")

        assertEquals(listOf("Stable"), build().albumNames())
    }

    @Test
    fun `album fallback fills albums from provider folders when the Last fm pool is empty`() = runTest {
        givenSeeds(score("Seed", 5.0)) // no similars => no Last.fm albums
        val folders = listOf(
            folder(
                "recommended_albums",
                albums = (1..5).map { album("FbAlbum$it", uri = "spotify://album/fb$it") }
            )
        )

        val albums = build(serverFolders = folders).albumNames()

        assertEquals(5, albums.size)
        (1..5).forEach { assertTrue("FbAlbum$it" in albums) }
    }

    @Test
    fun `album fallback applies type, recent, image and library-primary-artist filters`() = runTest {
        givenSeeds(score("Seed", 5.0))
        val library = listOf(
            artist("KnownArtist1", uri = "library://artist/known1"),
            artist("KnownArtist2", uri = "library://artist/known2")
        )
        val folders = listOf(
            folder(
                "recommended_albums",
                albums = listOf(
                    album("Single", uri = "spotify://album/s", albumType = "single"),
                    album("Comp", uri = "spotify://album/c", albumType = "compilation"),
                    album("NoImg", uri = "spotify://album/n", imageUrl = null),
                    album("Recent1", uri = "spotify://album/r1"),
                    album("Recent2", uri = "spotify://album/r2"),
                    album("ByKnown1", uri = "spotify://album/k1", artists = listOf(artist("KnownArtist1"))),
                    album("ByKnown2", uri = "spotify://album/k2", artists = listOf(artist("KnownArtist2"))),
                    album("Good1", uri = "spotify://album/g1", artists = listOf(artist("Unknown1"))),
                    album("Good2", uri = "spotify://album/g2", artists = listOf(artist("Unknown2")))
                )
            )
        )
        coEvery { playHistory.getRecentAlbums(any()) } returns
            listOf(recent("spotify://album/r1"), recent("spotify://album/r2"))

        val albums = build(libraryArtists = library, serverFolders = folders).albumNames()

        assertEquals(setOf("Good1", "Good2"), albums.toSet())
    }

    @Test
    fun `album fallback skips albums already chosen from the Last fm pool`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val a = artist("A")
        val b = artist("B")
        wireSeed(seed, a to 0.9, b to 0.8)
        givenArtistAlbums(a, album("Shared1", uri = "spotify://album/shared1"))
        givenArtistAlbums(b, album("Shared2", uri = "spotify://album/shared2"))
        val folders = listOf(
            folder(
                "recommended_albums",
                albums = listOf(
                    album("Shared1", itemId = "dup1", uri = "spotify://album/shared1"),
                    album("Shared2", itemId = "dup2", uri = "spotify://album/shared2"),
                    album("FreshAlbum1", uri = "spotify://album/fresh1"),
                    album("FreshAlbum2", uri = "spotify://album/fresh2")
                )
            )
        )

        val albums = build(serverFolders = folders).albumNames()

        assertEquals(1, albums.count { it == "Shared1" })
        assertEquals(1, albums.count { it == "Shared2" })
        assertTrue("FreshAlbum1" in albums && "FreshAlbum2" in albums)
    }

    @Test
    fun `final album list is de-duplicated by canonical key and capped at albumCount`() = runTest {
        givenSeeds(score("Seed", 5.0))
        val folders = listOf(
            folder(
                "recommended_albums",
                albums = (1..6).map { album("Alb$it", uri = "spotify://album/$it") }
            )
        )

        val result = build(serverFolders = folders, albumCount = 3)

        assertEquals(3, result.albums.size)
    }

    // =======================================================================================
    // Final assembly
    // =======================================================================================

    @Test
    fun `artists and albums are combined from Last fm and fallback sources`() = runTest {
        val seed = score("Seed", 5.0)
        givenSeeds(seed)
        val lfA = artist("LfArtist1", uri = "spotify://artist/lf1")
        val lfB = artist("LfArtist2", uri = "spotify://artist/lf2")
        wireSeed(seed, lfA to 0.9, lfB to 0.8)
        givenArtistAlbums(lfA, album("LfAlbum1", uri = "spotify://album/lf1"))
        givenArtistAlbums(lfB, album("LfAlbum2", uri = "spotify://album/lf2"))
        val folders = listOf(
            folder("recommended_artists", artists = listOf(
                artist("FbArtist1", uri = "spotify://artist/fb1"),
                artist("FbArtist2", uri = "spotify://artist/fb2")
            )),
            folder("recommended_albums", albums = listOf(
                album("FbAlbum1", uri = "spotify://album/fb1"),
                album("FbAlbum2", uri = "spotify://album/fb2")
            ))
        )

        val result = build(serverFolders = folders)

        assertTrue(result.artistNames().containsAll(listOf("LfArtist1", "LfArtist2", "FbArtist1", "FbArtist2")))
        assertTrue(result.albumNames().containsAll(listOf("LfAlbum1", "LfAlbum2", "FbAlbum1", "FbAlbum2")))
    }

    @Test
    fun `custom artistCount and albumCount are independently honored`() = runTest {
        givenSeeds(score("Seed", 5.0))
        val folders = listOf(
            folder("recommended_artists", artists = (1..10).map { artist("A$it", uri = "spotify://artist/$it") }),
            folder("recommended_albums", albums = (1..10).map { album("Al$it", uri = "spotify://album/$it") })
        )

        val result = build(serverFolders = folders, artistCount = 4, albumCount = 2)

        assertEquals(4, result.artists.size)
        assertEquals(2, result.albums.size)
    }

    @Test
    fun `entirely empty inputs produce an empty result`() = runTest {
        val result = build()

        assertTrue(result.artists.isEmpty())
        assertTrue(result.albums.isEmpty())
    }

    // =======================================================================================
    // End-to-end scenario
    //
    // User listens to rock + jazz (+ a little electronic). Discovery should return diverse new
    // artists and albums they haven't heard, excluding blocked artists, with genre diversity,
    // drawing first from Last.fm-derived recommendations and then from provider fallback.
    // =======================================================================================

    @Test
    fun `end-to-end - diverse new artists and albums excluding library, blocked and recently played`() = runTest {
        // --- library (3 known artists across rock / jazz / electronic) ---
        val libraryArtists = listOf(
            artist("Pink Floyd", uri = "library://pinkfloyd", genres = listOf("rock")),
            artist("Miles Davis", uri = "library://milesdavis", genres = listOf("jazz")),
            artist("Daft Punk", uri = "library://daftpunk", genres = listOf("electronic"))
        )
        val excluded = setOf("spotify://artist/nickelback")

        // --- server fallback folders ---
        val serverFolders = listOf(
            folder(
                "recommended_artists",
                provider = "spotify",
                artists = listOf(
                    artist("Fallback Artist1", uri = "spotify://fb1", genres = listOf("pop")),
                    artist("Fallback Artist2", uri = "spotify://fb2", genres = listOf("indie")),
                    artist("Pink Floyd", uri = "library://pinkfloyd", genres = listOf("rock")) // in library -> filtered
                )
            ),
            folder(
                "recommended_albums",
                provider = "spotify",
                albums = listOf(
                    album("Fallback Album", uri = "spotify://fbalbum1", albumType = "album", artists = listOf(artist("NewGuy"))),
                    album("Bad Single", uri = "spotify://single1", albumType = "single", artists = listOf(artist("NewGuy")))
                )
            )
        )

        // --- play history: seeds + one recently played album ---
        givenSeeds(
            score("Pink Floyd", 8.0, uri = "library://pinkfloyd"),
            score("Miles Davis", 6.0, uri = "library://milesdavis"),
            score("Daft Punk", 4.0, uri = "library://daftpunk")
        )
        coEvery { playHistory.getRecentAlbums(any()) } returns listOf(recent("spotify://already-heard-album"))

        // --- genres: top families map to the library seed artists ---
        coEvery { genre.scoredGenres(any(), any()) } returns listOf(
            GenreScore("rock", 10.0),
            GenreScore("jazz", 7.0),
            GenreScore("electronic", 5.0)
        )
        coEvery { genre.genreArtistMap() } returns mapOf(
            "rock" to listOf("library://pinkfloyd"),
            "jazz" to listOf("library://milesdavis"),
            "electronic" to listOf("library://daftpunk")
        )

        // --- Last.fm similar artists per seed (Porcupine Tree is voted by 2 seeds) ---
        givenSimilars(
            "Pink Floyd",
            sim("Porcupine Tree", 0.9),
            sim("Tool", 0.85),
            sim("Radiohead", 0.8),   // resolves to nothing -> dropped
            sim("Nickelback", 0.7)   // blocked -> dropped
        )
        givenSimilars(
            "Miles Davis",
            sim("John Coltrane", 0.95),
            sim("Herbie Hancock", 0.8),
            sim("Porcupine Tree", 0.3) // second vote for Porcupine Tree
        )
        givenSimilars(
            "Daft Punk",
            sim("Justice", 0.9),
            sim("Kavinsky", 0.7) // resolves with null image -> dropped
        )

        // --- resolution: search hit (no genres) + getArtist (genres filled in) ---
        fun wireResolved(query: String, search: Artist, full: Artist) {
            coEvery { music.search(query, any(), any()) } returns SearchResult(artists = listOf(search))
            coEvery { music.getArtist(full.itemId, full.provider) } returns full
        }

        val pt = artist("Porcupine Tree", itemId = "pt1", uri = "spotify://pt", genres = emptyList())
        val tool = artist("Tool", itemId = "tool1", uri = "spotify://tool", genres = emptyList())
        val jc = artist("John Coltrane", itemId = "jc1", uri = "spotify://jc", genres = emptyList())
        val hh = artist("Herbie Hancock", itemId = "hh1", uri = "spotify://hh", genres = emptyList())
        val justice = artist("Justice", itemId = "j1", uri = "spotify://justice", genres = emptyList())

        wireResolved("porcupine tree", pt, pt.copy(genres = listOf("progressive rock")))
        wireResolved("tool", tool, tool.copy(genres = listOf("progressive metal")))
        wireResolved("john coltrane", jc, jc.copy(genres = listOf("jazz")))
        wireResolved("herbie hancock", hh, hh.copy(genres = listOf("jazz", "fusion")))
        wireResolved("justice", justice, justice.copy(genres = listOf("electronic", "house")))

        coEvery { music.search("radiohead", any(), any()) } returns SearchResult() // not found
        // Nickelback resolves (no getArtist stub -> uses the search hit) but is excluded.
        coEvery { music.search("nickelback", any(), any()) } returns SearchResult(
            artists = listOf(artist("Nickelback", itemId = "nb1", uri = "spotify://artist/nickelback", genres = emptyList()))
        )
        // Kavinsky resolves with a null image -> filtered.
        coEvery { music.search("kavinsky", any(), any()) } returns SearchResult(
            artists = listOf(artist("Kavinsky", itemId = "k1", uri = "spotify://kavinsky", imageUrl = null, genres = emptyList()))
        )

        // --- albums per resolved artist ---
        givenArtistAlbums(
            pt.copy(genres = listOf("progressive rock")),
            album("In Absentia", uri = "spotify://album/ia"),
            album("Single X", uri = "spotify://album/sx", albumType = "single") // single -> skipped
        )
        givenArtistAlbums(jc.copy(genres = listOf("jazz")), album("A Love Supreme", uri = "spotify://album/als"))
        givenArtistAlbums(justice.copy(genres = listOf("electronic", "house")), album("Cross", uri = "spotify://album/cross"))
        givenArtistAlbums(
            tool.copy(genres = listOf("progressive metal")),
            album("Lateralus", uri = "spotify://already-heard-album"), // recently played -> skipped
            album("Fear Inoculum", uri = "spotify://album/fi")
        )
        // Herbie Hancock has no albums available -> contributes no album.

        val result = build(
            libraryArtists = libraryArtists,
            serverFolders = serverFolders,
            excludedArtistUris = excluded,
            artistCount = 10,
            albumCount = 5
        )

        // ---------------- artists ----------------
        val artistNames = result.artistNames()
        assertEquals(
            setOf(
                "Porcupine Tree", "John Coltrane", "Tool", "Herbie Hancock", "Justice",
                "Fallback Artist1", "Fallback Artist2"
            ),
            artistNames.toSet()
        )
        listOf("Pink Floyd", "Miles Davis", "Daft Punk", "Nickelback", "Radiohead", "Kavinsky")
            .forEach { assertFalse(it in artistNames, "$it must not appear") }
        assertTrue(result.artists.size <= 10)
        assertTrue(result.artists.all { it.imageUrl != null }, "all artists must have an image")
        assertEquals(result.artists.size, result.artists.map { it.uri }.distinct().size, "no duplicate artist uris")

        // ranking: Porcupine Tree (2 votes + high match) leads; relevance order within families holds
        assertEquals("Porcupine Tree", artistNames.first())
        assertTrue(artistNames.indexOf("Porcupine Tree") < artistNames.indexOf("Tool"))
        assertTrue(artistNames.indexOf("John Coltrane") < artistNames.indexOf("Herbie Hancock"))
        // MMR diversity: the top three discoveries are not all the same genre family
        assertTrue(result.artists.take(3).flatMap { it.genres }.toSet().size >= 2, "top results should be genre-diverse")

        // ---------------- albums ----------------
        val albumNames = result.albumNames()
        assertEquals(
            setOf("In Absentia", "A Love Supreme", "Fear Inoculum", "Cross", "Fallback Album"),
            albumNames.toSet()
        )
        listOf("Single X", "Lateralus", "Bad Single")
            .forEach { assertFalse(it in albumNames, "$it must not appear") }
        assertTrue(result.albums.size <= 5)
        assertTrue(result.albums.all { it.imageUrl != null }, "all albums must have an image")
        assertEquals(result.albums.size, result.albums.map { it.uri }.distinct().size, "no duplicate album uris")
        // fallback album is appended after the Last.fm-derived albums
        assertEquals("Fallback Album", albumNames.last())
    }
}
