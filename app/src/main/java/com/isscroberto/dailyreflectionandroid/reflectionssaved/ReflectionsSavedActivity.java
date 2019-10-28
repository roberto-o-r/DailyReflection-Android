package com.isscroberto.dailyreflectionandroid.reflectionssaved;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isscroberto.dailyreflectionandroid.R;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;
import com.isscroberto.dailyreflectionandroid.reflectiondetail.ReflectionDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ReflectionsSavedActivity extends AppCompatActivity implements ReflectionsSavedContract.View {

    //----- Bindings.
    @BindView(R.id.list_reflections)
    RecyclerView listReflections;

    private ReflectionsSavedContract.Presenter mPresenter;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmResults<Reflection> mReflections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflections_saved);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reflections Saved");

        // Setup recycler view.
        listReflections.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listReflections.setLayoutManager(mLayoutManager);

        // Create the presenter
        new ReflectionsSavedPresenter(new ReflectionLocalDataSource(), this);
        mPresenter.start();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    public void setPresenter(ReflectionsSavedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showReflections(RealmResults<Reflection> reflections) {
        // Setup recycler view adapter.
        mReflections = reflections;
        mAdapter = new ReflectionAdapter(this, mReflections);
        listReflections.setAdapter(mAdapter);
    }

    private static class ReflectionAdapter extends RealmRecyclerViewAdapter<Reflection, ReflectionAdapter.ViewHolder> {

        private Context mContext;

        public ReflectionAdapter(Context context, RealmResults<Reflection> reflections) {
            super(reflections, true);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reflection, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Reflection reflection = getItem(position);
            holder.textTitle.setText(reflection.getTitle());
            holder.textPreview.setText(getExcerpt(reflection.getDescription()));

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textTitle;
            public TextView textPreview;

            public ViewHolder(View v) {
                super(v);
                textTitle = (TextView) v.findViewById(R.id.text_title);
                textPreview = (TextView) v.findViewById(R.id.text_preview);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ReflectionDetailActivity.class);
                        intent.putExtra("id", getItem(getAdapterPosition()).getId());
                        intent.putExtra("title", getItem(getAdapterPosition()).getTitle());
                        intent.putExtra("description", getItem(getAdapterPosition()).getDescription());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        private String getExcerpt(String input) {
            String excerpt = input;
            if(excerpt.lastIndexOf(" ") > -1 && excerpt.length() > 99) {
                excerpt = excerpt.substring(0, 100);
                excerpt = excerpt.substring(0, excerpt.lastIndexOf(" "));
            }
            excerpt = excerpt + "...";
            return excerpt;
        }
    }
}
