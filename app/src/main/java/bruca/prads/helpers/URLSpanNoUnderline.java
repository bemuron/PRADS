package bruca.prads.helpers;

import android.text.TextPaint;
import android.text.style.URLSpan;

import bruca.prads.R;


/**
 * Created by Emo on 7/14/2017.
 */


public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String p_Url) {
        super(p_Url);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
        //p_DrawState.setColor(R.color.from);
    }
}
