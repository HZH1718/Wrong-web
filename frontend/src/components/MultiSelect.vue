<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  // 选项列表：[{ value, label }]
  options: { type: Array, default: () => [] },
  // 已选中的值数组
  modelValue: { type: Array, default: () => [] },
  // 未选中任何项时的占位文案
  placeholder: { type: String, default: '全部' }
})

const emit = defineEmits(['update:modelValue'])

const open = ref(false)
const root = ref(null)

const selectedLabels = computed(() =>
  props.options
    .filter((option) => props.modelValue.includes(option.value))
    .map((option) => option.label)
)

function isSelected(value) {
  return props.modelValue.includes(value)
}

function toggle(value) {
  const next = isSelected(value)
    ? props.modelValue.filter((item) => item !== value)
    : [...props.modelValue, value]

  emit('update:modelValue', next)
}

function clear() {
  emit('update:modelValue', [])
}

function onDocumentClick(event) {
  if (root.value && !root.value.contains(event.target)) {
    open.value = false
  }
}

onMounted(() => document.addEventListener('click', onDocumentClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocumentClick))
</script>

<template>
  <div ref="root" class="multi-select">
    <button
      class="multi-select-trigger"
      type="button"
      :aria-expanded="open"
      @click="open = !open"
    >
      <span v-if="selectedLabels.length === 0" class="multi-select-placeholder">
        {{ placeholder }}
      </span>

      <span v-else class="multi-select-tags">
        <span v-for="label in selectedLabels" :key="label" class="badge badge-green">
          {{ label }}
        </span>
      </span>

      <span class="multi-select-caret" :class="{ 'is-open': open }">▾</span>
    </button>

    <div v-if="open" class="multi-select-panel">
      <div class="multi-select-actions">
        <span class="multi-select-count">已选 {{ modelValue.length }} 项</span>
        <button class="multi-select-clear" type="button" @click="clear">清空</button>
      </div>

      <div v-if="options.length === 0" class="multi-select-empty">暂无可选项</div>

      <label v-for="option in options" :key="option.value" class="multi-select-option">
        <input
          type="checkbox"
          :checked="isSelected(option.value)"
          @change="toggle(option.value)"
        />
        <span>{{ option.label }}</span>
      </label>
    </div>
  </div>
</template>
