package com.example.coolioevents.Entrant;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.R;
/**
 * Copyright 2025 Juliane Phan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class the entrant criteria and guidelines activity. It displays
 * the apps guidelines and functionality.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-30
 */
public class EntrantCriteriaGuidelinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_criteria_guidelines);

        FrameLayout backButton = findViewById(R.id.btnBack);

        // Back button onclick activity --> Leads to previous activity (Profile or Edit Profile Fragment)
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    finish());
        }
    }
}