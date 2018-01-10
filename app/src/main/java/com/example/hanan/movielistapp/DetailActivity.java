package com.example.hanan.movielistapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activ_detail, new DetailFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        public ArrayAdapter<String> TrailersAdapter;
        public ArrayAdapter<String> ReviewsAdapter;

        public DetailFragment() {
        }

        String mov_id;
        Holder result = new Holder();
      List<String> trailer_key= new ArrayList<String>();
        ImageButton favourite_Btn1;
        FavouritsHandler fav_handler=new FavouritsHandler();
        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            final String movie_data;

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                movie_data = intent.getStringExtra(Intent.EXTRA_TEXT);
                //movie_data = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            }else {
                movie_data = getArguments().getString(Intent.EXTRA_TEXT);
                //movie_data = getArguments().getStringArray(Intent.EXTRA_TEXT);
            }
            Gson gson =new Gson();
            my_Movie this_movie =gson.fromJson(movie_data, my_Movie.class);
            System.out.println("movies data = movie's id: "+this_movie.getID()+"  movie's title: "+ this_movie.getTITLE());

                ((TextView) rootView.findViewById(R.id.detail_movie_title))
                        .setText(this_movie.getTITLE());
                ((TextView) rootView.findViewById(R.id.plot_textView))
                        .setText("Plot:  " + this_movie.getPlot());
                Picasso.with(getActivity()).load(this_movie.getPoster_Path()).into((ImageView) rootView.findViewById(R.id.poster_imageView));
                mov_id = String.valueOf(this_movie.getID());
                ((TextView) rootView.findViewById(R.id.release_date_textView))
                        .setText("release date:  " +this_movie.get_release_date());
                ((TextView) rootView.findViewById(R.id.rating_textView))
                        .setText("rate:  " + this_movie.get_vote_avg()+ "/10");
                         TrailersAdapter =
                        new ArrayAdapter<String>(
                                getActivity(), // The current context (this activity)
                                R.layout.list_item_trailer, // The name of the layout ID.
                                R.id.trailer_textView, // The ID of the textview to populate.
                                new ArrayList<String>());
                ReviewsAdapter =
                        new ArrayAdapter<String>(
                                getActivity(), // The current context (this activity)
                                R.layout.list_item_review, // The name of the layout ID.
                                R.id.list_item_review_TextView, // The ID of the textview to populate.
                                new ArrayList<String>());

                ListView listView1 = (ListView) rootView.findViewById(R.id.trailers_Listview);
                listView1.setAdapter(TrailersAdapter);
                ListView listView2 = (ListView) rootView.findViewById(R.id.Listview_reviews);
                listView2.setAdapter(ReviewsAdapter);


                FetchTrailers_Reviews_Task tr_rev = new FetchTrailers_Reviews_Task();
                tr_rev.execute(mov_id);

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                       //String key = TrailersAdapter.getItem(position);
                        if(isOnline())
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer_key.get(position))));
                        else
                            Toast.makeText(getActivity(), "Sorry, there is no INTERNET CONNECTION  ", Toast.LENGTH_SHORT).show();
                    }


                });

                 final String LOG_TAG = DetailFragment.class.getSimpleName();
            favourite_Btn1 = (ImageButton) rootView.findViewById(R.id.fav_Btn_off);
                Log.v(LOG_TAG, "get favourites result: " + fav_handler.getStringFromPreferences(getActivity(),null,"favourites"));

           if (fav_handler.getStringFromPreferences(getActivity(),null,"favourites")==null||
                    !fav_handler.IsFavourite(fav_handler.getFavouriteList(getActivity(),"favourites"), mov_id)) {

                favourite_Btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View btn) {
                      /*  if (btn.isSelected()) {
                           btn.setSelected(false);
                       } else {*/
                            favourite_Btn1.setSelected(true);
                            btn.setSelected(true);
                            favourite_Btn1.setBackgroundResource(R.drawable.fav_btn_on);
                            Gson gson = new Gson();
                            fav_handler.addFavouriteItem(getActivity(), movie_data,"favourites");
                           // fav_handler.addFavouriteItem(getActivity(),mov_id,"FavIDs");

                    }
                });
            }
            else {
                favourite_Btn1.setSelected(true);
             //   btn.setSelected(true);
                favourite_Btn1.setBackgroundResource(R.drawable.fav_btn_on);
            }
            return rootView;
        }

     /*   @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            .getMenuInflater().inflate(R.menu.detail, menu);
            return true;
        }*/
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail, menu);
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();


            if (id == R.id.detail_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        public class FetchTrailers_Reviews_Task extends AsyncTask<String, Integer, Holder> {
            public final String LOG_TAG = FetchTrailers_Reviews_Task.class.getSimpleName();


            List<String> getTrailer_DataFromJson(String movieJsonStr)
                    throws JSONException
            {
                JSONObject moviesJson = new JSONObject(movieJsonStr);
                JSONArray movie_trailerArray = moviesJson.getJSONArray("results");

                String key;
                JSONObject trailers;
                for(int i = 0; i < movie_trailerArray.length(); i++) {
                    trailers = movie_trailerArray.getJSONObject(i);
                    key = trailers.getString("key");

                    //Log.v(LOG_TAG, "Movie trailer key: "+ i+ "\t"+key);
                    result.trailers.add(key);

                }

              /*  for (String s : result.trailers) {
                    Log.v(LOG_TAG, "Movie trailer key: " + s);
                }*/
                return result.trailers;

            }
            List<String> getReviews_DataFromJson(String movieJsonStr)
                    throws JSONException
            {
                JSONObject moviesJson = new JSONObject(movieJsonStr);
                JSONArray movie_reviewsArray = moviesJson.getJSONArray("results");

                String content;
                JSONObject reviews;
                for(int i = 0; i < movie_reviewsArray.length(); i++) {
                    reviews= movie_reviewsArray.getJSONObject(i);
                    content = reviews.getString("content");

                 //   Log.v(LOG_TAG, "Movie trailer content: "+ i+ "\t"+content);
                    result.reviews.add(content);
                    //Log.v(LOG_TAG, "Movie entry: "+ i+ "\t"+result.get(i));
                }

              /*  for (String s : result.reviews) {
                    Log.v(LOG_TAG, "Movie review key: " + s);
                }*/
                return result.reviews;

            }
            String fetchJsonString(String mov_id,String fetch_what)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

// Will contain the raw JSON response as a string.
                String movieJsonStr = null;

                try {
                    final String BASE_URL = ("http://api.themoviedb.org/3/movie/");
                    String API_key_Param = "api_key";

                    Uri builtUri = Uri.parse(BASE_URL + mov_id + fetch_what).buildUpon().
                            appendQueryParameter(API_key_Param, BuildConfig.Movie_DB_API_KEY)
                            .build();
                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        movieJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        movieJsonStr = null;
                    }
                    movieJsonStr = buffer.toString();
                  //  Log.v(LOG_TAG, fetch_what+ "  string: " + movieJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);

                    movieJsonStr = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                return movieJsonStr;
            }
            @Override
            protected Holder doInBackground(String... params) {

                if (params.length == 0) {
                    return null;
                }


                try {
                    getTrailer_DataFromJson(fetchJsonString(params[0],"/videos?"));
                    getReviews_DataFromJson(fetchJsonString(params[0],"/reviews?"));
                    return result;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


                return null;
            }
            @Override
            protected void onPostExecute(Holder holder_result){
                if (holder_result.trailers != null) {
                    TrailersAdapter.clear();
                    int i=0;
                    for (String trailerStr : holder_result.trailers) {
                        TrailersAdapter.add("Trailer   "+Integer.toString(i+1));
                       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+holder_result.trailers.get(i))));
                        i++;

                    }
                    trailer_key=holder_result.trailers;
                }
                    if (holder_result.reviews != null) {
                        ReviewsAdapter.clear();
                        for(String reviewStr : holder_result.reviews) {
                            ReviewsAdapter.add(reviewStr);
                        }
                    // New data is back from the server.  Hooray!
                }
            }

        }
        public class Holder
        {
            List<String> trailers= new ArrayList<>();
            List<String> reviews= new ArrayList<>();

        }
    }
}
