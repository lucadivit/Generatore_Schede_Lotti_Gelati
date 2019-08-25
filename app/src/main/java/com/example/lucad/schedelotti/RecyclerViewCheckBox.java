package com.example.lucad.schedelotti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecyclerViewCheckBox implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;
    private RecyclerView recyclerView;
    private ListView listView;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, ArrayList<Object> itemViews);

        public void onLongItemClick(View view, int position, ArrayList<Object> itemViews);
    }

    GestureDetector mGestureDetector;

    public RecyclerViewCheckBox(Context context, final RecyclerView recyclerView, final OnItemClickListener listener, final List<Integer> itemViewIdToReturn) {
        mListener = listener;
        this.recyclerView = recyclerView;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                ArrayList<Object> itemViews = new ArrayList<>();
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(childView);
                if(itemViewIdToReturn.size() > 0){
                    Iterator iterator = itemViewIdToReturn.iterator();
                    while (iterator.hasNext()){
                        Integer id = (Integer) iterator.next();
                        Object obj = getItemViewByPosition(position, id);
                        itemViews.add(obj);
                    }
                }
                mListener.onItemClick(childView, position, itemViews);
                return true;
                /*
                //Questa linea prende l'item view che in questo caso è il checkedtextview
                Object checkedTextView = getItemViewByPosition(position, R.id.checked_text_view_ing);
                mListener.onItemClick(childView, position, checkedTextView);
                return true;*/
            }

            @Override
            public void onLongPress(MotionEvent e) {
                ArrayList<Object> itemViews = new ArrayList<>();
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child), itemViews);
                }
            }
        });
    }

    public Object getItemViewByPosition(int position, int id){
        return recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(id);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            //mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
