<template>
  <div class="notfound page-enter">
    <div class="container">
      <div class="inner">
        <div class="code mono">404</div>
        <span class="eyebrow">PAGE NOT FOUND</span>
        <h1>这个页面不存在</h1>
        <p>
          地址可能已经变更，或者文章被下线了。可以回到首页，或者用搜索找你需要的内容。
        </p>
        <div class="actions">
          <router-link to="/" class="btn primary">回到首页</router-link>
          <router-link to="/search" class="btn ghost">搜索文章</router-link>
        </div>
        <div class="quick">
          <span class="mono">POPULAR</span>
          <router-link to="/articles">全部文章</router-link>
          <router-link to="/archive">归档</router-link>
          <router-link to="/guestbook">留言板</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.notfound {
  padding: 70px 0 40px;
}

.inner {
  max-width: 560px;
  margin: 0 auto;
  padding: 48px 32px;
  border: 1px dashed var(--line);
  border-radius: 16px;
  background:
    radial-gradient(420px 220px at 50% 0%, rgba(62, 231, 255, 0.06), transparent 70%),
    rgba(13, 21, 32, 0.35);
  text-align: center;
}

.code {
  margin-bottom: 4px;
  font-family: var(--mono);
  font-size: clamp(64px, 12vw, 96px);
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.04em;
  background: linear-gradient(100deg, var(--cyan) 10%, #b6a4ff 60%, var(--violet));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.eyebrow {
  display: block;
  margin: 10px 0 8px;
}

h1 {
  font-size: 24px;
  letter-spacing: -0.02em;
}

p {
  margin: 10px auto 22px;
  max-width: 34em;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.8;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn {
  display: inline-flex;
  align-items: center;
  height: 38px;
  padding: 0 20px;
  border-radius: var(--radius);
  font-size: 13px;

  &.primary {
    border: 1px solid rgba(62, 231, 255, 0.45);
    background: linear-gradient(180deg, #54ecff, #25cfe8);
    color: #04121a;
    font-weight: 600;

    &:hover {
      filter: brightness(1.07);
      text-decoration: none;
    }
  }

  &.ghost {
    border: 1px solid var(--line);
    color: var(--muted);

    &:hover {
      color: var(--text);
      border-color: var(--line-strong);
      text-decoration: none;
    }
  }
}

.quick {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-top: 26px;
  padding-top: 18px;
  border-top: 1px solid var(--line);

  .mono {
    color: var(--faint);
    font-size: 9px;
    letter-spacing: 0.12em;
  }

  a {
    color: var(--muted);
    font-size: 11.5px;

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }
}
</style>
