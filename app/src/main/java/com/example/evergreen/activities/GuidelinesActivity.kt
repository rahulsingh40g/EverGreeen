package com.example.evergreen.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.GuidelinesAdapter
import kotlinx.android.synthetic.main.activity_guidelines.*
import java.util.*
import kotlin.collections.ArrayList


class GuidelinesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidelines)

        setupActionBar()

        val points = arrayOf(
            "Ideally, there should be a space of 15 feet between two saplings that will grow big, else as they grow one will interfere with another and there will be problems of cross pollination, fight for resources like water and nutrients etc.",
            "There are many theories on what to plant where. It really depends on the core reason why we are planting the saplings. Key thing to keep in mind is :",
            "Do not go for foreign varieties of trees.",
            "Promote local and organic varieties. Variety among trees planted is required to ensure biodiversity, because naturally various plants attract various types of insects, birds, bees and animals, and all of them have different uses.",
            "Know the source of your sapling. Go to trustworthy sapling providers. No GM saplings, or hybrids that do not survive beyond a generation.",
            "Get more fruiting and flowering varieties, rather than focusing on just greenery and shade.",
            "Get a sapling that is at least three feet high. Younger the sapling, difficult for it to survive the replanting shock where the sapling is shifted from the controlled environment of a nursery in the soil placed in a pot or a plastic bag, to huge open environment with no real control over what might lie in the soil.",
            "Go for species with taproot system that go deep down in the soil and straight up in the sky rather than spreading, in commercial areas that have hoardings etc, because trees that spread across are likely to be cut by hoarding owners to increase visibility of the hoardings.",
        )
        val pointsList = ArrayList<String>()
        pointsList.addAll(points)

        mainListView.layoutManager = LinearLayoutManager(this)
        mainListView.setHasFixedSize(true)

        val adapter = GuidelinesAdapter(this, pointsList)
        mainListView.adapter = adapter
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_guidelines_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "GUIDELINES"
        }

        toolbar_guidelines_activity.setNavigationOnClickListener { onBackPressed() }
    }
}