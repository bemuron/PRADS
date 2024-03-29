package bruca.prads.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by Emo on 7/13/2017.
 */


public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());

        return(picker);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        if (Build.VERSION.SDK_INT >= 23){
            picker.setHour(lastHour);
            picker.setMinute(lastMinute);
        }else{
            picker.setCurrentHour(lastHour);
            picker.setCurrentMinute(lastMinute);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            if (Build.VERSION.SDK_INT >= 23){
                lastHour = picker.getHour();
                lastMinute = picker.getMinute();
            }else{
                lastHour = picker.getCurrentHour();
                lastMinute = picker.getCurrentMinute();
            }

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }
}
