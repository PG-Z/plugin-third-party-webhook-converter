<script lang="ts" setup>
import { Toast, VButton, VModal, VSpace } from "@halo-dev/components";
import { computed, nextTick, ref, watch } from "vue";
import { axiosInstance } from "@halo-dev/api-client";
import cloneDeep from "lodash.clonedeep";
import type { WebhookGroup } from "@/types";
import { plugin as FormKit } from '@formkit/vue';

const props = withDefaults(
  defineProps<{
    visible: boolean;
    group?: WebhookGroup;
  }>(),
  {
    visible: false,
    group: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", visible: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: WebhookGroup = {
  apiVersion: "webhook.third.aiheiyo.top/v1alpha1",
  kind: "WebhookGroup",
  metadata: {
    name: "",
    generateName: "webhook-group-",
  },
  spec: {
    displayName: "",
    priority: 0,
  },
};

const formState = ref<WebhookGroup>(cloneDeep(initialFormState));
const saving = ref(false);
const formVisible = ref(false);

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑分组" : "新建分组";
});

const annotationsFormRef = ref();

const handleCreateOrUpdateGroup = async () => {
  annotationsFormRef.value?.handleSubmit();
  await nextTick();

  const { customAnnotations, annotations, customFormInvalid, specFormInvalid } =
    annotationsFormRef.value || {};
  if (customFormInvalid || specFormInvalid) {
    return;
  }

  formState.value.metadata.annotations = {
    ...annotations,
    ...customAnnotations,
  };

  try {
    saving.value = true;
    if (isUpdateMode.value) {
      await axiosInstance.put(
        `/apis/webhook.third.aiheiyo.top/v1alpha1/webhookgroups/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      await axiosInstance.post(
        "/apis/webhook.third.aiheiyo.top/v1alpha1/webhookgroups",
        formState.value
      );
    }

    Toast.success("保存成功");

    onVisibleChange(false);
  } catch (e) {
    console.error("Failed to create map group", e);
  } finally {
    saving.value = false;
  }
};

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      if (props.group) formState.value = cloneDeep(props.group);
      formVisible.value = true;
    } else {
      setTimeout(() => {
        formVisible.value = false;
        formState.value = cloneDeep(initialFormState);
      }, 200);
    }
  }
);
</script>
<template>
  <VModal
    :visible="visible"
    :width="600"
    :title="modalTitle"
    @update:visible="onVisibleChange"
  >
    <FormKit
      v-if="formVisible"
      id="link-group-form"
      v-model="formState.spec"
      name="link-group-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      @submit="handleCreateOrUpdateGroup"
    >
      <div class="md:grid md:grid-cols-4 md:gap-6">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base font-medium text-gray-900"> 常规 </span>
          </div>
        </div>
        <div class="mt-5 divide-y divide-gray-100 md:col-span-3 md:mt-0">
          <FormKit
            name="displayName"
            label="分组名称"
            type="text"
            validation="required"
          ></FormKit>
        </div>
      </div>
    </FormKit>

    <div class="py-5">
      <div class="border-t border-gray-200"></div>
    </div>

    <div class="md:grid md:grid-cols-4 md:gap-6">
      <div class="md:col-span-1">
        <div class="sticky top-0">
          <span class="text-base font-medium text-gray-900"> 元数据 </span>
        </div>
      </div>
      <div class="mt-5 divide-y divide-gray-100 md:col-span-3 md:mt-0">
        <AnnotationsForm
          v-if="visible"
          :key="formState.metadata.name"
          ref="annotationsFormRef"
          :value="formState.metadata.annotations"
          kind="WebhookGroup"
          group="webhook.third.aiheiyo.top"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton
          :loading="saving"
          type="secondary"
          @click="$formkit.submit('link-group-form')"
        >
          提交
        </VButton>
        <VButton @click="onVisibleChange(false)"> 取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
