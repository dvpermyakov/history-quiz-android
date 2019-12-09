package dvpermyakov.historyquiz.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.fragments.HistoryInfoFragment;
import dvpermyakov.historyquiz.fragments.InstructionDoneFragment;
import dvpermyakov.historyquiz.fragments.InstructionFragment;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 14.09.2016.
 */
public class InstructionAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public InstructionAdapter(FragmentManager manager, View view, boolean external) {
        super(manager);

        fragments = new ArrayList<>();
        fragments.add(getInstructionFragment(view, R.drawable.instructions_1_v3, "Выбирайте период истории России"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_2_v3, "Читайте статью о выбранном периоде"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_3_v3, "Переходите по ссылкам, чтобы открывать новые статьи"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_4_v3, "После перехода узнавайте кратко об открытой статье"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_5_v3, "Выбирайте открытые статьи, чтобы их прочитать"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_6_v3, "После прочтения проходите тестирование"));
        fragments.add(getInstructionFragment(view, R.drawable.instructions_8_v3, "Выполняйте тесты статей последовательно"));
        fragments.add(getInstructionDoneFragment(view, external));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public Fragment getInstructionFragment(View view, int imageId, String header) {
        Bundle args = new Bundle();
        args.putInt(IntentStrings.INTENT_INSTRUCTION_IMAGE_ID_PARAM, imageId);
        args.putString(IntentStrings.INTENT_INSTRUCTION_HEADER_PARAM, header);
        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);
        fragment.setNextView(view);
        return fragment;
    }

    public Fragment getInstructionDoneFragment(View view, boolean external) {
        Bundle args = new Bundle();
        args.putBoolean(IntentStrings.INTENT_INSTRUCTIONS_EXTERNAL_PARAM, external);
        InstructionDoneFragment fragment = new InstructionDoneFragment();
        fragment.setArguments(args);
        fragment.setNextView(view);
        return fragment;
    }
}
