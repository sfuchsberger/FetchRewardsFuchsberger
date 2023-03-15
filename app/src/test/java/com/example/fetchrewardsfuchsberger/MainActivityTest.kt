package com.example.fetchrewardsfuchsberger

import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.junit.Assert.*
import org.junit.runner.RunWith


/**
 * Main Activity Test
 *
 * Test 1:
 *      Name: testFetchDataFromURL
 *      What: Verifies Data is fetched from the provided URL successfully.
 *
 * Test 2:
 *      Name: testParsingJSONData
 *      What: Verify that the GSON module successfully parses the fetched JSON data
 *
 * Test 3:
 *      Name: testFilteringEmptyOrNullItems
 *      What: ensures that all json data passed with null or empty values is filtered out.
 *
 * Test 4:
 *      Name: testGroupingItemsByListId
 *      What: verifies all the items are separated by List ID
 *
 * Test 5:
 *      Name: testSortingItemsByName
 *      What: verifies the items are arranged by name value.
 *
 * Test 6:
 *      Name: testHandlingDataRetrievalErrors
 *      What: Verifies that an error is thrown if the data is no longer available.
 *
 */

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun testFetchDataFromURL() {
        val activity = MainActivity()
        val queue = Volley.newRequestQueue(activity)
        val url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
        var dataFetched = false

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                dataFetched = true
            },
            { error ->
                Log.e("MainActivity", "Error retrieving data: ${error.message}")
            }
        )

        queue.add(stringRequest)

        Thread.sleep(5000)

        assertTrue(dataFetched)
    }

    @Test
    fun testParsingJSONData() {

        val gson = Gson()
        val json = "[{\"id\": 1, \"listId\": 2, \"name\": \"Item 1\"}, {\"id\": 2, \"listId\": 1, \"name\": \"Item 2\"}]"
        val items = gson.fromJson(json, Array<MainActivity.Item>::class.java)

        assertEquals(items.size, 2)
        assertEquals(items[0].id, 1)
        assertEquals(items[0].listId, 2)
        assertEquals(items[0].name, "Item 1")
        assertEquals(items[1].id, 2)
        assertEquals(items[1].listId, 1)
        assertEquals(items[1].name, "Item 2")
    }

    @Test
    fun testFilteringEmptyOrNullItems() {
        val gson = Gson()
        val json = "[{\"id\": 1, \"listId\": 2, \"name\": null}, {\"id\": 2, \"listId\": 1, \"name\": \"Item 2\"}, {\"id\": 3, \"listId\": 1, \"name\": \"\"}]"
        val items = gson.fromJson(json, Array<MainActivity.Item>::class.java)
            .filter { it.name != null && it.name.isNotEmpty() }

        assertEquals(items.size, 1)
        assertEquals(items[0].id, 2)
        assertEquals(items[0].listId, 1)
        assertEquals(items[0].name, "Item 2")
    }

    @Test
    fun testGroupingItemsByListId() {
        val activity = MainActivity()
        val gson = Gson()
        val json = "[{\"id\": 1, \"listId\": 2, \"name\": \"Item 1\"}, {\"id\": 2, \"listId\": 1, \"name\": \"Item 2\"}, {\"id\": 3, \"listId\": 2, \"name\": \"Item 3\"}]"
        val items = gson.fromJson(json, Array<MainActivity.Item>::class.java)
            .filter { it.name != null && it.name.isNotEmpty() }
            .groupBy { it.listId }

        assertEquals(items.size, 2)
        assertEquals(items[1]?.size, 1)
        assertEquals(items[1]?.get(0)?.id, 2)
        assertEquals(items[1]?.get(0)?.listId, 1)
        assertEquals(items[1]?.get(0)?.name, "Item 2")
        assertEquals(items[2]?.size, 2)
        assertEquals(items[2]?.get(0)?.id, 1)
        assertEquals(items[2]?.get(0)?.listId, 2)
        assertEquals(items[2]?.get(0)?.name, "Item 1")
        assertEquals(items[2]?.get(1)?.id, 3)
        assertEquals(items[2]?.get(1)?.listId, 2)
        assertEquals(items[2]?.get(1)?.name, "Item 3")
    }

    @Test
    fun testSortingItemsByName() {
        val gson = Gson()
        val json = "[{\"id\": 1, \"listId\": 2, \"name\": \"B\"}, {\"id\": 2, \"listId\": 1, \"name\": \"A\"}, {\"id\": 3, \"listId\": 2, \"name\": \"C\"}]"
        val items = gson.fromJson(json, Array<MainActivity.Item>::class.java)
            .filter { it.name != null && it.name.isNotEmpty() }
            .groupBy { it.listId }
            .toSortedMap()
            .flatMap { (_, items) -> items.sortedBy { it.name } }

        assertEquals(items.size, 3)
        assertEquals(items[0].id, 2)
        assertEquals(items[0].listId, 1)
        assertEquals(items[0].name, "A")
        assertEquals(items[1].id, 1)
        assertEquals(items[1].listId, 2)
        assertEquals(items[1].name, "B")
        assertEquals(items[2].id, 3)
        assertEquals(items[2].listId, 2)
        assertEquals(items[2].name, "C")
    }


    @Test
    fun testHandlingDataRetrievalErrors() {
        val activity = MainActivity()
        val queue = Volley.newRequestQueue(activity)
        val url = "https://fetch-hiring.s3.amazonaws.com/invalid_url.json"
        var errorOccurred = false

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                fail("The request should have resulted in an error")
            },
            { error ->
                errorOccurred = true
                assertEquals(error.message, "java.net.UnknownHostException: Unable to resolve host \"fetch-hiring.s3.amazonaws.com\": No address associated with hostname")
            }
        )

        queue.add(stringRequest)

        assertTrue(errorOccurred)
    }


}