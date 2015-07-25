package kr.edcan.neoanime;

import android.content.Context;

/**
 * Created by Junseok on 2015-04-16.
 */
public class CData {
    private String m_linktext;

    public CData(Context context, String p_linktext) {
        m_linktext = p_linktext;
    }

    public String getLink() {
        return m_linktext;
    }

}
