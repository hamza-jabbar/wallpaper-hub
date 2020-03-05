package com.ahzam.example.wallpaperhub.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahzam.example.wallpaperhub.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final int GOOGLE_SIGN_IN_CODE = 212;     //  Detect Intent Callback
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //  User is not logged in; viewing the page below
            return inflater.inflate(R.layout.fragment_settings_default, container, false);
        }
        //  Once logged in, view this screen
        return inflater.inflate(R.layout.fragment_settings_logged_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        //  Initialize GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //  User is logged in --> Display user information
            CircleImageView ivUserIcon = view.findViewById(R.id.fsl_ivUserLoggedIn);
            TextView tvName = view.findViewById(R.id.fsl_tvName);
            TextView tvEmail = view.findViewById(R.id.fsl_tvEmail);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(getActivity())
                    .load(user.getPhotoUrl().toString())
                    .into(ivUserIcon);

            tvName.setText(user.getDisplayName());
            tvEmail.setText(user.getEmail());

            //  User can logout
            view.findViewById(R.id.fsl_tvLogout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Sign out from Firebase and Google Client
                    FirebaseAuth.getInstance().signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area, new SettingsFragment()).commit();
                        }
                    });


                }
            });


        } else {
            view.findViewById(R.id.fsd_btnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Google SignIn intent
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, GOOGLE_SIGN_IN_CODE);
                }
            });
        }

    }

    //  Track the callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            //  The callback is gained
            Task<GoogleSignInAccount> gsaTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount gsaAccount = gsaTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(gsaAccount);

            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getActivity(), R.string.login_successful, Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area, new SettingsFragment()).commit();

                        } else {
                            Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
