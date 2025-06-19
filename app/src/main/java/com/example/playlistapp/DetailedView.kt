package com.example.playlistapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistapp.MainActivity
import com.example.playlistapp.R

class DetailedView : AppCompatActivity() {

    private lateinit var averageButton: Button
    private lateinit var returnButton: Button
    private lateinit var packingListTextView: TextView

    // These pre-defined lists are "static" data for demonstration.
    // In a real app, this data would come from a database or shared preferences.
    private val itemSongName = ArrayList<String>(
        listOf("Ocean Eyes", "single ladies", "trust", "u'jesu")
    )
    private val itemArtistName = ArrayList<String>(
        listOf("Billie Ellish", "Beyonce", "Brent faiyaz", "Lundi")
    )
    private val itemRating = ArrayList<Int>(
        listOf(5, 6, 2, 9)
    )
    private val itemComments = ArrayList<String>(
        listOf("new gen rnb", "lets breack our legs! dance song", "Rnb mix with rap", "gosspel song")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitydetailed)


        returnButton = findViewById(R.id.Return)
        averageButton = findViewById(R.id.averageButton)
        packingListTextView = findViewById(R.id.packingListTextView)

        // *** CRITICAL CHANGE HERE ***
        // Get the list of newly added items passed from MainActivity.
        val newlyAddedItemsList = intent.getStringArrayListExtra("NEWLY_ADDED_PACKING_ITEMS")

        // Determine what to display initially based on whether new items were passed.
        if (newlyAddedItemsList != null && newlyAddedItemsList.isNotEmpty()) {
            // If new items were passed, process and display ONLY them.
            val successfullyAddedItemsForDisplay = mutableListOf<String>()

            for (itemString in newlyAddedItemsList) {
                val parts = itemString.split(",").map { it.trim() }
                if (parts.size == 4) {
                    try {
                        val Songname = parts[0]
                        val Artistname = parts[1]
                        val Rating = parts[2].toInt()
                        val comments = parts[3]

                        // Add the new item to the *internal persistent lists* here.
                      
                        itemSongName.add(Songname)
                        itemArtistName.add(Artistname)
                        itemRating.add(Rating)
                        itemComments.add(comments)

                        successfullyAddedItemsForDisplay.add(itemString)
                    } catch (e: NumberFormatException) {
                        // Log or handle parsing errors
                    } catch (e: Exception) {
                        // Handle other parsing errors
                    }
                }
            }

            if (successfullyAddedItemsForDisplay.isNotEmpty()) {
                // Display ONLY the successfully parsed new items.
                displayNewlyAddedItems(successfullyAddedItemsForDisplay)
            } else {
                // This means the list was passed, but it contained no valid items.
                // In this specific case, it's a good fallback to show the full list.
                displayFullPackingList()
                AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage("No valid items found in the newly added list to display. Showing full list as fallback.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        } else {
            // If no newly added items were passed, or the list was empty,
            // then display the full pre-existing packing list.
            displayFullPackingList()
        }

        // --- Event Listeners remain the same ---

        averageButton.setOnClickListener {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Rating with averages:\n\n")

            var foundItems = false
            for (i in itemRating.indices) {
                if (itemRating[i] >= 1) {
                    stringBuilder.append("- ${itemSongName[i]}\n")
                    stringBuilder.append("  Artistname: ${itemArtistName[i]}\n")
                    stringBuilder.append("  Rating: ${itemRating[i]}\n")
                    stringBuilder.append("  Comments: ${itemComments[i]}\n\n")
                    foundItems = true
                }
            }

            if (!foundItems) {
                stringBuilder.clear()
                stringBuilder.append("Average of rating of the playList.")
            }

            AlertDialog.Builder(this)
                .setTitle("Filtered Packing List")
                .setMessage(stringBuilder.toString())
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finish MainActivity so it's removed from the back stack
        }
    }

    private fun displayFullPackingList() {
        if (itemSongName.isEmpty()) {
            packingListTextView.text = "Your packing list is empty."
            return
        }

        val displayBuilder = StringBuilder()
        displayBuilder.append("--- Your Full Packing List ---\n\n")

        for (i in itemSongName.indices) {
            displayBuilder.append("SongName: ${itemSongName[i]}\n")
            displayBuilder.append("Artistname: ${itemArtistName[i]}\n")
            displayBuilder.append("Rating: ${itemRating[i]}\n")
            displayBuilder.append("Comments: ${itemComments[i]}\n")
            displayBuilder.append("----------------------------------\n")
        }
        packingListTextView.text = displayBuilder.toString()
    }

    private fun displayNewlyAddedItems(newItems: MutableList<String>) {
        if (newItems.isEmpty()) {
            packingListTextView.text = "No newly added items to display."
            return
        }

        val displayBuilder = StringBuilder()
        displayBuilder.append("--- Newly Added Items ---\n\n")

        for (itemString in newItems) {
            val parts = itemString.split(",").map { it.trim() }
            if (parts.size == 4) {
                try {
                    val Songname = parts[0]
                    val Artistname = parts[1]
                    val Rating = parts[2].toInt()
                    val comments = parts[3]

                    displayBuilder.append("Songname: $Songname\n")
                    displayBuilder.append("Artistname: $Artistname\n")
                    displayBuilder.append("Ratings: $Rating\n")
                    displayBuilder.append("Comments: $comments\n")
                    displayBuilder.append("----------------------------------\n")
                } catch (e: NumberFormatException) {
                    displayBuilder.append("Error parsing item: $itemString (Quantity not a number)\n")
                    displayBuilder.append("----------------------------------\n")
                }
            } else {
                displayBuilder.append("Invalid format for item: $itemString\n")
                displayBuilder.append("----------------------------------\n")
            }
        }
        packingListTextView.text = displayBuilder.toString()
    }
}