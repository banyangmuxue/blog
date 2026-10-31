<template>
  <section class="page-hero container">
    <nav class="crumbs" aria-label="面包屑">
      <router-link to="/">首页</router-link>
      <span aria-hidden="true">/</span>
      <router-link v-if="parent" :to="parent.to">{{ parent.label }}</router-link>
      <span v-if="parent" aria-hidden="true">/</span>
      <span class="current">{{ title }}</span>
    </nav>

    <div class="head">
      <div class="text">
        <span v-if="eyebrow" class="eyebrow">{{ eyebrow }}</span>
        <h1>{{ title }}</h1>
        <p v-if="description">{{ description }}</p>
      </div>
      <div v-if="$slots.actions" class="actions">
        <slot name="actions" />
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: '' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  parent: { type: Object, default: null }
})
</script>

<style scoped lang="scss">
.page-hero {
  padding-top: 44px;
  padding-bottom: 26px;
}

.crumbs {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 20px;
  font-family: var(--mono);
  font-size: 10.5px;
  letter-spacing: 0.05em;
  color: var(--faint);

  a {
    color: var(--faint);

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }

  .current {
    color: var(--muted);
  }
}

.head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
}

.text {
  .eyebrow {
    display: block;
    margin-bottom: 10px;
  }

  h1 {
    font-size: clamp(28px, 3.4vw, 38px);
    font-weight: 700;
    letter-spacing: -0.03em;
    line-height: 1.15;
  }

  p {
    max-width: 46em;
    margin-top: 10px;
    color: var(--muted);
    font-size: 13.5px;
    line-height: 1.8;
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: none;
}

@media (max-width: 768px) {
  .page-hero {
    padding-top: 30px;
  }

  .head {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }
}
</style>
