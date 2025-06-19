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
                        val name = parts[0]
                        val category = parts[1]
                        val quantity = parts[2].toInt()
                        val comments = parts[3]

                        // Add the new item to the *internal persistent lists* here.
                        // This ensures that when the user later views the full list
                        // (e.g., by returning from MainActivity, or if this ScreenTwo
                        // is re-opened without new items), these new items are included.
                        itemSongName.add(name)
                        itemArtistName.add(category)
                        itemRating.add(quantity)
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
                stringBuilder.append("No items found with 2 or more quantities.")
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
            // Use FLAG_ACTIVITY_CLEAR_TOP and FLAG_ACTIVITY_NEW_TASK
            // This clears all activities on top of MainActivity and brings MainActivity to the front.
            // It also ensures that the MainActivity's `newlyAddedItemsBuffer` will be cleared
            // if MainActivity itself is recreated due to these flags.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finish Screentwo so it's removed from the back stack
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
            displayBuilder.append("Item: ${itemSongName[i]}\n")
            displayBuilder.append("Category: ${itemArtistName[i]}\n")
            displayBuilder.append("Quantity: ${itemRating[i]}\n")
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
                    val name = parts[0]
                    val category = parts[1]
                    val quantity = parts[2].toInt()
                    val comments = parts[3]

                    displayBuilder.append("Item: $name\n")
                    displayBuilder.append("Category: $category\n")
                    displayBuilder.append("Quantity: $quantity\n")
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