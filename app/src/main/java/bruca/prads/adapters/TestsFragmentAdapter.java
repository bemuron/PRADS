package bruca.prads.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bruca.prads.R;

/**
 * Created by Emo on 5/10/2017.
 */

public class TestsFragmentAdapter extends RecyclerView.Adapter<TestsFragmentAdapter.MyViewHolder> {

    public ArrayList<Menus> list;
    public ArrayList<Drawable> allItemsResourceID;
    private LayoutInflater inflater;
    private TestsFragmentAdapterListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public CardView categoryContainer;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.category_name);
            //count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.category_pic);
            categoryContainer = (CardView) view.findViewById(R.id.card_view);
            //overflow = (ImageView) view.findViewById(R.id.overflow);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onMessageRowClicked(getAdapterPosition());
            //listener.onRowLongClicked(getAdapterPosition());
            //view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            //return true;
        }
    }

    public TestsFragmentAdapter(Context context, int[] images, TestsFragmentAdapterListener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
       // allItemsResourceID = images;
        list = new ArrayList<Menus>();
        Resources res = context.getResources();
        String[] tempMenusNames = res.getStringArray(R.array.SelfEvaluationTests);
        //int[] menusImages = { R.mipmap.depression, R.mipmap.depression,
          //      R.mipmap.depression, R.mipmap.depression};
        //int[] tempMenusImages = res.getIntArray(R.array.icons);
        //int [] menusImages = images;
        //list.add(images);
        for (int i = 0; i < images.length; i++) {
            Menus tempMenus = new Menus(images[i], tempMenusNames[i]);
            list.add(tempMenus);
        }
    }

    @Override
    public TestsFragmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);

        return new TestsFragmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TestsFragmentAdapter.MyViewHolder holder, int position) {
        //Album album = albumList.get(position);
        TestsFragmentAdapter.Menus options = list.get(position);
        holder.title.setText(options.menus);
        //holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(context).load(options.imageId).into(holder.thumbnail);
        // apply click events
        applyClickEvents(holder, position);
    }

    //handling different click events
    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.categoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.categoryContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }
/*
    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }
*/
    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface TestsFragmentAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
/*

    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Menus temp = list.get(i);
        holder.myMenus.setImageResource(temp.imageId);
        holder.myMenusText.setText(temp.menus);
       // row.setOnClickListener(new ListClickHandler());//////////////////////////
        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(temp.menus.equals("Study Tips")){
                    Intent intent = new Intent(context, StudyTips.class);
                    intent.putExtra("selected", temp.menus);
                    context.startActivity(intent);
                    Toast.makeText(context, "ID= "+ temp.imageId,
                            Toast.LENGTH_SHORT).show();
                } else if(temp.menus.equals("My Goals and Objectives")){
                    Intent intent = new Intent(context, GoalsListActivity.class);
                    intent.putExtra("selected", temp.menus);
                    context.startActivity(intent);
                }

            }
        });

        return row;
    }

class ViewHolder {
    ImageView imageView;
    ImageView myMenus;
    TextView myMenusText;
   // TextView MyMenusPrice;

    ViewHolder(View v) {
        myMenus = (ImageView) v.findViewById(R.id.category_pic);
        myMenusText = (TextView) v.findViewById(R.id.category_name);
       // MyMenusPrice = (TextView) v.findViewById(R.id.category_text);
    }
}
*/
class Menus {
    int imageId;
    String menus;

    Menus(int imageId, String menus) {
        this.imageId = imageId;
        this.menus = menus;
    }
}

}
