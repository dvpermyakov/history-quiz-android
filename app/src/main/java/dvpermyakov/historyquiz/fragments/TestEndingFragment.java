package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.VideoInfoActivity;
import dvpermyakov.historyquiz.adapter_models.CardViewInformationModel;
import dvpermyakov.historyquiz.adapters.CardViewInformationAdapter;
import dvpermyakov.historyquiz.adapters.VideoRecommendationAdapter;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.decorations.DividerItemDecoration;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;
import dvpermyakov.historyquiz.models.TestResultCategory;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 30.05.2016.
 */
public class TestEndingFragment extends Fragment {
    public interface OnRetryClickListener {
        void onRetryClick();
        void onContinueClick();
    }

    private Context context;
    private OnRetryClickListener listener;
    private Test test;
    private TestResult testResult;
    private HistoryEntity historyEntity;
    private VideoRecommendationAdapter videoRecommendationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        test = getArguments().getParcelable(IntentStrings.INTENT_TEST_PARAM);
        testResult = getArguments().getParcelable(IntentStrings.INTENT_TEST_RESULT_PARAM);
        historyEntity = getArguments().getParcelable(IntentStrings.INTENT_HISTORY_ENTITY_PARAM);
        listener = (OnRetryClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_ending, container, false);
        addCardTestInfo(view);
        Button startButton = (Button) view.findViewById(R.id.doneEndingStartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRetryClick();
            }
        });
        Button continueButton = (Button) view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onContinueClick();
            }
        });

        if (testResult.getResultCategory() == TestResultCategory.WIN) {
            continueButton.setText(context.getString(R.string.button_share) +  "  +" + CoinsTransaction.CoinsTransactionCategory.SHARE_TEST_RESULT.getValue());
        } else {
            continueButton.setText(continueButton.getText() + "  " + Math.abs(CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getValue()));
        }
        final Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_coins).getConstantState().newDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorBlackLight), PorterDuff.Mode.SRC_IN);
        drawable.setBounds(0, 0,
                (int) (drawable.getIntrinsicWidth() / 2.5),
                (int) (drawable.getIntrinsicHeight() / 2.5));
        continueButton.setCompoundDrawables(null, null, drawable, null);
        continueButton.setCompoundDrawablePadding(3);

        if (testResult.getResultCategory() == TestResultCategory.WIN && historyEntity.getCategory() != HistoryEntityCategory.VIDEO) {
            final List<Video> videos = DataBaseHelperFactory.getHelper().getVideoDao().getVideos(historyEntity);
            if (videos.size() > 0) {
                Set<String> channelIds = new HashSet<>();
                for (Video video : videos) {
                    channelIds.add(video.getChannelId());
                }
                List<VideoChannel> channels = new ArrayList<>();
                for (String id : channelIds) {
                    if (id != null) {
                        VideoChannel channel = DataBaseHelperFactory.getHelper().getVideoChannelDao().getById(id);
                        if (channel != null) {
                            channels.add(channel);
                        }
                    }
                }
                final Map<String, VideoChannel> channelMap = new HashMap<>();
                for (VideoChannel channel : channels) {
                    channelMap.put(channel.getId(), channel);
                }

                view.findViewById(R.id.videoRecommendationLayout).setVisibility(View.VISIBLE);
                RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerViewVideosRecommendation));
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                startActivity(new Intent(context, VideoInfoActivity.class)
                                        .putExtra(IntentStrings.INTENT_VIDEO_PARAM, videos.get(position))
                                        .putExtra(IntentStrings.INTENT_VIDEO_CHANNEL_PARAM, channelMap.get(videos.get(position).getChannelId())));

                            }
                        }));
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);
                videoRecommendationAdapter = new VideoRecommendationAdapter(context, videos, channelMap);
                recyclerView.setAdapter(videoRecommendationAdapter);
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoRecommendationAdapter != null) {
            videoRecommendationAdapter.refresh();
        }
    }

    private void addCardTestInfo(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTestEndingInfo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        TextView resultText = ((TextView)view.findViewById(R.id.tvTestResult));
        resultText.setText(testResult.getResultCategory().toString(getContext()));
        ImageView drawable = (ImageView) view.findViewById(R.id.resultIcon);
        if (testResult.getResultCategory() == TestResultCategory.WIN) {
            drawable.setImageResource(R.drawable.ic_check_circle);
            drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
        } else {
            drawable.setImageResource(R.drawable.ic_cancel);
            drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorWrongAnswer), PorterDuff.Mode.SRC_IN);
        }
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(getResources().getString(R.string.question_amount_test_result),
                String.format("%s/%s", testResult.getQuestionNumber(), test.getQuestionAmount()), null, null));
        list.add(new CardViewInformationModel( getResources().getString(R.string.time_amount_test_result),
                String.format("%s/%s", testResult.getSecondNumber(), test.getMaxSeconds()), null, null));
        list.add(new CardViewInformationModel(getResources().getString(R.string.mistake_amount_test_result),
                String.format("%s/%s", testResult.getMistakeNumber(), test.getMaxMistakes()), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(context, list);
        recyclerView.setAdapter(adapter);
    }
}
