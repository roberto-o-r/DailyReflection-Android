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
import com.isscroberto.dailyreflectionandroid.databinding.ActivityReflectionsSavedBinding;
import com.isscroberto.dailyreflectionandroid.reflectiondetail.ReflectionDetailActivity;

import javax.annotation.Nonnull;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ReflectionsSavedActivity extends AppCompatActivity implements ReflectionsSavedContract.View {

    private ReflectionsSavedContract.Presenter presenter;
    private ActivityReflectionsSavedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding.
        binding = ActivityReflectionsSavedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Setup toolbar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reflections Saved");
        }

        // Setup recycler view.
        binding.listReflections.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.listReflections.setLayoutManager(mLayoutManager);

        // Create the presenter
        presenter = new ReflectionsSavedPresenter(new ReflectionLocalDataSource());
        presenter.takeView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    public void showReflections(RealmResults<Reflection> reflections) {
        // Setup recycler view adapter.
        RecyclerView.Adapter<ReflectionAdapter.ViewHolder> mAdapter = new ReflectionAdapter(this, reflections);
        binding.listReflections.setAdapter(mAdapter);
    }

    private static class ReflectionAdapter extends RealmRecyclerViewAdapter<Reflection, ReflectionAdapter.ViewHolder> {

        private final Context mContext;

        public ReflectionAdapter(Context context, RealmResults<Reflection> reflections) {
            super(reflections, true);
            mContext = context;
        }

        @Override
        public @Nonnull
        ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reflection, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
            final Reflection reflection = getItem(position);
            if (reflection != null) {
                holder.textTitle.setText(reflection.getTitle());
                holder.textPreview.setText(getExcerpt(reflection.getDescription()));
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textTitle;
            public TextView textPreview;

            public ViewHolder(View v) {
                super(v);
                textTitle = v.findViewById(R.id.text_title);
                textPreview = v.findViewById(R.id.text_preview);

                v.setOnClickListener(v1 -> {
                    Intent intent = new Intent(v1.getContext(), ReflectionDetailActivity.class);
                    Reflection reflection = getItem(getAdapterPosition());
                    if(reflection != null) {
                        intent.putExtra("id", reflection.getId());
                        intent.putExtra("title", reflection.getTitle());
                        intent.putExtra("description", reflection.getDescription());
                    }
                    mContext.startActivity(intent);
                });
            }
        }

        private String getExcerpt(String input) {
            String excerpt = input;
            if (excerpt.lastIndexOf(" ") > -1 && excerpt.length() > 99) {
                excerpt = excerpt.substring(0, 100);
                excerpt = excerpt.substring(0, excerpt.lastIndexOf(" "));
            }
            excerpt = excerpt + "...";
            return excerpt;
        }
    }
}
