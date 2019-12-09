package dvpermyakov.historyquiz.links;

import android.content.Context;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.preferences.SharePreferences;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by dvpermyakov on 26.11.2016.
 */

public class BranchLink {
    public static void generateLink(final Context context) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);
        LinkProperties linkProperties = new LinkProperties()
                .setChannel("vk")
                .setFeature("sharing")
                .setCampaign("testResult");
        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    SharePreferences.setSharedVKUrl(context, url);
                }
            }
        });
    }
}
