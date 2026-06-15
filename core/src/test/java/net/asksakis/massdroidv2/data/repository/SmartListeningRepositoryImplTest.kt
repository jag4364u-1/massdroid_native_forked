package net.asksakis.massdroidv2.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.asksakis.massdroidv2.data.database.AppDatabase
import net.asksakis.massdroidv2.data.database.PlayHistoryDao
import net.asksakis.massdroidv2.data.database.SmartFeedbackEntity
import net.asksakis.massdroidv2.domain.model.Track
import net.asksakis.massdroidv2.domain.repository.SettingsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SmartListeningRepositoryImplTest {

    private val dao: PlayHistoryDao = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk()
    private val appDatabase: AppDatabase = mockk(relaxed = true)

    private lateinit var repository: SmartListeningRepositoryImpl

    @BeforeEach
    fun setUp() {
        every { dao.observeBlockedArtistUris() } returns flowOf(emptyList())
        repository = SmartListeningRepositoryImpl(dao, settingsRepository, appDatabase)
    }

    @Test
    fun `recordSkip does nothing when smart listening is disabled`() = runTest {
        every { settingsRepository.smartListeningEnabled } returns flowOf(false)

        val track = Track(
            itemId = "track-1",
            provider = "test",
            name = "Test Track",
            uri = "library://track/1",
            duration = 200.0,
            artistNames = "Test Artist",
            artistItemId = "artist-1",
            artistUri = "library://artist/1"
        )
        val artists = listOf(Pair("library://artist/1", "Test Artist"))

        repository.recordSkip(track, artists, 5000L)

        coVerify(exactly = 0) { dao.insertSmartFeedback(any<List<SmartFeedbackEntity>>()) }
    }

    @Test
    fun `recordListen does nothing when smart listening is disabled`() = runTest {
        every { settingsRepository.smartListeningEnabled } returns flowOf(false)

        val track = Track(
            itemId = "track-1",
            provider = "test",
            name = "Test Track",
            uri = "library://track/1",
            duration = 200.0,
            artistNames = "Test Artist",
            artistItemId = "artist-1",
            artistUri = "library://artist/1"
        )
        val artists = listOf(Pair("library://artist/1", "Test Artist"))

        repository.recordListen(track, artists, 180_000L)

        coVerify(exactly = 0) { dao.insertSmartFeedback(any<List<SmartFeedbackEntity>>()) }
    }

    @Test
    fun `getBlockedArtistUris returns set from dao`() = runTest {
        coEvery { dao.getBlockedArtistUris() } returns listOf("uri-1", "uri-2")

        val result = repository.getBlockedArtistUris()

        assertEquals(setOf("uri-1", "uri-2"), result)
    }

    @Test
    fun `getSuppressedTrackUris returns set from dao`() = runTest {
        coEvery { dao.getSuppressedTrackUris() } returns listOf("track-a", "track-b")

        val result = repository.getSuppressedTrackUris()

        assertEquals(setOf("track-a", "track-b"), result)
    }
}
