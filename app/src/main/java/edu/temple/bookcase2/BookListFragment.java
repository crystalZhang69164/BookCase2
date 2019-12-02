package edu.temple.bookcase2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;



public class BookListFragment extends Fragment {

    // Instead of having this be GetBookInterface I made it Context
    Context parent;
    public final static String BOOKS_KEY = "books";
    ArrayList<Book> books;
    String[] titles;
    ListView listView;

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GetBookInterface)
            parent = context;
        else
            throw new RuntimeException("Didn't implement BookListFragment's interface");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            books = bundle.getParcelableArrayList(BOOKS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listView = (ListView) inflater.inflate(R.layout.fragment_book_list, container, false);

        listView.setAdapter(new BookListFragmentAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((GetBookInterface) BookListFragment.this.parent).bookSelected(books.get(position)); //this needs casting
            }
        });
        return listView;
    }

    public void updateBooks(ArrayList books){
        this.books = books;
        titles = new String[books.size()];
        for (int i = 0; i < books.size(); i++) {
            titles[i] = this.books.get(i).getTitle();
        }
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        Log.d("UPDATE", "this.titles " + this.titles.length + " adapter.getCount " + listView.getAdapter().getCount());
    }

    public ArrayList<Book> getBooksAsArrayList(){
        return books;
    }

    public static BookListFragment newInstance(ArrayList<Book> books)
    {
        BookListFragment bookListFragment = new BookListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKS_KEY, books);
        bookListFragment.setArguments(bundle);
        return bookListFragment;
    }

    interface GetBookInterface
    {
        void bookSelected (Book book);
    }

    class BookListFragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView;
            if (convertView instanceof TextView)
                textView = (TextView) convertView;
            else
                textView = new TextView(getContext());

            textView.setText(books.get(position).getTitle());
            textView.setTextSize(24);
            textView.setGravity(Gravity.CENTER);
            return textView;
        }
    }
}
