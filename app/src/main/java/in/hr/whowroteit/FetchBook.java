package in.hr.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.channels.AsynchronousChannel;

public class FetchBook extends AsyncTask<String, Void, String> {

    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    FetchBook(TextView titleText, TextView authorText) {
        this.mTitleText = new WeakReference<>(titleText);
        this.mAuthorText = new WeakReference<>(authorText);
    }


    @Override
    protected String doInBackground(String... strings) {

        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
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
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            }else{
                // If none are found, update the UI to
                // show failed results.
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }

        }catch (JSONException e){
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
    }
}
