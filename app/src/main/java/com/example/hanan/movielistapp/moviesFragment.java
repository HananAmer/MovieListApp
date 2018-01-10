package com.example.hanan.movielistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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


/**
 * Created by hanan on 11/11/2016.
 */

public class moviesFragment extends Fragment {

    List<my_Movie> result;
    GridView gridView;
    MovieAdapter mMoviesAdapter;
private TwoPaneListener listener;
    String sort;
    View rootView;

    void setListener(TwoPaneListener Tp_Listener){

        listener=Tp_Listener;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.movie_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
     gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                             my_Movie movie = mMoviesAdapter.getItem(position);
             Gson gson = new Gson();
             String json = gson.toJson(movie);
             System.out.println("movie's json is: "+ json);
             ((MainActivity) getActivity()).setSelectedData(json);


                         }
     });

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateMovie();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.sort_type) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));

            return true;
        }

     //   mMoviesAdapter =new MovieAdapter(result,getActivity());

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void updateMovie() {
       // sort="top_rated?";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        System.out.println("the value of favourite option:     "+ getString(R.string.favourites_movies));

        if(prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_top)).equals("favourites"))
        {
           getFavourites();
        }
        else if(isOnline()){
        sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_top))+"?";
            FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute();

            ((TextView) rootView.findViewById(R.id.sort_text_view))
                    .setText("the " +prefs.getString(getString(R.string.pref_sort_key),
                            getString(R.string.pref_sort_top))+" movies");
       // mMoviesAdapter= new MovieAdapter(result,getActivity());
            }
        else {
            Toast.makeText(getActivity(), "Sorry, there is no INTERNET CONNECTION  ", Toast.LENGTH_SHORT).show();
            ((TextView) rootView.findViewById(R.id.sort_text_view))
                    .setText("no movies to show");
            mMoviesAdapter= new MovieAdapter(new ArrayList<my_Movie>(),getActivity());
            gridView.setAdapter(mMoviesAdapter);
        }

    }

    void getFavourites()
    {
        FavouritsHandler fav_handler =new FavouritsHandler();
        result= fav_handler.getFavouriteList(getActivity(),"favourites");

        if(result==null)
        {
            mMoviesAdapter= new MovieAdapter(new ArrayList<my_Movie>(),getActivity());
            gridView.setAdapter(mMoviesAdapter);
            ((TextView) rootView.findViewById(R.id.sort_text_view))
                    .setText("no favourites to show ");
        }
        else{
            mMoviesAdapter=new MovieAdapter(result,getActivity());
            gridView.setAdapter(mMoviesAdapter);
            ((TextView) rootView.findViewById(R.id.sort_text_view))
                    .setText("My Favourite Movie List  ");
        }

    }

    @Override
public void onStart(){
        super.onStart();
 //       updateMovie();
    }

    public class FetchMoviesTask extends AsyncTask<String, Integer, List<my_Movie>> {
        public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*extract the data from json string*/

        private List<my_Movie> getMoviesDataFromJson(String movieJsonStr)
                throws JSONException {

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            result = new ArrayList<>();
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_top));


            if (!sortType.equals(getString(R.string.pref_sort_top))) {
                Log.d(LOG_TAG, "Sort type found: " + sortType);
            }
            // These are the names of the JSON objects that need to be extracted

            String id;
            String title;
            String image_path;
            String plot;
            String release_date;
            String rate;

            JSONObject one_movie;
            for(int i = 0; i < moviesArray.length(); i++) {

                one_movie = moviesArray.getJSONObject(i);

                id= one_movie.getString("id");
                title = one_movie.getString("original_title");
                image_path=one_movie.getString("poster_path");
                plot=one_movie.getString("overview");
                release_date=one_movie.getString("release_date");
                rate=one_movie.getString("vote_average");

                my_Movie mov_obj= new my_Movie();
                mov_obj.setID(id);
                mov_obj.setPoster_Path(image_path);
                mov_obj.setTITLE(title);
                mov_obj.setPlot(plot);
                mov_obj.set_release_date(release_date);
                mov_obj.set_vote_avg(rate);

               // Log.v(LOG_TAG, "Movie entry: "+ i+ "\t"+mov_obj.getID()+"\t"+ mov_obj.getPoster_Path()+"\t"+mov_obj.getTITLE());

                result.add(i,mov_obj);
                Log.v(LOG_TAG, "Movie entry: "+ i+ "\t"+result.get(i));
           }

          /*  for (my_Movie s : result) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }*/
            return result;

        }


        @Override
        protected List<my_Movie> doInBackground(String... params) {
          /*  String sort_by="";
           if (params.length == 0) {
               sort_by="now_playing";
           }
           */
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                final String BASE_URL =("http://api.themoviedb.org/3/movie/");
                String API_key_Param="api_key";

                Uri builtUri = Uri.parse(BASE_URL+sort).buildUpon().
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies string: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally{
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
            try {
                return getMoviesDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the data.
            return null;
        }

        /**@Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }*/

        @Override
        protected void onPostExecute(List<my_Movie> movies) {
            if (movies != null) {
              mMoviesAdapter= new MovieAdapter(movies,getActivity());
//
                gridView.setAdapter(mMoviesAdapter);

                // New data is back from the server.
            }
        }

    }
}