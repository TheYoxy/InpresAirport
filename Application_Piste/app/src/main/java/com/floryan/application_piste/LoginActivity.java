package com.floryan.application_piste;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import LUGAP.ReponseLUGAP;
import LUGAP.RequeteLUGAP;
import LUGAP.TypeReponseLUGAP;
import LUGAP.TypeRequeteLUGAP;
import NetworkObject.Login;
import Tools.DigestCalculator;
import Tools.Procedural;

public class LoginActivity extends AppCompatActivity {
    public final static String USERNAME = "Username";
    private static Socket s = null;
    private static ObjectInputStream ois = null;
    private static ObjectOutputStream oos = null;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String username;

    public static Socket getS() {
        return s;
    }

    public static ObjectInputStream getOis() {
        return ois;
    }

    public static ObjectOutputStream getOos() {
        return oos;
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = findViewById(R.id.user);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @SuppressLint("StaticFieldLeak")
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress(true);
            }

            @Override
            protected Boolean doInBackground(Void[] objects) {
                try {
                    Looper.prepare();
                    s = new Socket(InetAddress.getByName(getString(R.string.ServerName)), getResources().getInteger(R.integer.ServerPort));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean o) {
                super.onPostExecute(o);
                if (o) {
                    mAuthTask = new UserLoginTask(user, password);
                    mAuthTask.execute();
                }
                showProgress(false);
            }
        }.execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private final static int SOCKET_ERROR = -1;
        private final static int REPONSE_ERROR = -2;
        private final static int EXCEPTION = -3;
        private final static int UNKNOWN_LOGIN = -4;
        private final static int BAD_PASSWORD = -5;
        private final static int UNKNOWN = -6;
        private final static int LOG = 0;

        private final String mEmail;
        private final String mPassword;

        public UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            //TODO Connexion via un appel au serveur
            ReponseLUGAP rep;
            int challenge = 0;
            try {
                if (s == null) return SOCKET_ERROR;
                if (oos == null) oos = new ObjectOutputStream(s.getOutputStream());
                RequeteLUGAP r = new RequeteLUGAP(TypeRequeteLUGAP.TryConnect, Procedural.IpPort(s));
                oos.writeObject(r);

                if (ois == null)
                    ois = new ObjectInputStream(s.getInputStream());
                rep = (ReponseLUGAP) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return EXCEPTION;
            }

            if (rep == null) return REPONSE_ERROR;
            if (rep.getCode() == TypeReponseLUGAP.OK) {
                if (rep.getParam() == null) return REPONSE_ERROR;
                challenge = (int) rep.getParam();
            }

            try {
                oos.writeObject(new RequeteLUGAP(TypeRequeteLUGAP.Login, new Login(mEmail, DigestCalculator.hashPassword(mPassword, challenge)), Procedural.IpPort(s)));
                rep = (ReponseLUGAP) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return EXCEPTION;
            }
            if (rep == null) return REPONSE_ERROR;
            switch ((TypeReponseLUGAP) rep.getCode()) {
                case UNKNOWN_LOGIN:
                    return UNKNOWN_LOGIN;
                case BAD_PASSWORD:
                    return BAD_PASSWORD;
                case LOG:
                    username = (String) rep.getParam();
                    return LOG;
            }
            return UNKNOWN;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            showProgress(false);
            switch (success) {
                case LOG:
                    //TODO Mettre tout les string pour la traduction
                    Toast.makeText(LoginActivity.this, "Connecté", Toast.LENGTH_LONG).show();
                    //Redirection vers une autre activité
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra(USERNAME, username);
                    startActivity(i);
                    return;
                case SOCKET_ERROR:
                    Toast.makeText(LoginActivity.this, "Erreur de socket lors de la connexion", Toast.LENGTH_LONG).show();
                    break;
                case REPONSE_ERROR:
                    Toast.makeText(LoginActivity.this, "Erreur de réponse lors de la connexion", Toast.LENGTH_LONG).show();
                    break;
                case EXCEPTION:
                    Toast.makeText(LoginActivity.this, "Exception lors de l'envoi de données", Toast.LENGTH_LONG).show();
                    break;
                case UNKNOWN_LOGIN:
                    mUserView.setError(getString(R.string.error_invalid_user));
                    mUserView.requestFocus();
                    break;
                case BAD_PASSWORD:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case UNKNOWN:
                    Toast.makeText(LoginActivity.this, "Erreur inconnue", Toast.LENGTH_LONG).show();
                    break;
            }
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mAuthTask = null;
        }
    }
}

