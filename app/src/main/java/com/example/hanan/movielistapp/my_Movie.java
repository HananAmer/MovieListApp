package com.example.hanan.movielistapp;

/**
 * Created by hanan on 11/14/2016.
 */

public class my_Movie {
    private String ID;
    private String TITLE;
    final String IMAGE_BASE_URL="http://image.tmdb.org/t/p/w185/";
    private String Poster_Path;
    private String Overview;
    private String Release_date;
    private String Vote_average;
   // private boolean IsFavourite;
    void setID(String id){
        ID=id;
    }
    String getID(){
        return ID;
    }
    void setTITLE(String title){
        TITLE=title;
    }
    String getTITLE(){
        return TITLE;
    }
    void setPoster_Path(String path){
        Poster_Path=IMAGE_BASE_URL+path;
    }
    String getPoster_Path(){
        return Poster_Path;
    }
    void setPlot(String plot){Overview=plot;}
    String getPlot(){return Overview;}
    void set_release_date(String date){Release_date=date;}
    String get_release_date(){return Release_date;}
    void set_vote_avg(String rate){Vote_average=rate;}
    String get_vote_avg(){return Vote_average;}
   /* void setIsFavourite(boolean fav)
    {IsFavourite= fav;}
    boolean getIsFavourite(){return IsFavourite;}*/
}
