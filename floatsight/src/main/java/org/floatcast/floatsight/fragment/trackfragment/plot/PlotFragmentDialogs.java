package org.floatcast.floatsight.fragment.trackfragment.plot;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.CappedTrackPointValueProvider;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;

public final class PlotFragmentDialogs {

    static final int GLIDE_CAP_MAX = 16;
    static final int GLIDE_CAP_MIN = 2;

    private PlotFragmentDialogs() {
        throw new AssertionError("No.");
    }

    public static void showGlideCapDialog(PlotFragment plotFragment, CappedTrackPointValueProvider cappedTrackPointValueProvider) {
        if(plotFragment.getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(plotFragment.getContext())
                .setTitle(R.string.glide_cap_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(plotFragment.getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.glide_cap_dialog, frameView);
        TextView seekBarTextView = ((TextView) dialoglayout.findViewById(R.id.text_view_glide_cap));
        SeekBar glideCapSeekBar = ((SeekBar) dialoglayout.findViewById(R.id.seekbar_glide_cap));
        glideCapSeekBar.setMax(GLIDE_CAP_MAX);
        glideCapSeekBar.setProgress((int) cappedTrackPointValueProvider.getCapYValueAt());
        seekBarTextView.setText(ChartDataSetProperties.GLIDE_FORMAT.format(cappedTrackPointValueProvider.getCapYValueAt()));
        glideCapSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                plotFragment.setNewGlideCapValue(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < GLIDE_CAP_MIN) {
                    glideCapSeekBar.setProgress(GLIDE_CAP_MIN);
                } else {
                    seekBarTextView.setText(ChartDataSetProperties.GLIDE_FORMAT.format(progress));
                }
            }
        });
        dialog.show();
    }
}
