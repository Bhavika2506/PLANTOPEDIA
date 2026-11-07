package com.example.plantopedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityScreenTest {

    @Test
    fun testCommunityCategories_notEmpty() {
        assertTrue("Categories list should not be empty", communityCategories.isNotEmpty())
        assertEquals(listOf("All", "Crops", "Diseases", "Organic", "Tips"), communityCategories)
    }

    @Test
    fun testCommunityPostsFilter_byCategory() {
        assertTrue("Sample posts should not be empty", sampleCommunityPosts.isNotEmpty())

        val diseasePosts = sampleCommunityPosts.filter { it.category.equals("Diseases", ignoreCase = true) }
        assertTrue("Should have at least 1 disease post", diseasePosts.isNotEmpty())
        assertTrue("All filtered items must have Diseases category", diseasePosts.all { it.category == "Diseases" })

        val organicPosts = sampleCommunityPosts.filter { it.category.equals("Organic", ignoreCase = true) }
        assertTrue("Should have at least 1 organic post", organicPosts.isNotEmpty())
        assertTrue("All filtered items must have Organic category", organicPosts.all { it.category == "Organic" })
    }

    @Test
    fun testCommunityPostCreation() {
        val post = CommunityPost(
            id = "test-1",
            authorName = "Test Farmer",
            location = "Pune, Maharashtra",
            timeAgo = "Just now",
            category = "Crops",
            contentEn = "Testing wheat harvest",
            contentHi = "गेहूं की कटाई का परीक्षण",
            contentMr = "गहू कापणी चाचणी",
            likesCount = 5,
            repliesCount = 2,
            isLiked = false
        )

        assertEquals("test-1", post.id)
        assertEquals("Test Farmer", post.authorName)
        assertEquals("Crops", post.category)
        assertFalse(post.isLiked)
        assertEquals(5, post.likesCount)
    }

    @Test
    fun testCommunityPostTranslations() {
        sampleCommunityPosts.forEach { post ->
            assertNotNull("English content must not be null", post.contentEn)
            assertTrue("English content must not be blank", post.contentEn.isNotBlank())

            assertNotNull("Hindi content must not be null", post.contentHi)
            assertTrue("Hindi content must not be blank", post.contentHi.isNotBlank())

            assertNotNull("Marathi content must not be null", post.contentMr)
            assertTrue("Marathi content must not be blank", post.contentMr.isNotBlank())
        }
    }
}
