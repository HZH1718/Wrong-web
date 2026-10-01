<script setup>
import { computed } from 'vue'
import katex from 'katex'
import 'katex/dist/katex.min.css'

const props = defineProps({
  /** 可能包含 LaTeX 的原始文本 */
  text: {
    type: [String, Number],
    default: ''
  }
})

/* 优先匹配行间公式 $$...$$，其次匹配行内公式 $...$（不跨行） */
const MATH_PATTERN = /\$\$([\s\S]+?)\$\$|\$([^$\n]+?)\$/g

/**
 * 把原始文本切成「纯文本」与「公式」片段。
 * 纯文本片段由模板插值渲染（Vue 会自动转义，杜绝 XSS），
 * 只有公式片段交给 KaTeX 生成 HTML。
 */
function toSegments(raw) {
  const source = raw === null || raw === undefined ? '' : String(raw)
  const segments = []
  let cursor = 0
  let match

  MATH_PATTERN.lastIndex = 0

  while ((match = MATH_PATTERN.exec(source)) !== null) {
    if (match.index > cursor) {
      segments.push({ type: 'text', value: source.slice(cursor, match.index) })
    }

    const isBlock = match[1] !== undefined

    segments.push({
      type: 'math',
      displayMode: isBlock,
      value: isBlock ? match[1] : match[2]
    })

    cursor = match.index + match[0].length
  }

  if (cursor < source.length) {
    segments.push({ type: 'text', value: source.slice(cursor) })
  }

  return segments
}

const segments = computed(() =>
  toSegments(props.text).map((segment) => {
    if (segment.type === 'text') {
      return segment
    }

    return {
      type: 'math',
      displayMode: segment.displayMode,
      html: katex.renderToString(segment.value, {
        displayMode: segment.displayMode,
        throwOnError: false,
        output: 'html'
      })
    }
  })
)
</script>

<template>
  <span class="math-text">
    <template v-for="(segment, index) in segments" :key="index">
      <span v-if="segment.type === 'text'">{{ segment.value }}</span>
      <span
        v-else
        class="math-text__formula"
        :class="{ 'is-block': segment.displayMode }"
        v-html="segment.html"
      ></span>
    </template>
  </span>
</template>

<style scoped>
.math-text {
  word-break: break-word;
}

.math-text__formula.is-block {
  display: block;
  margin: 10px 0;
  overflow-x: auto;
  overflow-y: hidden;
}
</style>
