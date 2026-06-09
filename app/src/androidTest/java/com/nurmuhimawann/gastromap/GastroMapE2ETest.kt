package com.nurmuhimawann.gastromap

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.nurmuhimawann.gastromap.ui.GastroMap
import com.nurmuhimawann.gastromap.ui.theme.GastroMapTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GastroMapE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            GastroMapTheme {
                GastroMap()
            }
        }
    }

    @Test
    fun testNavigationAndContentDisplay() {
        // 1. Periksa apakah halaman Home muncul pertama kali
        composeTestRule.onNodeWithText("GastroMap").assertIsDisplayed()
        
        // Wait content list (Getting data dari API)
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodesWithText("Featured Restaurants").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Featured Restaurants").assertIsDisplayed()

        // 2. Navigasi ke halaman Favorite
        composeTestRule.onNode(hasText("Favorite") and hasClickAction()).performClick()
        
        // Wait content
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Favorites").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()

        // 3. Navigasi ke halaman About
        composeTestRule.onNode(hasText("About") and hasClickAction()).performClick()

        // Wait content
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("About").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("About").onFirst().assertIsDisplayed()

        // 4. Kembali ke Home
        composeTestRule.onNode(hasText("Home") and hasClickAction()).performClick()
        composeTestRule.onNodeWithText("GastroMap").assertIsDisplayed()
    }

    @Test
    fun testSearchPositiveAndNegative() {
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodesWithText("Featured Restaurants").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Positive Search
        composeTestRule.onNodeWithText("Search your mood...").performTextInput("Melting")
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Search results for \"Melting\"").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Search results for \"Melting\"").assertIsDisplayed()

        // 2. Negative Search
        composeTestRule.onNode(hasText("Melting") and hasSetTextAction()).performTextReplacement("RestoranGhaib123")
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("No restaurants found").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("No restaurants found").assertIsDisplayed()

        // 3. Clear Search
        composeTestRule.onNodeWithContentDescription("Clear").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Featured Restaurants").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Featured Restaurants").assertIsDisplayed()
    }

    @Test
    fun testFavoriteFlow() {
        // 1. Tambah content ke Favorite dari list di Home
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodesWithContentDescription("Toggle Favorite").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithContentDescription("Toggle Favorite").onFirst().performClick()

        // 2. Periksa content di halaman Favorite
        composeTestRule.onNode(hasText("Favorite") and hasClickAction()).performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithContentDescription("Toggle Favorite").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("No favorite restaurant at the moment").assertDoesNotExist()

        // 3. Navigasi ke detail restaurant dari halaman Favorite
        // Klik area kartu yang mengandung tombol favorite
        composeTestRule.onAllNodes(
            hasClickAction() and 
            hasAnyChild(hasContentDescription("Toggle Favorite")) and 
            !hasContentDescription("Toggle Favorite")
        ).onFirst().performClick()
        
        // 4. Di halaman Detail, periksa konten
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodesWithText("Menus").fetchSemanticsNodes().isNotEmpty()
        }
        // Scroll ke "Menus", "Drinks", dan "Customer Reviews" karena mungkin berada di luar layar
        composeTestRule.onNodeWithText("Menus").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Drinks").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Customer Reviews").performScrollTo().assertIsDisplayed()

        // 5. Hapus dari Favorite di Detail
        composeTestRule.onNodeWithContentDescription("Toggle Favorite").performClick()
        
        // 6. Kembali
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun testThemeToggle() {
        composeTestRule.onNodeWithContentDescription("Theme Toggle").performClick()
        composeTestRule.onNodeWithContentDescription("Theme Toggle").assertExists()
    }

    @Test
    fun testAddCustomerReview() {
        val testName = "Juwita"
        val testReview = "Yummy, Enak banget. (Review from Automated E2E Test)"

        // 1. Klik salah satu restoran
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodes(hasClickAction() and hasAnyChild(hasContentDescription("Toggle Favorite"))).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodes(hasClickAction() and hasAnyChild(hasContentDescription("Toggle Favorite"))).onFirst().performClick()

        // 2. Di halaman Detail, scroll ke bawah untuk menemukan tombol "Add Review"
        composeTestRule.waitUntil(timeoutMillis = 16000) {
            composeTestRule.onAllNodesWithText("Add Review").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Add Review").performScrollTo().performClick()

        // 3. Isi dialog review
        composeTestRule.onNodeWithText("Name").performClick().performTextInput(testName)
        composeTestRule.onNodeWithText("Review").performClick().performTextInput(testReview)

        // Pastikan tombol Post dapat diklik
        composeTestRule.onNodeWithText("Post").assertIsEnabled().performClick()

        // Verifikasi UI
        composeTestRule.onNodeWithText(testName).assertIsDisplayed()
    }
}
