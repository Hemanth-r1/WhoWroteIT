package in.hr.whowroteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookInput = (EditText)findViewById(R.id.bookInput);
        mTitleText = (TextView)findViewById(R.id.titleText);
        mAuthorText = (TextView)findViewById(R.id.authorText);
        if (getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }
    public void searchBooks(View view) {
        // Get the search string from the input field.
        String queryString = mBookInput.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        //code to check network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;{
            networkInfo = connectivityManager.getActiveNetworkInfo();

            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected() &&
                    queryString.length() !=0){

                new FetchBook(mTitleText, mAuthorText).execute(queryString);
                mAuthorText.setText("");
                mTitleText.setText(R.string.loading);

                Bundle queryBundle = new Bundle();
                queryBundle.putString("queryString", queryString);
                getSupportLoaderManager().restartLoader(0, queryBundle,this);

            }else{
                if (queryString.length()==0){
                    mAuthorText.setText("");
                    mTitleText.setText(R.string.no_search_term);
                }else{
                    mAuthorText.setText("");
                    mTitleText.setText(R.string.no_network);
                }
            }
        }


    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String queryString ="";
        if (args!= null){
            queryString = args.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(data);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            int i=0;
            String title = null;
            String authors = null;

            // Look for results in the items array, exiting
            // when both the title and author
            // are found or when all items have been checked.
            while(i< itemsArray.length() && (authors == null && title == null)){
                //Get the current item information.

                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                //Try to get the author and title from the current item,
                //catch if either field is empty and move on.

                try{
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                }catch (Exception e){
                    e.printStackTrace();
                }
                //Move on to next item
                i++;

            }
            if (title != null && authors != null){

            }else{
                // If none are found, update the UI to
                // show failed results.

            }

        }catch (JSONException e){
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.

            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}