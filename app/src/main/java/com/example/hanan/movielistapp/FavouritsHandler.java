package com.example.hanan.movielistapp;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hanan on 12/1/2016.
 */

public class FavouritsHandler {
     static final String LOG_TAG = FavouritsHandler.class.getSimpleName();

    public static boolean addFavouriteItem(Activity activity,String favouriteItem,String key){
            //Get previous favorite items
            String favouriteList = getStringFromPreferences(activity,null,key);
            // Append new Favorite item
            if(favouriteList!=null){
                favouriteList = favouriteList+"&##&"+favouriteItem;
            }else{
                favouriteList = favouriteItem;
            }
            // Save in Shared Preferences
            return putStringInPreferences(activity,favouriteList,key);
        }
    private static boolean putStringInPreferences(Activity activity, String favList, String key){
        SharedPreferences.Editor editor = activity.getSharedPreferences(key,MODE_PRIVATE).edit();
      //  SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, favList);
        editor.commit();
        return true;
    }

    public static List<my_Movie> getFavouriteList(Activity activity, String key){
        String favouriteList = getStringFromPreferences(activity,null,key);
        // favouriteList have one string of json strings with ',' between their data and '-' between them
        return convertStringToArray(favouriteList);
    }

    static String getStringFromPreferences(Activity activity,String defaultValue,String key){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(key,MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }
        private static List<my_Movie> convertStringToArray(String str){

            List<my_Movie> fav_movs = new ArrayList<>();
            if(str==null)
                return null;
            else{
               /* try {
                    return getMoviesDataFromJson(str);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }/

                //List<my_Movie> movies=gson.fromJson(str,new TypeToken<List<my_Movie>>(){}.getType());

              // JSONObject moviesJson = new JSONObject(str);

              //  my_Movie[] movies = gson.fromJson(str, my_Movie[].class);
              //  System.out.println("movie "+ movies);*/
                String[] arr = str.split("&##&");
                Gson gson = new Gson();
                for(int i=0; i<arr.length;i++)
                {
                    my_Movie one_movie=gson.fromJson(arr[i], my_Movie.class);
                    fav_movs.add(one_movie);
                }
                return fav_movs;
            }

            }



    public static boolean IsFavourite(List<my_Movie> fav_list, String id)
    {
       boolean fav= false;
       // Gson gson = new Gson();
        if(fav_list!=null){
        for(int i=0;i<fav_list.size();i++)
        {

             if(id.equals(fav_list.get(i).getID()))
            {fav= true;
                break;
            }
        }}
        return fav;
    }

}
