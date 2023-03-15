package com.example.fetchrewardsfuchsberger

//Import the required classes and packages
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    //Declare the data class for Items.
    data class Item(val id: Int, val listId: Int, val name: String)

    //Declare the ListView variable that corresponds to the UI element
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        //initialize new variables and set the layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

        //Prepare the Volley request queue
        val queue = Volley.newRequestQueue(this)
        val url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"


        //Begin JSON Data retrieval
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                //Set GSON to parse the JSON data into an array of type Item
                val gson = Gson()
                val items = gson.fromJson(response, Array<Item>::class.java)
                    //Filter out blank and null names
                    .filter { it.name != null && it.name.isNotEmpty() }
                    //Group items by listID
                    .groupBy { it.listId }
                    .toSortedMap()
                    //Sort the individual groups by name
                    .flatMap { (_, items) -> items.sortedBy { it.name } }
                //Initialize the adapter to display the list of item names in order
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    items.map { it.name }
                )

                //Set the adapter for the ListView
                listView.adapter = adapter
            },
            //Error Listener
            { error ->
                // Log the error message if there is a problem retrieving data
                Log.e("MainActivity", "Error retrieving data: ${error.message}")

            }
        )

        // Add the string request to the request queue
        queue.add(stringRequest)

    }



}


