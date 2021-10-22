package org.myaj.myaj.tool;

import android.content.Context;
import android.widget.Toast;

import org.myaj.myaj.R;
import com.stardust.util.IntentUtil;

/**
 * Created by Stardust on 2017/4/12.
 */

public class IntentTool {

    public static void browse(Context context, String url){
        if (!IntentUtil.browse(context, url)) {
            Toast.makeText(context, R.string.text_no_brower, Toast.LENGTH_SHORT).show();
        }
    }
}
