package cc.blynk.server.core.model.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.utils.IntArray;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.05.18.
 */
public class OtaDTO {

    public final int orgId;

    public final int productId;

    public final String pathToFirmware;

    public final String firmwareOriginalFileName;

    public final int[] deviceIds;

    public final String title;

    public final FirmwareInfo firmwareInfo;

    public final int attemptsLimit;

    public final boolean isSecure;

    @JsonCreator
    public OtaDTO(@JsonProperty("orgId") int orgId,
                  @JsonProperty("productId") int productId,
                  @JsonProperty("pathToFirmware") String pathToFirmware,
                  @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                  @JsonProperty("deviceIds") int[] deviceIds,
                  @JsonProperty("title") String title,
                  @JsonProperty("firmwareInfo") FirmwareInfo firmwareInfo,
                  @JsonProperty("attemptsLimit") int attemptsLimit,
                  @JsonProperty("isSecure") boolean isSecure) {
        this.orgId = orgId;
        this.productId = productId;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.deviceIds = deviceIds == null ? IntArray.EMPTY_INTS : deviceIds;
        this.title = title;
        this.firmwareInfo = firmwareInfo;
        this.attemptsLimit = attemptsLimit == 0 ? 3 : attemptsLimit;
        this.isSecure = isSecure;
    }

    public boolean isNotValid() {
        return pathToFirmware == null || firmwareInfo == null || firmwareOriginalFileName == null
                || pathToFirmware.isEmpty() || isDevicesEmpty();
    }

    public boolean isDevicesEmpty() {
        return deviceIds == null || deviceIds.length == 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
