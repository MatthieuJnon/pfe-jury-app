package fr.eseo.dis.joannomabeduneba.pfe_jury_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils.HttpUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        fastLogin();
    }

    private void fastLogin() {
        new DatabaseUsersHandler(this).execute();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
            parameters.put("q", "LOGON");
            parameters.put("user", mUsername);
            parameters.put("pass", mPassword);

            JSONObject response = HttpUtils.executeRequest("GET", HttpUtils.URL, parameters);

            Log.i("Request login", mUsername);

            if (HttpUtils.requestOK(response)) {
                parameters = new LinkedHashMap<>();
                parameters.put("q", "MYINF");
                parameters.put("user", mUsername);
                String token = HttpUtils.requestFromJson(response, "token");
                parameters.put("token", token);
                response = HttpUtils.executeRequest("GET", HttpUtils.URL, parameters);

                Log.i("Request details", mUsername);

                if (HttpUtils.requestOK(response)) {
                    User user = new User(0
                            , mUsername
                            , mPassword
                            , HttpUtils.requestFromJsonInfo(response, "descr")
                            , HttpUtils.requestFromJsonInfo(response, "forename")
                            , HttpUtils.requestFromJsonInfo(response, "surname")
                            , true
                            , token);

                    List<User> users = PFEDatabase.getInstance(Application.getAppContext()).getUserDao().getUserFromName(mUsername);

                    if( users != null ){
                        Log.i("List users", users.toString());
                        Log.i("New user", user.toString());
                        if(!users.isEmpty()){
                            users.get(0).setForename(user.getForename());
                            users.get(0).setLastname(user.getLastname());
                            users.get(0).setLogged(true);
                            users.get(0).setName(user.getName());
                            users.get(0).setPassword(user.getPassword());
                            users.get(0).setToken(user.getToken());
                            PFEDatabase.getInstance(Application.getAppContext()).getUserDao().update(users.get(0));
                            System.out.println( PFEDatabase.getInstance(Application.getAppContext()).getUserDao().getUserFromName(mUsername));
                        } else {
                            PFEDatabase
                                    .getInstance(Application.getAppContext())
                                    .getUserDao()
                                    .insert(user);
                        }
                    }

                    return user.getForename() + " " + user.getLastname();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String name) {
            mAuthTask = null;
            showProgress(false);

            if (name != null) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                myIntent.putExtra("name", name);
                LoginActivity.this.startActivity(myIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private class DatabaseUsersHandler extends AsyncTask<Void, Void, User> {

        //Prevent leak
        private WeakReference<Activity> weakActivity;


        public DatabaseUsersHandler(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected User doInBackground(Void... params) {
            List<User> users = PFEDatabase.getInstance(Application.getAppContext()).getUserDao().getAllUsers();
            for(int i = 0 ; i < users.size() ; i ++){
                System.out.println(users.get(i));
                if(users.get(i).isLogged()){
                    return users.get(i);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            LoginActivity activity = (LoginActivity) weakActivity.get();
            if(activity == null || user == null) {
                return;
            }
            activity.showProgress(true);
            activity.mAuthTask = new UserLoginTask(user.getName(), user.getPassword());
            activity.mAuthTask.execute((Void) null);

        }
    }
}

