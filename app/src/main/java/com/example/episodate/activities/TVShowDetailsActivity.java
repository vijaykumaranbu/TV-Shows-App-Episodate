package com.example.episodate.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.episodate.R;
import com.example.episodate.adapters.EpisodeAdapter;
import com.example.episodate.adapters.ImageSliderAdapter;
import com.example.episodate.adapters.SeasonAdapter;
import com.example.episodate.databinding.ActivityTVShowDetailsBinding;
import com.example.episodate.databinding.LayoutBottomSheetEpisodeBinding;
import com.example.episodate.listeners.SeasonListener;
import com.example.episodate.models.Episode;
import com.example.episodate.models.Season;
import com.example.episodate.models.TVShow;
import com.example.episodate.models.TVShowDetails;
import com.example.episodate.responses.TVShowDetailsResponse;
import com.example.episodate.utilities.TempDataHelper;
import com.example.episodate.viewModels.MostPopularTVShowViewModel;
import com.example.episodate.viewModels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity implements SeasonListener {

    private ActivityTVShowDetailsBinding activityTVShowDetailsBinding;
    private TVShowDetailsViewModel viewModel;
    private BottomSheetDialog episodeBottomSheetDialog;
    private TVShowDetails tvShowDetails = new TVShowDetails();
    private TVShow tvShow;
    private boolean isAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTVShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_t_v_show_details);
        doInit();
    }

    private void doInit() {
        viewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTVShowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        activityTVShowDetailsBinding.imageBack.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.seasonsRecyclerView.setHasFixedSize(true);
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTVShowDetails();
        checkIsAvailableInWatchlist();
    }

    private void checkIsAvailableInWatchlist(){
       new CompositeDisposable().add(viewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(tvShow1 -> {
           isAvailableInWatchlist = true;
           activityTVShowDetailsBinding.imageAddWatchlist.setImageResource(R.drawable.ic_remove_watchlist);
           activityTVShowDetailsBinding.imageAddWatchlist.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorTextOthers));
       }));
    }

    private void getTVShowDetails() {
        String tvShowId = String.valueOf(tvShow.getId());
        activityTVShowDetailsBinding.setIsLoading(true);
        viewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            activityTVShowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                tvShowDetails = tvShowDetailsResponse.getTvShowDetails();
                if (tvShowDetails.getPictures() != null) {
                    loadImageSlider(tvShowDetails.getPictures());
                }
                activityTVShowDetailsBinding.setTvShowURL(tvShowDetails.getImagePath());
                activityTVShowDetailsBinding.tvShowImage.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(tvShowDetails.getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                        )
                );
                activityTVShowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.textReadMore.setOnClickListener(view -> {
                    if (activityTVShowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                        activityTVShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                        activityTVShowDetailsBinding.textReadMore.setText(R.string.read_less);
                    } else {
                        activityTVShowDetailsBinding.textDescription.setMaxLines(5);
                        activityTVShowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        activityTVShowDetailsBinding.textReadMore.setText(R.string.read_more);
                    }
                });
                activityTVShowDetailsBinding.setRating(
                        String.format(
                                Locale.getDefault(), "%.1f",
                                Double.parseDouble(tvShowDetails.getRating())
                        )
                );
                if (tvShowDetails.getGenres() != null)
                    activityTVShowDetailsBinding.setGenre(tvShowDetails.getGenres()[0]);
                else
                    activityTVShowDetailsBinding.setGenre("N/A");

                activityTVShowDetailsBinding.setRuntime(tvShowDetails.getRuntime() + " Min");
                activityTVShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.imageAddWatchlist.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowDetails.getUrl()));
                    startActivity(intent);
                });
                activityTVShowDetailsBinding.imageAddWatchlist.setOnClickListener(view -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if(isAvailableInWatchlist){
                        compositeDisposable.add(viewModel.removeTVShowFromWatchlist(tvShow)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            isAvailableInWatchlist = false;
                            TempDataHelper.IS_WATCHLIST_UPDATED = true;
                            activityTVShowDetailsBinding.imageAddWatchlist.setImageResource(R.drawable.ic_add_watchlist);
                            activityTVShowDetailsBinding.imageAddWatchlist.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorTextIcons));
                            Toast.makeText(getApplicationContext(),"Removed from watchlist",Toast.LENGTH_SHORT).show();
                            compositeDisposable.dispose();
                        }));
                    }
                    else{
                        compositeDisposable.add(viewModel.addToWatchlist(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(()->{
                                    TempDataHelper.IS_WATCHLIST_UPDATED = true;
                                    activityTVShowDetailsBinding.imageAddWatchlist.setImageResource(R.drawable.ic_remove_watchlist);
                                    activityTVShowDetailsBinding.imageAddWatchlist.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorTextOthers));
                                    Toast.makeText(getApplicationContext(),"added to watchlist",Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                    }
                });
                loadBasicTVShowDetails();
                loadSeason(tvShowDetails.getEpisodes(), tvShowDetails.getImagePath());
            }
        });
    }

    private void loadImageSlider(String[] pictures) {
        activityTVShowDetailsBinding.viewPager.setOffscreenPageLimit(1);
        activityTVShowDetailsBinding.viewPager.setAdapter(new ImageSliderAdapter(pictures));
        activityTVShowDetailsBinding.viewPager.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.sliderFadeEdge.setVisibility(View.VISIBLE);
        setUpSliderIndicator(pictures.length);

        activityTVShowDetailsBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setUpSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            activityTVShowDetailsBinding.sliderIndicator.addView(indicators[i]);
        }
        activityTVShowDetailsBinding.sliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTVShowDetailsBinding.sliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView currentIndicator = (ImageView) activityTVShowDetailsBinding.sliderIndicator.getChildAt(i);
            if (i == position) {
                currentIndicator.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.background_slider_indicator_active
                ));
            } else {
                currentIndicator.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.background_slider_indicator_inactive
                ));
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTVShowDetailsBinding.setTvShowName(tvShow.getName());
        activityTVShowDetailsBinding.setNetworkCountry(tvShow.getNetwork() + " (" + tvShow.getCountry() + ")");
        activityTVShowDetailsBinding.setStatus(tvShow.getStatus());
        activityTVShowDetailsBinding.setStartedDate("Started On : " + tvShow.getStartDate());
    }

    private void loadSeason(List<Episode> episodes, String imageURL) {
        List<Season> seasons = new ArrayList<>();
        String date = "";
        int i = 1;
        for (Episode episode : episodes) {
            if (Integer.parseInt(episode.getEpisode()) == 1) {
                date = episode.getAirDate().split(" ")[0];
                seasons.add(new Season(imageURL, "Season " + i, date));
                i++;
            }
        }
        if (!seasons.isEmpty()) {
            activityTVShowDetailsBinding.titleSeasons.setVisibility(View.VISIBLE);
            activityTVShowDetailsBinding.seasonsRecyclerView.setAdapter(
                    new SeasonAdapter(seasons, this)
            );
        }
    }

    private void showEpisodes(List<Episode> episodes) {
        episodeBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
        com.example.episodate.databinding.LayoutBottomSheetEpisodeBinding layoutBottomSheetEpisodeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(TVShowDetailsActivity.this),
                R.layout.layout_bottom_sheet_episode,
                findViewById(R.id.containerEpisode),
                false
        );

        episodeBottomSheetDialog.setContentView(layoutBottomSheetEpisodeBinding.getRoot());
        layoutBottomSheetEpisodeBinding.episodeRecyclerView.setAdapter(
                new EpisodeAdapter(episodes)
        );

        layoutBottomSheetEpisodeBinding.textTitle.setText(
                String.format("Season %s | %s", episodes.get(0).getSeason(), tvShow.getName())
        );

        layoutBottomSheetEpisodeBinding.imageClose.setOnClickListener(view1 -> episodeBottomSheetDialog.dismiss());

        FrameLayout frameLayout = episodeBottomSheetDialog.findViewById(
                com.google.android.material.R.id.design_bottom_sheet
        );
        if (frameLayout != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        episodeBottomSheetDialog.show();
    }


    @Override
    public void onClickSeason(Season season) {

            List<Episode> episodes = new ArrayList<>();
            for (Episode episode : tvShowDetails.getEpisodes()) {
                if (Integer.parseInt(episode.getSeason()) == Integer.parseInt(season.getTitle().split(" ")[1])) {
                    episodes.add(episode);
                }
            }
            if (!episodes.isEmpty())
                showEpisodes(episodes);
    }
}