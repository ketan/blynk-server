package cc.blynk.server.web.session;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.StateHolderBase;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder extends StateHolderBase {

    private static final int NO_DEVICE = -1;

    public int selectedDeviceId;

    public WebAppStateHolder(User user) {
        super(user);
        this.selectedDeviceId = NO_DEVICE;
    }

    @Override
    public boolean contains(String sharedToken) {
        return true;
    }

    @Override
    public boolean isSameDevice(int deviceId) {
        return selectedDeviceId == deviceId;
    }

}
