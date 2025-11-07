package com.example.coolioevents;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class UpdateProfileFragment extends Fragment {

    private EditText editUsername, editName, editEmail;
    private Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        editUsername = view.findViewById(R.id.edit_username);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        btnSave = view.findViewById(R.id.btn_save_profile);

        // (Optional) preload with current user info
//        editUsername.setText("coolio_user");
//        editName.setText("John Doe");
//        editEmail.setText("john.doe@example.com");

        btnSave.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            // In a real app, update this info in Firebase/SQLite/etc.
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            // navigate back
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
