package com.example.playlistapp

import android.content.Intent
import android.media.Rating
import android.os.Bundle
import android.view.View // Import View for visibility
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout // Import LinearLayout for the container
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistapp.DetailedView
import com.example.playlistapp.R

/**
 * MainActivity serves as the first screen of the travel packing list manager app.
 * It allows users to input details for a new packing item and navigate to the
 * second screen (DetailedView) to view the complete packing list or exit the app.
 */
class MainActivity : AppCompatActivity() {

    // Declare UI elements
  private lateinit var StartAddingplaylistButton :Button
    private lateinit var saveItemButton: Button        // Button to save the entered item
    private lateinit var nextButton: Button
    private lateinit var exitButton: Button

    // Declare EditText fields for capturing packing item details.
    private lateinit var editTextSongname: EditText
    private lateinit var editTextArtistname: EditText
    private lateinit var editTextRating: EditText
    private lateinit var editTextComments: EditText

    // Declare the container for input fields
    private lateinit var inputContainer: LinearLayout

    // List to temporarily store the details of newly added items.
    private val newlyAddedItemsBuffer = ArrayList<String>()

    /**
     * Called when the activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        StartAddingplaylistButton = findViewById(R.id.StartAddingplaylistButton)
        saveItemButton = findViewById(R.id.saveItemButton) // Initialize the new save button
        nextButton = findViewById(R.id.nextButton)
        exitButton = findViewById(R.id.ExitButton)

        // Initialize EditText fields
        editTextSongname = findViewById(R.id.editTextSongName)
        editTextArtistname = findViewById(R.id.editTextArtistname)
        editTextRating = findViewById(R.id.editTextRating)
        editTextComments = findViewById(R.id.editTextComments)

        // Initialize the input container
        inputContainer = findViewById(R.id.inputContainer)

        // --- Initial Visibility Setup ---
        inputContainer.visibility = View.GONE // Hide input fields initially
        saveItemButton.visibility = View.GONE // Hide save button initially
        nextButton.visibility = View.GONE     // Hide next button initially

        // Set up the "Add New Packing Item" button click listener
        // This button now makes the input fields and related buttons visible
        StartAddingplaylistButton.setOnClickListener {
            inputContainer.visibility = View.VISIBLE    // Show the input fields
            saveItemButton.visibility = View.VISIBLE    // Show the save button
            nextButton.visibility = View.VISIBLE        // Show the next button
            StartAddingplaylistButton.visibility = View.GONE // Hide this button once input is visible
        }

        // Set up the "Save Item" button click listener (this was the old "Add to Packing List" logic)
        saveItemButton.setOnClickListener {
            val Songname = editTextSongname.text.toString().trim()
            val Artistname = editTextArtistname.text.toString().trim()
            val RatingStr = editTextRating.text.toString().trim()
            val comments = editTextComments.text.toString().trim()

            // --- Input Validation ---
            if (Songname.isEmpty()) {
                Toast.makeText(this, "Please enter an Songname", Toast.LENGTH_SHORT).show()
                editTextSongname.error = "Required"
                return@setOnClickListener
            }
            if (Artistname.isEmpty()) {
                Toast.makeText(this, "Please enter a Artistname", Toast.LENGTH_SHORT).show()
                editTextArtistname.error = "Required"
                return@setOnClickListener
            }
            if (RatingStr.isEmpty()) {
                Toast.makeText(this, "Please enter a Rating", Toast.LENGTH_SHORT).show()
                editTextRating.error = "Required"
                return@setOnClickListener
            }

            val quantity: Int
            try {
                quantity = RatingStr.toInt()
                if (quantity <= 0) {
                    Toast.makeText(this, "Quantity must be a positive number", Toast.LENGTH_SHORT).show()
                    editTextRating.error = "Must be > 0"
                    return@setOnClickListener
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Quantity must be a valid number", Toast.LENGTH_SHORT).show()
                editTextRating.error = "Invalid number"
                return@setOnClickListener
            }

            val finalComments = if (comments.isEmpty()) "No comments" else comments

            val newPackingItemDetails = "$Songname,$Artistname,$finalComments"

            // Add the details of the newly added item to the buffer list.
            newlyAddedItemsBuffer.add(newPackingItemDetails)

            // Clear the input fields after successful addition.
            editTextSongname.text.clear()
            editTextArtistname.text.clear()
            editTextRating.text.clear()
            editTextComments.text.clear()

            Toast.makeText(this, "Item '$Songname' added to buffer. Add more or View List.", Toast.LENGTH_LONG).show()
        }

        // Set up the "View Packing List (Screen 2)" button click listener
        nextButton.setOnClickListener {
            val intent = Intent(this, DetailedView::class.java)
            // Pass the buffer list of newly added items to Screentwo if it's not empty.
            if (newlyAddedItemsBuffer.isNotEmpty()) {
                intent.putStringArrayListExtra("NEWLY_ADDED_PACKING_ITEMS", newlyAddedItemsBuffer)
                newlyAddedItemsBuffer.clear() // Clear the buffer after passing the items
            }
            startActivity(intent)
        }

        // Set up the "Exit App" button click listener
        exitButton.setOnClickListener {
            finishAffinity()
        }
    }
}