package ayman.tech2go.com.sudantractor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


public class RVArrayAdapter_Profile extends RecyclerView.Adapter<RVArrayAdapter_Profile.MyViewHolder> {
    private LayoutInflater inflater;
    private List<Information> data = Collections.emptyList();
    private final static int FADE_DURATION = 1000; // in milliseconds
    private Context context;

    public RVArrayAdapter_Profile(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.custom_profle_list, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        final Information current = data.get(position);
        viewHolder.textView.setText(current.name);
        viewHolder.textViewDes.setText(current.phone);
        final int TransitionPosition = position + 1;

        /*viewHolder.imageView.setImageResource(current.icon_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.imageView.setTransitionName("accidant");
        }*/
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Toast.makeText(inflater.getContext(), current.name, Toast.LENGTH_LONG).show();

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Intent intent = new Intent(inflater.getContext(), MainActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) inflater.getContext(), viewHolder.imageView,
                                    viewHolder.imageView.getTransitionName());

                    //view.setTransitionName(getString(R.string.transition_name) + 1);

                    intent.putExtra("awareness_" + TransitionPosition, "");
//                    Toast.makeText(inflater.getContext(), view.getTransitionName(), Toast.LENGTH_LONG).show();

                    inflater.getContext().startActivity(intent, options.toBundle());
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(inflater.getContext(), MainActivity.class);
                    intent.putExtra("awareness_" + TransitionPosition, "");
                    inflater.getContext().startActivity(intent);
                }*/

//                if (viewHolder.textViewDes.getText() == "") {
//                    viewHolder.textViewDes.setText(current.icon_description);
//                }else{
//                    viewHolder.textViewDes.setText("");
//                }

            }
        });
        setFadeAnimation(viewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textViewDes;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.profile_name);
            textViewDes = (TextView) itemView.findViewById(R.id.profile_phone);
//            imageView = (ImageView) itemView.findViewById(R.id.section_icon_system);
        }
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    /*private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }*/
}
