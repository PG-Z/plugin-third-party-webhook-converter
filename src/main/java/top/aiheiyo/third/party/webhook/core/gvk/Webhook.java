package top.aiheiyo.third.party.webhook.core.gvk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;
import top.aiheiyo.third.party.webhook.core.enums.ConvertType;
import top.aiheiyo.third.party.webhook.core.enums.WebhookType;
import java.util.List;

/**
 * Description: Webhook
 *
 * @author : evan  Date: 2024/8/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "webhook.third.aiheiyo.top", version = "v1alpha1",
        kind = "Webhook", plural = "webhooks", singular = "webhook")
public class Webhook extends AbstractExtension {

    private WebhookSpec spec;

    @Data
    public static class WebhookSpec {

        private String displayName;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private WebhookType webhookType;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private ConvertType convertType;

        @Schema(description = "Webhook sign")
        private String webhookSign;

        private String description;

        private Integer priority;

        @Schema(description = "Webhook group name")
        private String groupName;
    }

    public String groupName() {
        return this.getSpec().getGroupName();
    }

    public static String groupName(List<Webhook> webhooks) {
        if (CollectionUtils.isEmpty(webhooks)) {
            return null;
        }
        return webhooks.get(0).getSpec().getGroupName();
    }
}
