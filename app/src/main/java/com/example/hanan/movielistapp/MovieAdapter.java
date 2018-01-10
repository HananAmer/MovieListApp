package com.example.hanan.movielistapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanan on 10/21/2016.
 */

public class MovieAdapter extends BaseAdapter implements OnClickListener {
   List <my_Movie> mMovies;
    private Context mContext;

    public MovieAdapter(){
        mMovies= new ArrayList<>();
    }
    public MovieAdapter(Context c) {
        mMovies= new ArrayList<>();
        mContext = c;
    }
    public MovieAdapter(List<my_Movie> movies, Context c) {
        this.mMovies= movies;
        mContext = c;
    }
    public MovieAdapter(int pos, my_Movie movie, Context c) {
        this.mMovies.add(pos,movie);
        mContext = c;
    }
    public void add(my_Movie mov)
{
    mMovies.add(mov);
}
    public void addAll(List<my_Movie> movieList)
    {
        mMovies.addAll(movieList);
    }
    @Override
    public int getCount() {
        return mMovies.size();
    }
    @Override
    public my_Movie getItem(int i) {
        return mMovies.get(i);
    }
    @Override
    public long getItemId(int i) {

        return Long.parseLong(mMovies.get(i).getID());
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view=convertView;
        if (view == null) {
            LayoutInflater inflater= (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_list_item,null);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvMovieName.setText(mMovies.get(i).getTITLE());
        Picasso.with(mContext).load(mMovies.get(i).getPoster_Path()).into(viewHolder.ivMoviePoster);
        //view.setOnClickListener(new OnItemClickListener(i));
       // Picasso.with(mContext).load(GlobalValues.IMAGE_BASE_URL+
        //mMovies.get(i).getPoster_Path()).into(viewHolder.ivMoviePoster);
        return view;

    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }


    public class ViewHolder{
        ImageView ivMoviePoster;
        TextView tvMovieName;

    public ViewHolder(View view){
        ivMoviePoster=(ImageView) view.findViewById(R.id.iv_movie_poster);
        tvMovieName= (TextView) view.findViewById(R.id.tv_movie_poster);
    }
    }

}
