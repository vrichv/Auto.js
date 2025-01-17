package org.myaj.myaj.external.tile;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.stardust.view.accessibility.NodeInfo;

import org.myaj.myaj.ui.floating.FullScreenFloatyWindow;
import org.myaj.myaj.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LayoutBoundsTile extends LayoutInspectTileService {
    @Override
    protected FullScreenFloatyWindow onCreateWindow(NodeInfo capture) {
        return new LayoutBoundsFloatyWindow(capture) {
            @Override
            public void close() {
                super.close();
                inactive();
            }
        };
    }
}
