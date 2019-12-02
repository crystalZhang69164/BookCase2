package edu.temple.bookcase2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.util.ArrayList;


public class ViewPagerFragment extends Fragment {

    public static final String BOOKS_KEY = "books";
    ViewPager viewPager;
    String[] titles;
    Context parent;
    ArrayList<Book> books;
    ArrayList<BookDetailsFragment> fragments;
    MyFragmentAdapter myFragmentAdapter;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null)
            books = bundle.getParcelableArrayList(BOOKS_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        for (int i = 0; i < books.size(); i++)
        {
            this.fragments.add(BookDetailsFragment.newInstance(books.get(i)));
        }
        viewPager = view.findViewById(R.id.viewPager);
        myFragmentAdapter = new MyFragmentAdapter(getFragmentManager());
        viewPager.setAdapter(myFragmentAdapter);

        return view;
    }

    public void updateBooks(ArrayList books){
        this.books = books;
        this.fragments.clear();
        for (int i = 0; i < books.size(); i++)
        {
            this.fragments.add(BookDetailsFragment.newInstance(this.books.get(i)));
        }
        Log.d("UPDATE", "fargs.size() " + this.fragments.size() + " adapter.getCount "+ viewPager.getAdapter().getCount());
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public static ViewPagerFragment newInstance(ArrayList<Book> books)
    {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKS_KEY, books);
        viewPagerFragment.setArguments(bundle);
        return viewPagerFragment;
    }

    public ArrayList<Book> getBooksAsArrayList(){
        return books;
    }

    public JSONArray getBooksAsJSON() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < books.size(); i++){
            jsonArray.put(books.get(i).toJSON());
        }
        Log.d("SentFromViewFrag", "" + jsonArray.toString());
        return jsonArray;
    }

    class MyFragmentAdapter extends FragmentStatePagerAdapter
    {

        public MyFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            // Causes adapter to reload all Fragments when
            // notifyDataSetChanged is called
            return POSITION_NONE;
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
