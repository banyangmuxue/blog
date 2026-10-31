<template>
  <ul class="comment-list">
    <li v-for="comment in comments" :key="comment.id" class="comment">
      <div class="head">
        <span class="avatar" aria-hidden="true">{{ initial(comment.nickname) }}</span>
        <div class="who">
          <strong>{{ comment.nickname }}</strong>
          <span class="mono time">{{ relativeTime(comment.createdAt) }}</span>
        </div>
      </div>

      <p class="text">{{ comment.content }}</p>

      <div v-if="comment.reply" class="reply">
        <span class="badge">博主</span>
        <p>{{ comment.reply.content }}</p>
        <span class="mono time">{{ relativeTime(comment.reply.createdAt) }}</span>
      </div>
    </li>
  </ul>
</template>

<script setup>
import { relativeTime } from '@/utils/format'

defineProps({
  comments: { type: Array, default: () => [] }
})

function initial(name = '') {
  return name.slice(0, 1).toUpperCase() || '?'
}
</script>

<style scoped lang="scss">
.comment-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.comment + .comment {
  margin-top: 2px;
  border-top: 1px dashed var(--line);
}

.comment {
  padding: 16px 0;
}

.head {
  display: flex;
  align-items: center;
  gap: 9px;
}

.avatar {
  display: grid;
  width: 30px;
  height: 30px;
  flex: none;
  place-items: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.28), rgba(139, 92, 246, 0.36));
  color: var(--text);
  font-family: var(--mono);
  font-size: 11px;
}

.who {
  display: flex;
  align-items: baseline;
  gap: 8px;

  strong {
    font-size: 12.5px;
    font-weight: 600;
  }
}

.time {
  color: var(--faint);
  font-size: 9.5px;
}

.text {
  margin-top: 9px;
  padding-left: 39px;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply {
  margin: 12px 0 0 39px;
  padding: 9px 12px 10px 13px;
  border-left: 1px solid rgba(62, 231, 255, 0.35);
  border-radius: 0 8px 8px 0;
  background: rgba(62, 231, 255, 0.045);

  .badge {
    display: inline-block;
    padding: 0 6px;
    border: 1px solid rgba(62, 231, 255, 0.32);
    border-radius: 4px;
    color: var(--cyan);
    font-family: var(--mono);
    font-size: 9px;
  }

  p {
    margin-top: 5px;
    color: var(--muted);
    font-size: 12.5px;
    line-height: 1.7;
    white-space: pre-wrap;
  }

  .time {
    display: inline-block;
    margin-top: 4px;
  }
}

@media (max-width: 640px) {
  .text,
  .reply {
    padding-left: 0;
    margin-left: 0;
  }

  .text {
    padding-top: 4px;
  }
}
</style>
