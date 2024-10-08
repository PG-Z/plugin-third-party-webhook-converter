<script lang="ts" setup>
import {Toast, VButton, VModal, VSpace} from "@halo-dev/components";
import {inject, ref, computed, nextTick, watch, type Ref} from "vue";
import type {Webhook} from "@/types";
import { axiosInstance } from "@halo-dev/api-client";
import cloneDeep from "lodash.clonedeep";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    link?: Webhook;
  }>(),
  {
    visible: false,
    link: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: Webhook = {
  metadata: {
    name: "",
    generateName: "map-",
  },
  spec: {
    displayName: "",
    webhookType: "",
    convertType: "",
    webhookSign: "",
    groupName: "",
  },
  kind: "Map",
  apiVersion: "map.aiheiyo.top/v1alpha1",
};

const formState = ref<Webhook>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);

const groupQuery = inject<Ref<string>>("groupQuery", ref(""));

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑地图" : "新建地图";
});

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

const handleResetForm = () => {
  formState.value = cloneDeep(initialFormState);
};

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      formVisible.value = true;
    } else {
      setTimeout(() => {
        formVisible.value = false;
        handleResetForm();
      }, 200);
    }
  }
);

watch(
  () => props.link,
  (link) => {
    if (link) {
      formState.value = cloneDeep(link);
    }
  }
);

const annotationsFormRef = ref();

const handleSaveLink = async () => {
  annotationsFormRef.value?.handleSubmit();
  await nextTick();

  const {customAnnotations, annotations, customFormInvalid, specFormInvalid} =
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
      await axiosInstance.put<Webhook>(
        `/apis/webhook.third.aiheiyo.top/v1alpha1/webhooks/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      formState.value.spec.groupName = groupQuery.value;
      await axiosInstance.post<Webhook>(
        `/apis/webhook.third.aiheiyo.top/v1alpha1/webhooks`,
        formState.value
      );
    }

    Toast.success("保存成功");

    onVisibleChange(false);
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
};
</script>
<template>
  <VModal
    :title="modalTitle"
    :visible="visible"
    :width="650"
    @update:visible="onVisibleChange"
  >
    <template #actions>
      <slot name="append-actions"/>
    </template>

    <FormKit
      v-if="formVisible"
      id="link-form"
      v-model="formState.spec"
      name="link-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      @submit="handleSaveLink"
    >
      <div class="md:grid md:grid-cols-4 md:gap-6">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base font-medium text-gray-900"> 常规 </span>
          </div>
        </div>
        <div class="mt-5 divide-y divide-gray-100 md:col-span-3 md:mt-0">
<!--          <FormKit
            type="postSelect"
            validation="required"
            name="post"
            label="文章"
          ></FormKit>
          <FormKit type="attachment" name="logo" label="Logo"></FormKit>-->
<!--          <FormKit type="textarea" name="description" label="描述"></FormKit>-->
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
          kind="Link"
          group="map.aiheiyo.top"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton
          :loading="saving"
          type="secondary"
          @click="$formkit.submit('link-form')"
        >
          提交
        </VButton>
        <VButton @click="onVisibleChange(false)">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
