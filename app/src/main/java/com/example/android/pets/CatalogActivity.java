/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

import com.example.android.pets.data.PetDbHelper;

import static android.R.attr.id;
import static android.R.attr.name;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // projection for query method
        String[] projection = {PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor c = db.query(PetEntry.TABLE_NAME, projection, null, null, null, null, null);

        // Find TextView on which to display data from the cursor
        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {

            // Find index of each column we want data from
            int idColumnIndex = c.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedComunIndex = c.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = c.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Build a String to display with StringBuilder
            StringBuilder sb = new StringBuilder();
            sb.append("Number of rows in pets database table: " + c.getCount() + "\n");
            sb.append("\n");
            sb.append("id - name - breed - gender - weight");
            sb.append("\n");
            // Gender of a pet to display
            String petGender = "";

            // Logic to build the string to display
            while (c.moveToNext()){
                sb.append("\n");
                sb.append(c.getInt(idColumnIndex) + " - ");
                sb.append(c.getString(nameColumnIndex) + " - ");
                sb.append(c.getString(breedComunIndex) + " - ");
                switch (c.getInt(genderColumnIndex)){
                    case PetEntry.GENDER_MALE:
                        petGender = getString(R.string.gender_male);
                        break;
                    case PetEntry.GENDER_FEMALE:
                        petGender = getString(R.string.gender_female);
                        break;
                    default:
                        petGender = getString(R.string.gender_unknown);
                        break;
                }
                sb.append(petGender + " - ");
                sb.append(c.getInt(weightColumnIndex));
            }
            displayView.setText(sb.toString());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            c.close();
        }
    }

    // Helper function to isnert dummy data
    private void insertPet(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        db.insert(PetEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
