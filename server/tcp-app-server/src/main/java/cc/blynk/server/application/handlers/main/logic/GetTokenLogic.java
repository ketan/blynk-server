package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.TokenManager;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.GET_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetTokenLogic {

    private final TokenManager tokenManager;
    private final DeviceDao deviceDao;

    public GetTokenLogic(Holder holder) {
        this.tokenManager = holder.tokenManager;
        this.deviceDao = holder.deviceDao;
    }

    //todo this old and outdated handle just for back compatibility
    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        var dashBoardIdString = message.body;

        String[] parts;
        if (dashBoardIdString.contains(StringUtils.BODY_SEPARATOR_STRING)) {
            parts = StringUtils.split2(dashBoardIdString);
        } else if (dashBoardIdString.contains("-")) {
            parts = StringUtils.split2Device(dashBoardIdString);
        } else {
            parts = new String[] {dashBoardIdString};
        }

        var dashId = Integer.parseInt(parts[0]);
        var deviceId = parts.length == 1 ? 0 : Integer.parseInt(parts[1]);

        var dash = user.profile.getDashByIdOrThrow(dashId);

        var device = dash.getDeviceById(deviceId);
        var token = device == null ? null : device.token;

        //if token not exists. generate new one
        if (token == null) {
            //todo back compatibility code. remove in future
            device = new Device(deviceId, "ESP8266", "ESP8266");
            dash.devices = new Device[] {device};

            token = TokenGeneratorUtil.generateNewToken();
            tokenManager.assignToken(user, dash, device, token);
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(GET_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
