package top.aiheiyo.third.party.webhook.core.gvk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * Description: WebhookGroup
 *
 * @author : evan  Date: 2024/8/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "webhook.third.aiheiyo.top", version = "v1alpha1", kind = "WebhookGroup", plural = "webhookgroups", singular = "webhookgroup")
public class WebhookGroup extends AbstractExtension {

    private WebhookGroupSpec spec;

    @Data
    public static class WebhookGroupSpec {

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String displayName;

        private Integer priority;

    }
}
